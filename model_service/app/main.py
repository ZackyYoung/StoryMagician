# app/main.py
import logging
from io import BytesIO

from fastapi import FastAPI, HTTPException, Response
import uvicorn

from app.schemas.llm_schema import StoryboardRequest, StoryboardResponse
from app.schemas.text2img_schema import Text2ImageRequest
from app.services.llm_services import generate_storyboard
from app.services.sd_service import generate_image
from app.utils.logger import setup_logging

setup_logging()
logger = logging.getLogger(__name__)
app = FastAPI()

@app.post("/storyboard", response_model=StoryboardResponse)
def storyboard_api(req: StoryboardRequest):
    """
    根据故事文本 + 风格，生成分镜 JSON。
    """
    try:
        shots = generate_storyboard(req.story_text, req.style)
        return {"shots": shots}
    except Exception as e:
        logger.exception("LLM storyboard generation failed")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/text2img")
def text2image_api(req: Text2ImageRequest):
    """
    根据 prompt + style 生成关键帧图像（JPG）。
    """
    try:
        img = generate_image(req.prompt, style=req.style, width=req.width, height=req.height)
        buf = BytesIO()
        img.save(buf, format="JPEG")
        buf.seek(0)
        return Response(content=buf.getvalue(), media_type="image/jpeg")
    except Exception as e:
        logger.exception("Image generation failed")
        raise HTTPException(status_code=500, detail=str(e))
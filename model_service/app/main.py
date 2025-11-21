# app/main.py
import logging

from fastapi import FastAPI, HTTPException
import uvicorn

from app.schemas.llm_schema import StoryboardRequest, StoryboardResponse
from app.services.llm_services import generate_storyboard
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


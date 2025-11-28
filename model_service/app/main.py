# app/main.py
import logging
from io import BytesIO

from fastapi import FastAPI, HTTPException, Response, UploadFile, File, Form
from starlette.responses import FileResponse

from app.schemas.llm_schema import StoryboardRequest, StoryboardResponse
from app.schemas.text2img_schema import Text2ImageRequest
from app.services.llm_services import generate_storyboard
from app.services.sd_service import generate_image
from app.schemas.tts_schema import TTSRequest
from app.services.svd_services import generate_video_from_frames
from app.services.tts_services import cosyvoice_tts
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
        shots = generate_storyboard(req.story_title, req.story_text, req.style)
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

@app.post("/tts")
def tts_api(req: TTSRequest):
    """
    将旁白文本转换为语音 (mp3/wav)。
    """
    try:
        audio_bytes, mime = cosyvoice_tts(tts_text=req.tts_text, spk_id=req.spk_id)
        return Response(content=audio_bytes, media_type=mime)
    except Exception as e:
        logger.exception("TTS failed")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/img2video")
def image2video_api(images: list[UploadFile] = File(...),
    transition: str = Form("fade"),
    fps: int = Form(10)):
    output_path = generate_video_from_frames(images, transition, fps)
    return FileResponse(output_path, media_type="video/mp4")

import requests
import logging
from app import config

logger = logging.getLogger(__name__)

def cosyvoice_tts(tts_text: str, spk_id: str = "english man"):
    url = 'http://localhost:50000/inference_sft'
    payload = {"tts_text": tts_text, "spk_id": spk_id, }
    r = requests.post(url, json=payload, timeout=60)
    content_type = r.headers.get("Content-Type", "audio/wav" )
    return r.content, content_type

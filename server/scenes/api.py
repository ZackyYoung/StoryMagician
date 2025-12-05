import requests
import uuid
import mimetypes
from django.conf import settings

base_request_url = settings.BASE_REQUEST_URL
base_source_url = settings.BASE_SOURCE_URL
static_root = settings.STATIC_ROOT
image_dir = settings.IMAGE_DIR
audio_dir = settings.AUDIO_DIR


def text_to_image(prompt:str, style:str):
    request_url = base_request_url + "/text2img"
    payload = {
        "prompt": prompt,
        "style": style
    }
    response = requests.post(request_url, json=payload, timeout=600)
    
    content_type = response.headers.get("Content-Type", "")
    ext = mimetypes.guess_extension(content_type) or ".jpg"

    filename = f"{uuid.uuid4()}{ext}"
    file_path = f"{static_root}/{image_dir}/{filename}"
    
    image_url = f"{base_source_url}/{image_dir}/{filename}"
    
    with open(file_path, "wb") as f:
        f.write(response.content)
    return image_url

def text_to_audio(text: str):
    request_url = base_request_url + "/tts"
    payload = {
        "tts_text": text
    }

    response = requests.post(request_url, json=payload, timeout=600)
    
    content_type = response.headers.get("Content-Type", "")
    ext = mimetypes.guess_extension(content_type) or ".wav"

    filename = f"{uuid.uuid4()}{ext}"
    file_path = f"{static_root}/{audio_dir}/{filename}"
    audio_url = f"{base_source_url}/{audio_dir}/{filename}"

    with open(file_path, "wb") as f:
        f.write(response.content)
    return audio_url
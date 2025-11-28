import requests
from django.conf import settings

base_request_url = settings.BASE_REQUEST_URL

def story_to_prompt(description:str, style:str):
    url = base_request_url + "/storyboard"
    payload = {
        "story_text": description,
        "style": style
    }
    response = requests.post(url, json=payload, timeout=60)
    return response.json()

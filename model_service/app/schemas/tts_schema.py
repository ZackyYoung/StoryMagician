from pydantic import BaseModel

class TTSRequest(BaseModel):
    tts_text: str
    spk_id: str = "英文女"

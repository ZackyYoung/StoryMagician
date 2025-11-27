from pydantic import BaseModel
from typing import Optional

class Text2ImageRequest(BaseModel):
    prompt: str
    style: str
    width: Optional[int] = 512
    height: Optional[int] = 256

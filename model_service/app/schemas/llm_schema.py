from pydantic import BaseModel
from typing import List, Optional

class StoryboardShot(BaseModel):
    scene: str
    prompt: str
    narration: str

class StoryboardRequest(BaseModel):
    story_title: str
    story_text: str
    style: str

class StoryboardResponse(BaseModel):
    shots: List[StoryboardShot]

from enum import Enum

class TaskStatus(Enum):
    PENDING = "pending"
    RUNNING = "running"
    SUCCESS = "success"
    FAILED = "failed"

class TaskType(Enum):
    STORY_TO_PROMPT = "StoryToPrompt"
    PROMPT_TO_SCENE = "PromptToScene"
    TEXT_TO_VOICE = "TextToVoice"
    SCENE_TO_VIDEO = "SceneToVideo"
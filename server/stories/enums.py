from enum import Enum

class StoryStatus(Enum):
    DRAFT = "draft"
    GENERATING = "generating"
    DONE = "done"
    ERROR = "error"
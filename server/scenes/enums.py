from enum import Enum

class SceneStatus(Enum):
    DRAFT = "draft"
    GENERATING = "generating"
    DONE = "done"
    ERROR = "error"
from enum import Enum

class VideoStatus(Enum):
    GENERATING = "generating"
    DONE = "done"
    ERROR = "error"
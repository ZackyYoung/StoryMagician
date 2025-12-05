from django.utils import timezone
from celery import shared_task

from stories.enums import StoryStatus
from stories.models import Story
from scenes.models import Scene
from videos.models import Video
from videos.enums import VideoStatus
from .api import scene_to_video
from utils.util import delete_file, url_to_path

@shared_task
def generate_video(story_id: int, transition: str = None):
    """
        scene list -> video
    """
    try:
        # 1. 验证 Story 存在
        story = Story.objects.get(id=story_id)
    except Story.DoesNotExist:
        return
    existing_videos = Video.objects.filter(story_id=story_id)
    for old_video in existing_videos:
        if old_video.video_url:
            # 删除云存储中的实际视频文件
            delete_file(url_to_path(old_video.video_url))
        # 删除数据库记录
        old_video.delete()
        
    video = Video.objects.create(
                story_id=story_id,
                transition=transition,
                status=VideoStatus.GENERATING.value,
                created_at=timezone.now(),
                updated_at=timezone.now(),
            )

    try:
        scenes = list(Scene.objects.filter(story_id=story_id).order_by("scene_index"))
        video.video_url = scene_to_video(scenes, transition)
        video.status = VideoStatus.DONE.value
        video.updated_at = timezone.now()
        video.save()
        story.status = StoryStatus.DONE.value
        story.updated_at = timezone.now()
        story.save()
    except Exception as e:
        video.status = VideoStatus.ERROR.value
        video.updated_at = timezone.now()
        video.info = str(e)
        video.save()
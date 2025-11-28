from django.utils import timezone
from celery import shared_task

from stories.enums import StoryStatus
from stories.models import Story
from scenes.models import Scene
from videos.models import Video
from videos.enums import VideoStatus
from .api import scene_to_video


@shared_task
def generate_video(story_id: int):
    """
        scene list -> video
    """
    story = Story.objects.get(id=story_id)
    video = Video.objects.create(
                story_id=story_id,
                status=VideoStatus.GENERATING.value,
                created_at=timezone.now(),
                updated_at=timezone.now(),
            )
    video.save()
    try:
        scenes = list(Scene.objects.filter(story_id=story_id).order_by("scene_index"))
        video.video_url = scene_to_video(scenes)
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
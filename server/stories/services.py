from django.utils import timezone

from celery import shared_task

from stories.enums import StoryStatus
from stories.models import Story
from scenes.models import Scene
from scenes.enums import SceneStatus
from videos.models import Video
from .api import story_to_prompt
from utils.util import url_to_path, delete_file

@shared_task
def generate_scene_prompt(story_id: int):
    """
        story -> scene prompt
    """
    story = Story.objects.get(id=story_id)
    
    try:
        title = story.title
        description = story.description
        style = story.style
        
        result = story_to_prompt(title, description, style)
        shots = result['shots']
        story.status = StoryStatus.GENERATING.value
        story.updated_at = timezone.now()
        story.save()

        scene_id_list = []
        for idx, shot in enumerate(shots):
            scene = Scene.objects.create(
                story_id=story_id,
                scene_index=idx,
                title=shot['scene'],
                style=style,
                prompt=shot['prompt'],
                narration=shot['narration'],
                status=SceneStatus.DRAFT.value,
                created_at=timezone.now(),
                updated_at=timezone.now(),
            )
            scene_id_list.append(scene.id)
        return scene_id_list
    
    except Exception as e:
        story.status = StoryStatus.ERROR.value
        story.info = str(e)
        story.updated_at = timezone.now()
        story.save()
        

def delete_story(story_id: int):
    story = Story.objects.get(id=story_id)
    scenes = Scene.objects.filter(story_id=story_id)
    for scene in scenes:
        if scene.image_url:
            delete_file(url_to_path(scene.image_url))
        if scene.audio_url:
            delete_file(url_to_path(scene.audio_url))
    
    videos = Video.objects.filter(story_id=story_id)
    for video in videos:
        if video.video_url:
            delete_file(url_to_path(video.video_url))
    
    # 删除关联 scene
    Scene.objects.filter(story_id=story_id).delete()

    # 删除关联 video
    Video.objects.filter(story_id=story_id).delete()

    # 删除 story
    story.delete()
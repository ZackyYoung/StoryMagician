from django.utils import timezone
from celery import shared_task

from stories.models import Story
from scenes.models import Scene
from scenes.enums import SceneStatus
from .api import text_to_image, text_to_audio


@shared_task
def generate_scene(scene_id_list: list[int]):
    """
        scene prompt -> scene
    """
    if scene_id_list is not None:
        for scene_id in scene_id_list:
            scene = Scene.objects.get(id=scene_id)
            try:
                scene.status = SceneStatus.GENERATING.value
                scene.save()
                
                scene.image_url = text_to_image(scene.prompt, scene.style)
                scene.audio_url = text_to_audio(scene.narration)
                
                scene.status = SceneStatus.DONE.value
                scene.updated_at = timezone.now()
                scene.save()
                
                if scene.scene_index == 0:
                    story = Story.objects.get(id=scene.story.id)
                    if story:
                        story.cover_url = scene.image_url
                        story.save()
                    
            except Exception as e:
                scene.updated_at = timezone.now()
                scene.status = SceneStatus.ERROR.value
                scene.info = str(e)
                scene.save()
from django.utils import timezone

from celery import shared_task
import time

from stories.enums import StoryStatus
from stories.models import Story
from scenes.models import Scene
from scenes.enums import SceneStatus

@shared_task
def generate_scene_prompt(story_id: int):
    """
        story -> scene prompt
    """
    story = Story.objects.get(id=story_id)
    title = story.title
    # description = story.description
    # result = generate_prompt(title, description)
    time.sleep(2)  # 模拟耗时任务
    story.status = StoryStatus.GENERATING.value
    story.updated_at = timezone.now()
    story.save()

    scene_num = 3
    scene_id_list = []
    for i in range(scene_num):
        scene = Scene.objects.create(
            story_id=story_id,
            status=SceneStatus.DRAFT.value,
            created_at=timezone.now(),
            updated_at=timezone.now(),
        )
        scene_id_list.append(scene.id)
    print(f"Generated prompt for story: {story_id}")
    return scene_id_list

@shared_task
def generate_scene_img(scene_id_list: list[int]):
    """
        scene prompt -> scene img
    """
    for scene_id in scene_id_list:
        scene = Scene.objects.get(id=scene_id)
        prompt = scene.prompt
        # text_to_img(prompt)
        time.sleep(2)  # 模拟耗时任务
        print(f"Generated scene {scene_id}")
    return scene_id_list

@shared_task
def generate_scene_narration(scene_id_list: list[int]):
    """
        narration -> voice
    """
    for scene_id in scene_id_list:
        scene = Scene.objects.get(id=scene_id)
        narration = scene.narration
        # text_to_voice(narration)
        time.sleep(2)  # 模拟耗时任务
        print(f"Generated narration {scene_id}")

@shared_task
def generate_video(story_id: int):
    """
        scene list -> video
    """
    scenes = Scene.objects.filter(story_id=story_id).order_by("scene_index")

    for scene in scenes:
        # narration = scene.narration
        # text_to_voice(narration)
        time.sleep(2)  # 模拟耗时任务

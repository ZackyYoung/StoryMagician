import uuid
from pathlib import Path
from moviepy.editor import ImageClip, AudioFileClip, concatenate_videoclips
import os
from django.conf import settings

from scenes.models import Scene
from utils.util import url_to_path

base_source_url = str(settings.BASE_SOURCE_URL)
static_root = str(settings.STATIC_ROOT)
video_dir = settings.VIDEO_DIR


def scene_to_video(scenes: list[Scene], pause_sec: float = 0.5):
    video_clips = []
    
    for i, scene in enumerate(scenes):
        
        image_path = url_to_path(scene.image_url)
        audio_path = url_to_path(scene.audio_url)

        if not os.path.exists(image_path):
            raise FileNotFoundError(f"Image not found: {image_path}")
        if not os.path.exists(audio_path):
            raise FileNotFoundError(f"Audio not found: {audio_path}")

        audio = AudioFileClip(audio_path)
        duration = audio.duration  # 音频时长作为图片时长
        
        # 创建图片视频片段
        img_clip = ImageClip(image_path).set_duration(duration)
        img_clip = img_clip.set_audio(audio)
        
        video_clips.append(img_clip)

        # 如果不是最后一个 scene，添加停顿
        if pause_sec > 0 and i < len(scenes) - 1:
            pause_clip = ImageClip(image_path).set_duration(pause_sec)
            video_clips.append(pause_clip)

    # 连接所有 clips
    final_video = concatenate_videoclips(video_clips, method="compose")

    filename = f"{uuid.uuid4()}.mp4"
    file_path = f"{static_root}/{video_dir}/{filename}"
    url = f"{base_source_url}/{video_dir}/{filename}"
    
    # 导出视频
    final_video.write_videofile(
        file_path,
        fps=30,
        codec="libx264",
        audio_codec="aac"
    )

    return url
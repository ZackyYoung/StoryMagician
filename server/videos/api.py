import uuid
from moviepy.editor import ImageClip, AudioFileClip, TextClip, CompositeVideoClip
from moviepy.video.fx.all import fadein, fadeout 
import os
from django.conf import settings

from scenes.models import Scene
from utils.util import url_to_path

base_source_url = str(settings.BASE_SOURCE_URL)
static_root = str(settings.STATIC_ROOT)
video_dir = settings.VIDEO_DIR


def scene_to_video(scenes: list[Scene], transition: str = "fade", pause_sec: float = 0.5):
    
    transition_duration = 0.0
    is_crossfade = (transition == "fade")
    if is_crossfade:
        # 如果是淡入淡出，则定义重叠时长
        transition_duration = 0.5 # 保持原淡入淡出时长 0.5 秒
        
    video_clips = []
    current_time = 0.0
    
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
        
         # 2. 字幕处理：将 narration 字段作为字幕叠加到 img_clip 上
        scene_clip_with_text = img_clip # 默认是图片本身
        
        if scene.narration:
            # 创建 TextClip
            text_clip = TextClip(
                scene.narration, 
                fontsize=12, 
                color='white', 
                bg_color='black',
                method='caption', # 使用 'caption' 自动换行
                stroke_color='white',
                stroke_width=1,
                size=(512,None)
            )
            
            # 设置 TextClip 的时长、位置和大小
            text_clip = text_clip.set_duration(duration)
            
            text_height = text_clip.size[1]
            y_center_pixel = scene_clip_with_text.size[1] - text_height
            
            # 3. 转换为相对位置
            y_relative_position = y_center_pixel / scene_clip_with_text.size[1]
            # 将 TextClip 居中放置在底部 (y=0.85，相对高度)
            # 使用 CompositeVideoClip 将图片和文字叠加
            scene_clip_with_text = CompositeVideoClip(
                [img_clip, text_clip.set_position(('center', y_relative_position), relative=True)]
            )
            # 确保组合后的剪辑保持正确的 duration 和 audio
            scene_clip_with_text = scene_clip_with_text.set_duration(duration).set_audio(audio)
            
        # 转场处理
        current_scene_clip = scene_clip_with_text
        if is_crossfade:
            # A. 淡入 (Fade In): 应用于除第一个 clip 以外的所有 clip 的开始
            if i > 0:
                # 确保淡入时间不超过 clip 持续时间的一半
                fade_in_time = min(transition_duration, duration / 2)
                current_scene_clip = current_scene_clip.fx(fadein, fade_in_time)

            # B. 淡出 (Fade Out): 应用于除最后一个 clip 以外的所有 clip 的结尾
            if i < len(scenes) - 1:
                # 确保淡出时间不超过 clip 持续时间的一半
                fade_out_time = min(transition_duration, duration / 2)
                current_scene_clip = current_scene_clip.fx(fadeout, fade_out_time)
        
        clip = current_scene_clip.set_start(current_time)
        video_clips.append(clip)

        # 如果不是最后一个 scene，添加停顿
        if i < len(scenes) - 1:
            current_time += duration - transition_duration
        if pause_sec > 0:
            current_time += pause_sec

    # 连接所有 clips
    final_video = CompositeVideoClip(video_clips)

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
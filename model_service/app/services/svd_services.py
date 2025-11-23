import io
import logging
import os
import uuid

import numpy as np
import torch
from moviepy import ImageClip, concatenate_videoclips, VideoFileClip, VideoClip, ColorClip, CompositeVideoClip
from moviepy.video.fx import CrossFadeIn, SlideIn, SlideOut, CrossFadeOut
from moviepy.video.fx.FadeIn import FadeIn
from moviepy.video.fx.FadeOut import FadeOut
from diffusers import StableVideoDiffusionPipeline
from PIL import Image


logger = logging.getLogger(__name__)

def generate_video_from_frames(files, transition="crossfade", fps=5) -> str:
    """
    多张关键帧 → 多段 SVD 视频 → 拼接 → 加转场 → 输出 MP4
    """

    svd = StableVideoDiffusionPipeline.from_pretrained("stabilityai/stable-video-diffusion-img2vid", local_files_only=True, torch_dtype=torch.float16, variant="fp16").to(device="cuda")
    svd.enable_model_cpu_offload()

    segments = []

    for file in files:

        content = file.file.read()
        file.file.seek(0)

        img = Image.open(io.BytesIO(content)).convert("RGB")

        result = svd(
            image=img,
            num_frames=10,
            num_inference_steps=25,
            min_guidance_scale=1.0,
            max_guidance_scale=3.0
        )

        frames = result.frames[0]
        clip = frames_to_videoClip(frames, fps)
        segments.append(clip)

    final_clip = concat_with_transition(segments, transition)
    os.makedirs("./outputs", exist_ok=True)
    output_path = f"./outputs/{uuid.uuid4().hex}.mp4"
    final_clip.write_videofile(
        output_path,
        fps=fps,
        codec="libx264",
        audio=False
    )

    return output_path


def frames_to_videoClip(frames, fps):
    """
    将 Stable Video Diffusion 输出的 numpy 数组帧列表转成 VideoClip
    """
    # 将PIL Image对象转换为numpy数组
    numpy_frames = []
    for frame in frames:
        if isinstance(frame, Image.Image):  # 如果是PIL Image对象
            numpy_frame = np.array(frame)
            numpy_frames.append(numpy_frame)
        else:  # 如果已经是numpy数组
            numpy_frames.append(frame)

    duration = len(frames) / fps

    def make_frame(t):
        idx = min(int(t * fps), len(numpy_frames) - 1)
        return numpy_frames[idx]

    return VideoClip(make_frame, duration=duration)


# ------------------------------------------------------
# 辅助方法（视频片段拼接 + 转场）
# ------------------------------------------------------

def concat_with_transition(clips, transition):
    if transition == "none":
        return concatenate_videoclips(clips, method="compose")

    if transition == "fade":
        # 为每个片段添加淡入淡出效果
        processed_clips = []
        for clip in clips:
            faded_clip = clip
            if len(processed_clips) > 0:
                faded_clip = FadeIn(0.5).apply(faded_clip)
            if len(processed_clips) < len(clips) - 1:
                faded_clip = FadeOut(0.5).apply(faded_clip)
            processed_clips.append(faded_clip)

        return concatenate_videoclips(processed_clips, method="compose")


    return concatenate_videoclips(clips, method="compose")

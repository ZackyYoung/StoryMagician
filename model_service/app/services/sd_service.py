import os

import torch
from diffusers import AutoPipelineForText2Image

def generate_image(prompt: str, style: str = None, width: int = 512, height: int = 256):
    """
    返回 PIL.Image
    """
    model = AutoPipelineForText2Image.from_pretrained("stabilityai/sd-turbo", dtype=torch.float16, local_files_only=True)

    full_prompt = f"{prompt}, {style} style"

    # use small steps by default for speed; production tune as needed
    out = model(full_prompt, width=width, height=height, num_inference_steps=10, guidance_scale=5)
    image = out.images[0]
    return image
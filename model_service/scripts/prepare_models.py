import os
import shutil
import subprocess
import torch

from diffusers import AutoPipelineForText2Image, StableVideoDiffusionPipeline


BASE = "../models"
os.makedirs(BASE, exist_ok=True)

def setup_hf_mirror():
    print("=== 配置 HuggingFace 镜像源（hf-mirror.com） ===")

    os.environ["HF_ENDPOINT"] = "https://hf-mirror.com"
    os.environ["HF_HUB_ENABLE_HF_TRANSFER"] = "1"

    # diffusers 强制使用镜像源
    os.environ["HUGGINGFACE_HUB_CACHE"] = os.path.expanduser("~/.cache/huggingface")
    os.environ["TRANSFORMERS_CACHE"] = os.path.expanduser("~/.cache/huggingface/transformers")

    print("已设置 HF_ENDPOINT 为 https://hf-mirror.com\n")


# 1) Stable Diffusion Turbo
def prepare_sd_turbo():
    path = os.path.join(BASE, "sd_turbo")
    print("\n=== [1/4] 准备 Stable Diffusion Turbo ===")

    if os.path.exists(path):
        print("SD Turbo 已存在，跳过下载。")
        return

    print("正在下载 SD Turbo")
    model = AutoPipelineForText2Image.from_pretrained(
        "stabilityai/sd-turbo",
        torch_dtype=torch.float16,
        variant="fp16"
    )
    model.save_pretrained(path)
    print(f"SD Turbo 已保存到: {path}")



# 2) Stable Video Diffusion Img2Vid
def prepare_svd():
    path = os.path.join(BASE, "svd")
    print("\n=== [2/4] 准备 Stable Video Diffusion Img2Vid ===")

    if os.path.exists(path):
        print("Stable Video Diffusion 已存在，跳过下载。")
        return

    print("正在下载 Stable Video Diffusion")
    model = StableVideoDiffusionPipeline.from_pretrained(
        "stabilityai/stable-video-diffusion-img2vid",
        torch_dtype=torch.float16,
        variant="fp16"
    )
    model.save_pretrained(path)
    print(f"SVD 已保存到: {path}")



# 3) CosyVoice（自动下载）
def prepare_cosyvoice():
    path = os.path.join(BASE, "cosyvoice")
    print("\n=== [3/4] CosyVoice 准备 ===")

    os.makedirs(path, exist_ok=True)
    print("CosyVoice TTS 模型将在服务启动时自动下载，无需提前处理。")


# 4) Ollama + Qwen2.5-0.5B

def prepare_ollama():
    print("\n=== [4/4] 检查 Ollama & Qwen2.5-0.5B ===")

    # 检查 ollama 是否安装
    ollama_path = shutil.which("ollama")
    if not ollama_path:
        print("⚠ 未检测到 Ollama。请先安装： https://ollama.com/download")
        return

    print(f"Ollama 已安装: {ollama_path}")

    # 尝试 pull 模型
    try:
        print("正在拉取 Qwen2.5 0.5B...（如已存在会自动跳过）")
        subprocess.run(
            ["ollama", "pull", "qwen2.5:0.5b"],
            check=True
        )
        print("Qwen2.5 0.5B 已准备就绪。")
    except Exception as e:
        print("⚠ 拉取 Qwen2.5-0.5B 时出错：", e)
        print("你可以手动运行： ollama pull qwen2.5:0.5b")


if __name__ == "__main__":
    print("=== 开始准备所有模型 ===")
    setup_hf_mirror()
    prepare_sd_turbo()
    prepare_svd()
    prepare_cosyvoice()
    prepare_ollama()
    print("\n=== 模型准备全部完成 ===")

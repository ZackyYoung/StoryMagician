import requests
import json
import logging
from app import config
from ollama import generate

logger = logging.getLogger(__name__)

def call_ollama(prompt: str, model: str = "qwen3:4b", stream: bool = False) -> str:
    response = generate(prompt=prompt, model=model, stream=stream)
    return response['response']

def _build_prompt(story_title: str, story_text: str, style: str) -> str:
    return f"""
你是一个故事分镜与旁白生成器（生成 JSON）。
根据下列故事标题、故事内容和风格，输出 JSON 数组 shots，包含至少3个分镜，每个分镜包含:
scene（有具体含义的场景标题）, prompt（用于文生图的英文 Prompt）, narration（旁白文本）。
全部内容使用英文输出，只返回 JSON，不要其他多余说明。再次强调，只返回JSON数组。

故事标题:
{story_title}

故事内容:
{story_text}

风格:
{style}

示例输出格式:
[
  {{"scene":"scene A","prompt":"...","narration":"..."}},
  ...
]
"""

def generate_storyboard(story_title:str, story_text: str, style: str):
    prompt = _build_prompt(story_title,story_text, style)
    raw = call_ollama(prompt)
    try:
        shots = json.loads(raw)
        if isinstance(shots, dict) and "shots" in shots:
            shots = shots["shots"]
        if not isinstance(shots, list):
            raise ValueError("Parsed result not a list")
        normalized = []
        for s in shots:
            normalized.append({
                "scene": s.get("scene", ""),
                "prompt": s.get("prompt", ""),
                "narration": s.get("narration", "")
            })
        return normalized
    except Exception:
        # 如果解析失败，尝试从文本中抽取 JSON 段 —— 最简单：找第一个 '[' 到最后一个 ']'
        import re
        match = re.search(r"(\[.*\])", raw, re.S)
        if match:
            try:
                shots = json.loads(match.group(1))
                normalized = []
                for s in shots:
                    normalized.append({
                        "scene": s.get("scene", ""),
                        "prompt": s.get("prompt", ""),
                        "narration": s.get("narration", ""),
                    })
                return normalized
            except Exception as e:
                logger.exception("Failed parse extracted JSON")
                raise RuntimeError("LLM returned unparsable output")
        logger.exception("LLM output not JSON")
        raise RuntimeError("LLM returned unparsable output")

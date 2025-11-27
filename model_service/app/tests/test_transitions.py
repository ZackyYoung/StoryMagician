import os
from moviepy import VideoFileClip
from app.services.svd_services import concat_with_transition  # 你自己的文件

def test_transitions(video1_path, video2_path, output_dir="transition_tests"):
    """
    测试多种转场效果并导出视频。
    """

    # 1. 读取视频
    clip1 = VideoFileClip(video1_path)
    clip2 = VideoFileClip(video2_path)

    clips = [clip1, clip2]

    # 2. 创建输出目录
    os.makedirs(output_dir, exist_ok=True)

    transitions_to_test = ["none", "fade"]

    for t in transitions_to_test:
        print(f"=== Testing transition: {t} ===")

        try:
            final = concat_with_transition(clips, t)

            output_path = os.path.join(output_dir, f"test_{t}.mp4")

            final.write_videofile(
                output_path,
                fps=clip1.fps,
                codec="libx264",
                audio=False
            )

            print(f"Saved: {output_path}\n")

        except Exception as e:
            print(f"[ERROR] Transition `{t}` failed: {e}\n")


if __name__ == "__main__":
    # 你本地的两个 mp4 文件
    videoA = "./video1.mp4"
    videoB = "./video2.mp4"

    test_transitions(videoA, videoB)

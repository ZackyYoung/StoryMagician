import requests

if __name__ == '__main__':
    url = "http://localhost:8000/img2video"

    files = [
        ('images', ('shot1.jpg', open('./test_images/shot1.jpg', 'rb'), 'image/jpeg')),
        ('images', ('shot2.jpg', open('./test_images/shot2.jpg', 'rb'), 'image/jpeg')),
        ('images', ('shot3.jpg', open('./test_images/shot3.jpg', 'rb'), 'image/jpeg'))
    ]

    data = {
        "transition": "fade",
        "fps": 10
    }

    response = requests.post(url, files=files, data=data)

    # 保存返回的视频
    if response.status_code == 200:
        with open("output.mp4", "wb") as f:
            f.write(response.content)
        print("视频生成成功：output.mp4")
    else:
        print("失败：", response.status_code, response.text)

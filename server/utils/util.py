from pathlib import Path
from django.conf import settings

def url_to_path(file_url: str):
    base_source_url = str(settings.BASE_SOURCE_URL)
    static_root = str(settings.STATIC_ROOT)
    if file_url.startswith(base_source_url):
        # 计算相对路径
        file_path = static_root + file_url[len(base_source_url):]
    else:
        # file_url 可能本来就是绝对路径
        file_path = file_url
    
    return file_path
    
def delete_file(file_path: str):
    try:
        file_path = Path(file_path)
        if file_path.exists():
            file_path.unlink()
            print(f"[删除文件] {file_path}")
        else:
            print(f"[文件不存在，跳过] {file_path}")

    except Exception as e:
        print(f"[删除失败] {file_path} 错误: {e}")
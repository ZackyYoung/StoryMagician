import os
from celery import Celery

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'server.settings')

app = Celery('server')

# 从 Django settings 加载 CELERY_ 开头的配置
app.config_from_object('django.conf:settings', namespace='CELERY')

# 自动发现所有 app 中的 tasks.py
app.autodiscover_tasks()

## 服务端
### 1 创建虚拟环境
```bash
python3 -m venv venv
source ./venv/bin/activate
```
### 2 安装依赖
```bash
cd server
pip install requirements.txt
```
### 3 安装并配置mysql
```bash
# 查看可安装mysql-server版本 并安装mysql-server-8.0
sudo apt search mysql-server
sudo apt install -y mysql-server-8.0

# 启动mysql
sudo systemctl start mysql
sudo systemctl enable mysql
sudo systemctl status mysql

# 创建数据库
sudo mysql -uroot -p
create database storymagician;
```
### 4 安装redis
```bash
apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
sudo systemctl status redis
```

### 5 安装并配置nginx
```bash
apt install -y nginx

// 创建站点配置
vim /etc/nginx/sites-available/storymagician
/**
server {
    listen 80;
    server_name 你的域名 或 ECS公网IP;

    location = /favicon.ico { access_log off; log_not_found off; }

    location /static/ {
        alias /var/www/storymagician/staticfiles/;
    }

    location / {
        include proxy_params;
        proxy_pass http://unix:/var/www/storymagician/gunicorn.sock;
    }
}
**/

//启用站点并重启 Nginx
ln -s /etc/nginx/sites-available/storymagician /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx
systemctl enable nginx
```
### 6 安装并配置frps
1. 下载对应版本的frps，并解压至 /etc/frp
```
https://github.com/fatedier/frp/releases/download/v0.64.0/frp_0.64.0_linux_amd64.tar.gz
```
2. 创建 /etc/frp/frps.ini
```
[common]
bind_port = 7000
dashboard_port = 7500
dashboard_user = admin
dashboard_pwd = admin123
```
3. 启动frps
```bash
./frps -c ./frps.ini
```
### 7 启动项目
1. 启动django项目
```bash
source /yourpath/venv/bin/activate
/yourpatg/venv/bin/gunicorn server.wsgi:application --bind 0.0.0.0:8000
```
2. 启动celery
```bash
celery -A server worker -l info
```

## 模型服务
### 1 部署CosyVoice
下载并独立部署cosyvoice模型，参考链接：https://github.com/FunAudioLLM/CosyVoice
对CosyVoice/runtime/python/fastapi/server.py文件中的/inference_sft接口做如下修改
```python
@app.post("/inference_sft")
async def inference_sft(req : TTSRequest):
    model_output = cosyvoice.inference_sft(req.tts_text, req.spk_id)
    # 收集所有 PCM
    pcm_list = []
    for i in model_output:
        audio = (i['tts_speech'].numpy()).astype(np.float32)
        pcm_list.append(audio)

    audio = np.concatenate(pcm_list, axis=0).squeeze()

    # 写入 WAV 到内存
    buffer = io.BytesIO()
    sf.write(buffer, audio, samplerate=cosyvoice.sample_rate, format='WAV')
    wav_bytes = buffer.getvalue()

    return Response(content=wav_bytes, media_type="audio/wav")
```
启动cosyvoice等待被调用

### 2 下载大模型
将ollama, stable diffusion turbo, stable video diffusion下载到本地，并使用ollama拉取qwen2.5:0.5b模型，参考链接：
ollama：https://ollama.com/
stable diffusion turbo: https://huggingface.co/stabilityai/sdxl-turbo
stable video diffusion: https://huggingface.co/stabilityai/stable-video-diffusion-img2vid-xt

### 3 启动model_service
打开model_service项目，创建虚拟环境，下载对应依赖
```bash
conda create -n model_service python=3.12
conda activate model_service
pip install -r requirements.txt
```
启动模型服务
```bash
python -m uvicorn app.main:app --reload
```

### 4 配置fprc
下载fprc，并准备frpc.ini文件
```bash
[common]
server_addr = 14.103.19.244
server_port = 7000

[web]
type = tcp
local_port = 8000
remote_port = 18000
```
启动frpc穿透
```bash
frpc -c frpc.ini
```

## 客户端
### 1 构建项目
使用gradle自动构建项目，下载必要依赖

### 2 打包apk
在android studio中选择build/build signed app bundle or apk打包成apk

### 3 安装app
将apk安装至安卓手机，模型服务和服务端启动后即可使用

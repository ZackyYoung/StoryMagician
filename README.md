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

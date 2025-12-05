"""
URL configuration for server project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/5.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include

from drf_yasg.views import get_schema_view
from drf_yasg import openapi

schema_view = get_schema_view(
    # API 信息
    openapi.Info(
        title='接口文档',   # API文档标题
        default_version='V1',   # 版本信息
        description='接口文档',    # 描述内容
    ),
    public=True,    # 是否公开
)

urlpatterns = [
    path('admin/', admin.site.urls),
    path("stories/", include("stories.urls")),
    path("scenes/", include("scenes.urls")),
    path("videos/", include("videos.urls")),
    path('swagger/', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),   # 互动模式
    path('redoc/', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),   # 文档模式
]

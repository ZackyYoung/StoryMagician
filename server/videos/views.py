from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.generics import RetrieveAPIView
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from stories.models import Story
from .models import Video
from .serializers import VideoDetailSerializer
from .services import generate_video

class GenerateVideoView(APIView):
    """
        生成视频
    """
    @swagger_auto_schema(
        operation_summary="生成视频",
        manual_parameters=[
            openapi.Parameter(
                name="id",
                in_=openapi.IN_QUERY,
                description="故事ID",
                type=openapi.TYPE_INTEGER,
                required=True
            )
        ],
        responses={
            201: openapi.Response("提交成功!"),
            400: openapi.Response("无效请求！"),
            500: openapi.Response("系统错误！")
        }
    )
    def post(self, request):
        story_id = request.query_params.get("id")
        if story_id is not None:
            story = Story.objects.get(id=story_id)
            if story is None:
                return Response(
                    {"error": "未找到对应的 story"},
                    status=status.HTTP_404_NOT_FOUND
                )
            try:
                task = generate_video.delay(story_id)
                return Response({"message": "提交成功！", "task_id": task.id},
                                status=status.HTTP_201_CREATED)
            except Exception as e:
                story.info = str(e)
                story.save()
                return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        return Response({"error": "无效请求！"}, status=status.HTTP_400_BAD_REQUEST)


class VideoDetailView(RetrieveAPIView):
    """
    查询视频详情，返回video_url、创建时间、修改时间
    """
    serializer_class = VideoDetailSerializer
    lookup_field = "id"

    @swagger_auto_schema(
        operation_summary="查询视频详情",
        operation_description="根据镜头ID查询视频详细信息",
        manual_parameters=[
            openapi.Parameter(
                name="id",
                in_=openapi.IN_PATH,
                description="视频ID",
                type=openapi.TYPE_INTEGER,
                required=True
            )
        ],
        responses={200: VideoDetailSerializer}
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        return (
            Video.objects.filter()
        )

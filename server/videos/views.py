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
from utils.permission import HasClientUUID

valid_transition = ['fade', 'none']

class GenerateVideoView(APIView):
    """
        生成视频
    """
    permission_classes = [HasClientUUID] 
    @swagger_auto_schema(
        operation_summary="生成视频",
        manual_parameters=[
            openapi.Parameter(
                name="id",
                in_=openapi.IN_QUERY,
                description="故事ID",
                type=openapi.TYPE_INTEGER,
                required=True
            ),
            openapi.Parameter(
                name="transition",
                in_=openapi.IN_QUERY,
                description="转场效果",
                type=openapi.TYPE_STRING,
                required=False
            )
        ],
        responses={
            201: openapi.Response("提交成功!"),
            400: openapi.Response("无效请求！"),
            404: openapi.Response("未找到对应的 story 或您无权操作！"),
            500: openapi.Response("系统错误！")
        }
    )
    def post(self, request):
        client_uuid = request.client_uuid
        story_id = request.query_params.get("id")
        transition = request.query_params.get("transition")
        if transition and transition not in valid_transition:
            return Response({"error": f"无效transition参数，必须在{valid_transition}中！"}, status=status.HTTP_400_BAD_REQUEST)
        if story_id is not None:
            try:
                # 必须同时检查 story ID 和 client_uuid，以验证所有权
                story = Story.objects.get(id=story_id, client_uuid=client_uuid)
            except Story.DoesNotExist:
                # 如果找不到 Story 或用户无权访问，返回 404
                return Response(
                    {"error": "未找到对应的 story 或您无权操作！"},
                    status=status.HTTP_404_NOT_FOUND
                )
            
            try:
                task = generate_video.delay(story_id, transition)
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
    permission_classes = [HasClientUUID] 
    serializer_class = VideoDetailSerializer
    lookup_field = "story_id"

    @swagger_auto_schema(
        operation_summary="查询视频详情",
        operation_description="根据故事ID获取视频",
        manual_parameters=[
            openapi.Parameter(
                name="story_id",
                in_=openapi.IN_PATH,
                description="故事ID",
                type=openapi.TYPE_INTEGER,
                required=True
            )
        ],
        responses={
            200: VideoDetailSerializer,
            403: openapi.Response("缺少 UUID 权限！"),
            404: openapi.Response("未找到对应视频！")
        }
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        client_uuid = self.request.client_uuid
        story_id = self.kwargs.get(self.lookup_field)
        return (
            Video.objects.filter(story_id=story_id, story__client_uuid=client_uuid)
        )

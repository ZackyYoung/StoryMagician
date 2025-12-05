from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.generics import ListAPIView, RetrieveAPIView
from django_filters.rest_framework import DjangoFilterBackend
from celery import chain
from django.utils import timezone

from .models import Story
from scenes.models import Scene
from .serializers import StorySerializer, CreateStorySerializer, SceneListSerializer, StoryListSerializer
from .services import generate_scene_prompt, delete_story
from scenes.services import generate_scene
from .enums import StoryStatus
from .filters import StoryFilter
from utils.permission import HasClientUUID

# 创建故事
class CreateStoryView(APIView):
    """
        1. 创建故事实体
        2. 提交“生成分镜”任务
        3. 返回响应
    """
    permission_classes = [HasClientUUID]
    
    @swagger_auto_schema(
        operation_summary="创建故事",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                "title": openapi.Schema(type=openapi.TYPE_STRING, description="标题"),
                "description": openapi.Schema(type=openapi.TYPE_STRING, description="描述"),
                "style": openapi.Schema(type=openapi.TYPE_STRING, description="风格"),
            },
            required=["title", "description", "style"],
        ),
        responses={
            201: openapi.Response("提交成功!"),
            400: openapi.Response("无效请求！"),
            500: openapi.Response("系统错误！")
        }
    )

    def post(self, request):
        serializer = CreateStorySerializer(data=request.data)
        if serializer.is_valid():
            title = serializer.validated_data["title"]
            description = serializer.validated_data["description"]
            style = serializer.validated_data["style"]
            # 1. 创建 Story 实体
            story = Story.objects.create(
                client_uuid=request.client_uuid,
                title=title,
                description=description,
                style=style,
                status=StoryStatus.DRAFT.value,
                created_at=timezone.now(),
                updated_at=timezone.now(),
            )
            
            try:
                workflow = chain(
                    generate_scene_prompt.s(story.id),
                    generate_scene.s()
                )
                task = workflow.apply_async()
                return Response({"message": "提交成功！", "task_id": task.id, "story_id": story.id}, status=status.HTTP_201_CREATED)
            except Exception as e:
                story.info = str(e)
                story.status = StoryStatus.ERROR.value
                story.save()
                return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


# 获取用户的全部 Story 列表
class StoryListView(ListAPIView):
    """
    查询故事列表，可按 status、created_at 过滤
    """
    serializer_class = StoryListSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = StoryFilter
    permission_classes = [HasClientUUID]
    
    @swagger_auto_schema(
        operation_summary="查询故事列表",
        manual_parameters=[
            openapi.Parameter(
                "status",
                openapi.IN_QUERY,
                description="故事状态，例如 draft / generating / done / error",
                type=openapi.TYPE_STRING
            ),
            openapi.Parameter(
                "created_after",
                openapi.IN_QUERY,
                description="开始时间",
                type=openapi.TYPE_STRING,
                format=openapi.FORMAT_DATETIME
            ),
            openapi.Parameter(
                "created_before",
                openapi.IN_QUERY,
                description="结束时间 ",
                type=openapi.TYPE_STRING,
                format=openapi.FORMAT_DATETIME
            )
        ],
        responses = {
            200: StoryListSerializer(many=True),
            400: openapi.Response("无效请求！"),
            500: openapi.Response("系统错误！")
        },
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        return Story.objects.filter(client_uuid=self.request.client_uuid).order_by("-created_at")


class StoryDetailView(RetrieveAPIView):
    """
    查询故事详情，返回 story属性
    """
    serializer_class = StorySerializer
    lookup_field = "id"
    permission_classes = [HasClientUUID]
    
    @swagger_auto_schema(
        operation_summary="查询故事详情",
        operation_description="根据故事ID查询故事详细信息",
        manual_parameters=[
            openapi.Parameter(
                name="id",
                in_=openapi.IN_PATH,
                description="故事ID",
                type=openapi.TYPE_INTEGER,
                required=True
            )
        ],
        responses={
            200: StorySerializer,
            400: openapi.Response("无效请求！"),
            500: openapi.Response("系统错误！")
        }
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        return (
            Story.objects.filter()
        )

# 获取story的scene list
class SceneListView(ListAPIView):
    """
    查询故事的分镜列表
    """
    serializer_class = SceneListSerializer
    permission_classes = [HasClientUUID]
    @swagger_auto_schema(
        operation_summary="查询指定故事的分镜列表",
        manual_parameters=[
            openapi.Parameter(
                "id",
                openapi.IN_QUERY,
                description="story id",
                type=openapi.TYPE_INTEGER
            )
        ],
        responses = {
            200: StorySerializer(many=True),
            400: openapi.Response("无效请求！"),
            500: openapi.Response("系统错误！")
        },
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        story_id = self.request.query_params.get("id")
        if story_id:
            story = Story.objects.get(id=story_id)
            if story:
                return story.scenes.all()
            return Scene.objects.none()
        return Scene.objects.none()
    
class DeleteStoryView(APIView):
    """
    删除story，以及相关scene、video
    """
    permission_classes = [HasClientUUID]
    @swagger_auto_schema(
        operation_summary="删除故事",
        operation_description="根据故事ID删除故事",
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
            200: openapi.Response("删除成功！"),
            400: openapi.Response("无效请求！"),
            404: openapi.Response("未找到对应的 story"),
            500: openapi.Response("系统错误！")
        }
    )
    
    def delete(self, request):
        story_id = request.query_params.get("id")
        if story_id:
            try:
                # 查找 Story 时，必须同时传入 client_uuid 进行授权验证
                story = Story.objects.get(id=story_id, client_uuid=request.client_uuid)
            except Story.DoesNotExist:
                # 如果 get() 失败，则说明找不到 Story 或用户无权删除
                return Response(
                    {"error": "未找到对应的 story"},
                    status=status.HTTP_404_NOT_FOUND
                )
                
            delete_story(story_id)
            return Response({"message": "删除成功"}, status=status.HTTP_200_OK)
        
        return Response({"error": "无效请求！"}, status=status.HTTP_400_BAD_REQUEST)
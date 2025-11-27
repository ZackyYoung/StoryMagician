from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.generics import ListAPIView, RetrieveAPIView
from django_filters.rest_framework import DjangoFilterBackend
from django.db.models import Prefetch
from celery import chain
from django.utils import timezone

from .models import Story
from .serializers import StorySerializer, CreateStorySerializer, StoryDetailSerializer
from tasks.services import generate_scene_img, generate_scene_prompt
from .enums import StoryStatus
from .filters import StoryFilter
from scenes.models import Scene

# 创建故事
class CreateStoryView(APIView):
    """
        1. 创建故事实体
        2. 提交“生成分镜”任务
        3. 返回响应
    """
    @swagger_auto_schema(
        operation_summary="创建故事",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                "title": openapi.Schema(type=openapi.TYPE_STRING, description="标题"),
                "description": openapi.Schema(type=openapi.TYPE_STRING, description="描述"),
            },
            required=["title", "description"],
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
            try:
                title = serializer.validated_data["title"]
                description = serializer.validated_data["description"]
                # 1. 创建 Story 实体
                story = Story.objects.create(
                    title=title,
                    description=description,
                    status=StoryStatus.DRAFT.value,
                    created_at=timezone.now(),
                    updated_at=timezone.now(),
                )
                story.save()
                workflow = chain(
                    generate_scene_prompt.s(story.id),
                    generate_scene_img.s()
                )
                task = workflow.apply_async()

                return Response({"message": "提交成功！", "task_id": task.id, "story_id": story.id}, status=status.HTTP_201_CREATED)
            except Exception as e:
                return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


# 获取用户的全部 Story 列表
class StoryListView(ListAPIView):
    """
    查询故事列表，可按 status、created_at 过滤
    """
    serializer_class = StorySerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = StoryFilter

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
            ),
        ],
        responses = {200: StorySerializer(many=True)},
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        return Story.objects.filter().order_by("-created_at")



class StoryDetailView(RetrieveAPIView):
    """
    查询故事详情，返回 story属性、scene列表、videos列表
    """
    serializer_class = StoryDetailSerializer
    lookup_field = "id"

    @swagger_auto_schema(
        operation_summary="查询故事详情",
        operation_description="根据故事ID查询故事详细信息，同时返回该故事对应的场景（按 scene_index 升序）。",
        manual_parameters=[
            openapi.Parameter(
                name="id",
                in_=openapi.IN_PATH,
                description="故事ID",
                type=openapi.TYPE_INTEGER,
                required=True
            )
        ],
        responses={200: StoryDetailSerializer}
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        return (
            Story.objects.filter()
            .prefetch_related(
                Prefetch(
                    "scenes",
                    queryset=Scene.objects.all().order_by("scene_index"),
                    to_attr="ordered_scenes"
                )
            )
        )

from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.generics import RetrieveAPIView
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from .models import Scene
from .serializers import GenerateSceneSerializer, SceneDetailSerializer
from .services import generate_scene
from utils.permission import HasClientUUID

class GenerateSceneView(APIView):
    """
        重新生成scene
    """
    permission_classes = [HasClientUUID]
    @swagger_auto_schema(
        operation_summary="重新生成分镜",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                "id": openapi.Schema(type=openapi.TYPE_INTEGER, description="scene id"),
                "title": openapi.Schema(type=openapi.TYPE_STRING, description="scene标题"),
                "prompt": openapi.Schema(type=openapi.TYPE_STRING, description="scene提示词"),
                "narration": openapi.Schema(type=openapi.TYPE_STRING, description="旁白"),
            },
            required=["id"],
        ),
        responses={
            201: openapi.Response("提交成功!"),
            404: openapi.Response("未找到对应的 Scene 或您无权修改！"),
            400: openapi.Response("无效请求！"),
            500: openapi.Response("系统错误！")
        }
    )
    def post(self, request):
        client_uuid = request.client_uuid
        serializer = GenerateSceneSerializer(data=request.data)
        if serializer.is_valid():
            scene_id = serializer.validated_data["id"]
            try:
                # 使用 .get() 确保只获取一个对象，并在找不到时抛出异常
                # Scene 通过外键 story 关联到 Story，所以使用 'story__client_uuid' 进行跨表查询
                scene = Scene.objects.get(id=scene_id, story__client_uuid=client_uuid)
            except Scene.DoesNotExist:
                # 如果找不到，或者找到但不属于该用户，都返回 404
                return Response(
                    {"error": "未找到对应的 Scene 或您无权修改！"},
                    status=status.HTTP_404_NOT_FOUND
                )
            try:
                update_fields = []
                for field in ["title", "prompt", "narration"]:
                    if field in serializer.validated_data:
                        setattr(scene, field, serializer.validated_data[field])
                        update_fields.append(field)

                if update_fields:
                    scene.save(update_fields=update_fields)

                task = generate_scene.delay([scene_id])

                return Response({"message": "提交成功！", "task_id": task.id},
                                status=status.HTTP_201_CREATED)
            except Exception as e:
                scene.info = str(e)
                scene.save()
                return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class SceneDetailView(RetrieveAPIView):
    """
    查询分镜详情，返回 title, style, prompt, narration, image_url, audio_url等
    """
    serializer_class = SceneDetailSerializer
    lookup_field = "id"
    permission_classes = [HasClientUUID] 
    
    @swagger_auto_schema(
        operation_summary="查询镜头详情",
        operation_description="根据镜头ID查询镜头详细信息",
        manual_parameters=[
            openapi.Parameter(
                name="id",
                in_=openapi.IN_PATH,
                description="镜头ID",
                type=openapi.TYPE_INTEGER,
                required=True
            )
        ],
        responses={200: SceneDetailSerializer}
    )
    def get(self, request, *args, **kwargs):
        return super().get(request, *args, **kwargs)

    def get_queryset(self):
        client_uuid = self.request.client_uuid
        return (
            Scene.objects.filter(story__client_uuid=client_uuid)
        )

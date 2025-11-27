from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.generics import RetrieveAPIView
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from .models import Scene
from .serializers import GenerateSceneSerializer, SceneDetailSerializer
from tasks.services import generate_scene_img

class GenerateSceneView(APIView):
    """
        重新生成scene
    """
    @swagger_auto_schema(
        operation_summary="重新生成分镜",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                "id": openapi.Schema(type=openapi.TYPE_INTEGER, description="scene id"),
                "title": openapi.Schema(type=openapi.TYPE_STRING, description="scene标题"),
                "prompt": openapi.Schema(type=openapi.TYPE_STRING, description="scene提示词"),
            },
            required=["id", "title", "prompt"],
        ),
        responses={
            201: openapi.Response("提交成功!"),
            400: openapi.Response("无效请求！"),
            500: openapi.Response("系统错误！")
        }
    )
    def post(self, request):
        serializer = GenerateSceneSerializer(data=request.data)
        if serializer.is_valid():
            try:
                scene_id = serializer.validated_data["id"]
                scene = Scene.objects.filter(id=scene_id)
                if not scene.exists():
                    return Response(
                        {"error": "未找到对应的 Scene"},
                        status=status.HTTP_404_NOT_FOUND
                    )
                update_fields = []
                for field in ["title", "prompt"]:
                    if field in serializer.validated_data:
                        setattr(scene, field, serializer.validated_data[field])
                        update_fields.append(field)

                if update_fields:
                    scene.save(update_fields=update_fields)

                task = generate_scene_img.delay([scene_id])

                return Response({"message": "提交成功！", "task_id": task.id},
                                status=status.HTTP_201_CREATED)
            except Exception as e:
                return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class SceneDetailView(RetrieveAPIView):
    """
    查询故事详情，返回 story属性、scene列表、videos列表
    """
    serializer_class = SceneDetailSerializer
    lookup_field = "id"

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
        return (
            Scene.objects.filter()
        )

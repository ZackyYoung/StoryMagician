from rest_framework import serializers
from .models import Scene

class GenerateSceneSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(read_only=False)
    class Meta:
        model = Scene
        fields = ["id", "title", "prompt", "narration"]

class SceneDetailSerializer(serializers.ModelSerializer):
    created_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    updated_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    class Meta:
        model = Scene
        fields = "__all__"
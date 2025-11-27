from rest_framework import serializers
from .models import Scene

class GenerateSceneSerializer(serializers.ModelSerializer):
    class Meta:
        model = Scene
        fields = ["id", "title", "prompt"]

class SceneDetailSerializer(serializers.ModelSerializer):
    class Meta:
        model = Scene
        fields = [
            "id",
            "title",
            "prompt",
            "narration",
            "image_url",
            "status",
            "created_at",
            "updated_at",
        ]
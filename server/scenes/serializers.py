from rest_framework import serializers
from .models import Scene

class GenerateSceneSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(read_only=False)
    class Meta:
        model = Scene
        fields = ["id", "title", "prompt", "narration"]

class SceneDetailSerializer(serializers.ModelSerializer):
    class Meta:
        model = Scene
        fields = [
            "id",
            "title",
            "prompt",
            "narration",
            "status",
            "image_url",
            "audio_url",
            "status",
            "created_at",
            "updated_at",
        ]
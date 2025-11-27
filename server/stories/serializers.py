# stories/serializers.py
from rest_framework import serializers
from .models import Story
from scenes.models import Scene
from videos.models import Video

class CreateStorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Story
        fields = ["title", "description"]

class StorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Story
        fields = "__all__"


class SceneSerializer(serializers.ModelSerializer):
    class Meta:
        model = Scene
        fields = ["id", "scene_index", "prompt", "title", "narration", "image_url", "status", "created_at", "updated_at"]


class VideoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Video
        fields = ["id", "video_url", "status", "created_at", "updated_at"]


class StoryDetailSerializer(serializers.ModelSerializer):
    scenes = SceneSerializer(many=True, read_only=True)
    videos = VideoSerializer(many=True, read_only=True)

    class Meta:
        model = Story
        fields = [
            "id",
            "title",
            "description",
            "created_at",
            "updated_at",
            "scenes",
            "videos",
        ]



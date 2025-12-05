# stories/serializers.py
from rest_framework import serializers
from .models import Story
from scenes.models import Scene

class CreateStorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Story
        fields = ["title", "description", "style"]

class SceneListSerializer(serializers.ModelSerializer):
    created_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    updated_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    class Meta:
        model = Scene
        fields = "__all__"

class StorySerializer(serializers.ModelSerializer):
    created_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    updated_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    class Meta:
        model = Story
        fields = "__all__"
        
class StoryListSerializer(serializers.ModelSerializer):
    created_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    updated_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    class Meta:
        model = Story
        fields = [
            "id",
            "title",
            "cover_url",
            "status",
            "created_at",
            "updated_at",
        ]

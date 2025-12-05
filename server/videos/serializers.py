from rest_framework import serializers
from .models import Video

class VideoDetailSerializer(serializers.ModelSerializer):
    created_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    updated_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S")
    class Meta:
        model = Video
        fields = [
            "id",
            "video_url",
            "status",
            "created_at",
            "updated_at",
        ]
from rest_framework import serializers
from .models import Video

class VideoDetailSerializer(serializers.ModelSerializer):
    class Meta:
        model = Video
        fields = [
            "id",
            "video_url",
            "status",
            "created_at",
            "updated_at",
        ]
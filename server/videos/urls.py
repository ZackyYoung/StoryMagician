from django.urls import path
from .views import GenerateVideoView, VideoDetailView

urlpatterns = [
    path("generate/", GenerateVideoView.as_view()),
    path("detail/<int:story_id>", VideoDetailView.as_view()),
]

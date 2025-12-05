from django.urls import path
from .views import GenerateSceneView, SceneDetailView

urlpatterns = [
    path("generate/", GenerateSceneView.as_view()),
    path("detail/<int:id>", SceneDetailView.as_view()),
]

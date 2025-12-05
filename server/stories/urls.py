from django.urls import path
from .views import CreateStoryView, StoryListView, StoryDetailView, DeleteStoryView, SceneListView

urlpatterns = [
    path("create/", CreateStoryView.as_view()),
    path("storyList/", StoryListView.as_view()),
    path("sceneList/", SceneListView.as_view()),
    path("delete/", DeleteStoryView.as_view()),
    path("detail/<int:id>", StoryDetailView.as_view()),
]

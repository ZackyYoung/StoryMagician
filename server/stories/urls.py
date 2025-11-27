from django.urls import path
from .views import CreateStoryView, StoryListView, StoryDetailView

urlpatterns = [
    path("create/", CreateStoryView.as_view()),
    path("list/", StoryListView.as_view()),
    path("detail/<int:id>", StoryDetailView.as_view()),
]

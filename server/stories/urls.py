from django.urls import path
from .views import CreateStoryView, StoryListView, StoryDetailView, DeleteStroyView

urlpatterns = [
    path("create/", CreateStoryView.as_view()),
    path("list/", StoryListView.as_view()),
    path("delete/", DeleteStroyView.as_view()),
    path("detail/<int:id>", StoryDetailView.as_view()),
]

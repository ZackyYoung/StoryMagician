import django_filters
from .models import Story

class StoryFilter(django_filters.FilterSet):
    status = django_filters.CharFilter(field_name="status")
    created_after = django_filters.DateTimeFilter(
        field_name="created_at", lookup_expr="gte"
    )
    created_before = django_filters.DateTimeFilter(
        field_name="created_at", lookup_expr="lte"
    )

    class Meta:
        model = Story
        fields = ["status", "created_after", "created_before"]

from rest_framework.permissions import BasePermission
from django.conf import settings

class HasClientUUID(BasePermission):
    """
    自定义权限类：检查请求头中是否包含 client_uuid (由 settings.UUID 定义)。
    如果缺少，DRF 将返回 403 Forbidden。
    """
    # 当权限验证失败时，DRF 返回 403 时会显示这条消息
    message = "Authorization header missing required UUID."

    def has_permission(self, request, view):
        # 从 settings 中获取用于查找 Header 的键 (例如 'HTTP_UUID')
        uuid_header_key = settings.CUSTOM_AUTH_HEADER
        # 检查请求头中是否存在 UUID
        client_uuid = request.META.get(uuid_header_key)
        
        # 如果 client_uuid 存在，则允许访问视图
        if client_uuid:
            # 可以在 request 对象上添加一个属性来存储 UUID，方便在 View 中直接使用
            # 这样 View 就不必重复从 request.META 中查找。
            request.client_uuid = client_uuid
            return True
        
        # 如果 UUID 不存在，则拒绝访问
        return False
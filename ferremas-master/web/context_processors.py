from .models import Producto

def cart_count(request):
    count = Producto.objects.count()
    return {'cart_count': count}

from django.shortcuts import render
from rest_framework import viewsets
from .models import Producto
from .serializers import ProductoSerializer

# Create your views here.

def home(request): 
    context={}
    return render(request,'web/home.html', context)

class ProductoViewSet(viewsets.ModelViewSet):
    queryset = Producto.objects.all()
    serializer_class = ProductoSerializer
    
def list(self, request, *args, **kwargs):
        print("GET productos API llamado")  # para ver en consola
        return super().list(request, *args, **kwargs)

def catalogo(request):
    return render(request, 'web/catalogo.html')

def login(request):
    return render(request, 'web/login.html')

def contacto(request):
    return render(request, 'web/contacto.html')


def registro(request):
    return render(request, 'web/registro.html')

def carrito(request):
    return render(request, 'web/carrito.html')


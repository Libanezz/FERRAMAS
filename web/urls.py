from django.urls import path, include
from rest_framework.routers import DefaultRouter
from . import views
from web.views import ProductoViewSet
from rest_framework import routers


router = routers.DefaultRouter()
router.register(r'productos', ProductoViewSet)

urlpatterns =[
    path('home', views. home, name='home'),

    path('contacto/', views. contacto, name='contacto'),  
    path('catalogo/', views. catalogo, name='catalogo'),
    path('login/', views. login, name='login'),
    path('registro/', views. registro, name='registro'),
    path('carrito/', views. carrito, name='carrito'),
    path('api/', include(router.urls)),
    
]   
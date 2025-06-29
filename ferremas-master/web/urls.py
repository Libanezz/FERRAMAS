from django.urls import path
from . import views

urlpatterns =[
    path('', views. home, name='home'),

    path('contacto/', views. contacto, name='contacto'),  
    path('catalogo/', views.ver_productos, name='catalogo'),
    path('login/', views. login, name='login'),
    path('registro/', views.registrar_cliente, name='registro'),
    path('carrito/', views. carrito, name='carrito'),
    #path('producto_del/<str:pk>', views., name='producto_del'),
    #path('agregar_producto', views.agregar_producto, name='agregar_producto'),
    path('vendedor/', views. vendedor, name='vendedor'),
    path('contador/', views. contador, name='contador'),
    path('administrador/', views. administrador, name='administrador'),
    path('bodeguero/', views. bodeguero, name='bodeguero'),
    path('logout/', views.logout, name='logout'),
    path('cambiar-password/', views.cambiar_password, name='cambiar_password'),
    path('panel/crear-usuario/', views.crear_usuario, name='crear_usuario'),
    path('panel/listar-usuarios/', views.listar_usuarios, name='listar_usuarios'),
    path('panel/editar-usuario/<int:id_usuario>/', views.editar_usuario, name='editar_usuario'),
    path('panel/eliminar-usuario/<int:id_usuario>/', views.eliminar_usuario, name='eliminar_usuario'),







]
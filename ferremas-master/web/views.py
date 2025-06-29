from django.shortcuts import render, redirect, get_object_or_404
from django.http import JsonResponse, HttpResponse
from django.contrib import messages
from .models import Producto
import requests
import bcrypt
import json
from django.views.decorators.csrf import csrf_exempt



# Create your views here.
#core- listar productos de la bdd tabla productos
def obtener_productos():
    ##url="http://127.0.0.1:8090/api/productos/"
    ##url de pruebas
    url='http://localhost:8080/api/productos'
    try:
        response = requests.get(url)
        data = response.json()
        return data
    except Exception as e:
        return None
    

def ver_productos(request):
    productos = obtener_productos()
    #print("Productos desde API:", productos)
    if not productos:
        messages.warning(request, "No se pudieron cargar productos desde la API")
        productos = []

    contexto = { "datos":productos}
    return render (request, 'catalogo.html', contexto)

def carrito(request):
    productos= Producto.objects.all()
    context= {'productos':productos}
    return render(request, 'carrito.html', context)


def vendedor(request):
    if request.session.get("tipoUsuario") != "vendedor":
        messages.error(request, "Acceso denegado. No tienes permisos para ingresar aquí.")
        return redirect('login')
    return render(request, 'vendedor.html')

def contador(request):
    if request.session.get("tipoUsuario") != "contador":
        messages.error(request, "No tienes permisos para ingresar aquí.")
        return redirect('login')
    return render(request, 'contador.html')


def administrador(request):
    if request.session.get("tipoUsuario") != "administrador":
        messages.error(request, "Acceso denegado. No tienes permisos para ingresar aquí.")
        return redirect('login')
    return render(request, 'administrador.html')


def crear_usuario(request):
    if request.session.get("tipoUsuario") != "administrador":
        messages.error(request, "Acceso denegado.")
        return redirect('login')

    try:
        # Obtener sucursales desde la API para mostrar en el formulario
        sucursales = requests.get('http://localhost:8080/api/sucursales').json()
    except Exception:
        sucursales = []
        messages.warning(request, "No se pudo cargar la lista de sucursales.")

    if request.method == 'POST':
        datos = {
            "nombre": request.POST.get('nombre'),
            "rut": request.POST.get('rut'),
            "correo": request.POST.get('correo'),
            "telefono": request.POST.get('telefono'),
            "nombreUsuario": request.POST.get('nombreUsuario'),
            "tipoUsuario": request.POST.get('tipoUsuario'),
            "idSucursal": request.POST.get('idSucursal'),
            "cambioPassword": False
        }

        contrasenia_plana = request.POST.get('contrasenia')

        # Cifrar la contraseña con bcrypt
        try:
            hashed = bcrypt.hashpw(contrasenia_plana.encode(), bcrypt.gensalt()).decode()
            datos["contrasenia"] = hashed
        except Exception:
            messages.error(request, "Error al cifrar la contraseña.")
            return redirect('crear_usuario')

        # Enviar al backend (ajustá la URL si cambia)
        try:
            response = requests.post('http://localhost:8080/api/usuarios', json=datos)
            if response.status_code == 201:
                messages.success(request, "Usuario creado correctamente.")
                return redirect('listar_usuarios')
            else:
                messages.error(request, "Error al crear el usuario.")
        except Exception:
            messages.error(request, "No se pudo conectar con el servidor.")
            return redirect('crear_usuario')

    return render(request, 'crear_usuario.html', {'sucursales': sucursales})


def listar_usuarios(request):
    if request.session.get("tipoUsuario") != "administrador":
        messages.error(request, "Acceso denegado.")
        return redirect('login')

    try:
        response = requests.get('http://localhost:8080/api/usuarios')
        if response.status_code == 200:
            todos = response.json()
            # Filtrar roles internos
            internos = [
                user for user in todos
                if user["tipoUsuario"] in ["vendedor", "bodeguero", "contador"]
            ]
        else:
            messages.error(request, "No se pudieron obtener los usuarios.")
            internos = []
    except Exception:
        messages.error(request, "Error al conectar con el backend.")
        internos = []

    return render(request, 'listar_usuarios.html', {"usuarios": internos})


def editar_usuario(request, id_usuario):
    if request.session.get("tipoUsuario") != "administrador":
        messages.error(request, "Acceso denegado.")
        return redirect('login')

    # Obtener datos del usuario
    try:
        user_response = requests.get(f'http://localhost:8080/api/usuarios/{id_usuario}')
        usuario = user_response.json()
    except Exception:
        messages.error(request, "Error al obtener datos del usuario.")
        return redirect('listar_usuarios')

    # Obtener sucursales para el select
    try:
        sucursales = requests.get('http://localhost:8080/api/sucursales').json()
    except Exception:
        sucursales = []

    if request.method == 'POST':
        datos_actualizados = {
            "nombre": request.POST.get("nombre"),
            "rut": request.POST.get("rut"),
            "correo": request.POST.get("correo"),
            "telefono": request.POST.get("telefono"),
            "nombreUsuario": request.POST.get("nombreUsuario"),
            "tipoUsuario": request.POST.get("tipoUsuario"),
            "idSucursal": request.POST.get("idSucursal"),
            "cambioPassword": usuario.get("cambioPassword", False),  # mantener estado
            "contrasenia": usuario["contrasenia"]  # no se modifica desde acá
        }

        try:
            url = f"http://localhost:8080/api/usuarios/{id_usuario}"
            response = requests.put(url, json=datos_actualizados)
            if response.status_code == 200:
                messages.success(request, "Usuario actualizado correctamente.")
                return redirect('listar_usuarios')
            else:
                messages.error(request, "No se pudo actualizar el usuario.")
        except Exception:
            messages.error(request, "Error al conectar con el backend.")

    return render(request, 'editar_usuario.html', {
        "usuario": usuario,
        "sucursales": sucursales
    })

def eliminar_usuario(request, id_usuario):
    if request.session.get("tipoUsuario") != "administrador":
        messages.error(request, "Acceso denegado.")
        return redirect('login')

    # Obtener los datos para mostrar en la confirmación
    try:
        usuario = requests.get(f'http://localhost:8080/api/usuarios/{id_usuario}').json()
    except Exception:
        messages.error(request, "No se pudo cargar el usuario.")
        return redirect('listar_usuarios')

    if request.method == 'POST':
        try:
            response = requests.delete(f'http://localhost:8080/api/usuarios/{id_usuario}')
            if response.status_code == 204:
                messages.success(request, "Usuario eliminado correctamente.")
            else:
                messages.error(request, "No se pudo eliminar el usuario.")
        except Exception:
            messages.error(request, "Error al conectar con el backend.")
        return redirect('listar_usuarios')

    return render(request, 'confirmar_eliminacion.html', {'usuario': usuario})






def bodeguero(request):
    if request.session.get("tipoUsuario") != "bodeguero":
        messages.error(request, "Acceso denegado.")
        return redirect('login')
    return render(request, 'bodeguero.html')


def home(request): 
    context={}
    return render(request,'home.html', context)


import requests
import bcrypt
from django.shortcuts import render, redirect
from django.contrib import messages

def login(request):
    if request.method == 'POST':
        nombre_usuario = request.POST.get('username')
        contrasena_ingresada = request.POST.get('password')

        try:
            response = requests.get('http://localhost:8080/api/usuarios')
            usuarios = response.json()

            for usuario in usuarios:
                if usuario['nombreUsuario'] == nombre_usuario:
                    contrasena_guardada = usuario['contrasenia']
                    tipo = usuario['tipoUsuario']

                    # === Lógica diferenciada por rol ===
                    if tipo == 'administrador':
                        if bcrypt.checkpw(contrasena_ingresada.encode(), contrasena_guardada.encode()):
                            return iniciar_sesion(request, usuario)
                        else:
                             messages.error(request, "Contraseña incorrecta.")
                             return redirect('login')


                    elif tipo == 'cliente':
                        # Contraseña hasheada con bcrypt
                        try:
                            if bcrypt.checkpw(contrasena_ingresada.encode(), contrasena_guardada.encode()):
                                return iniciar_sesion(request, usuario)
                            else:
                                messages.error(request, "Contraseña incorrecta.")
                                return redirect('login')
                        except Exception:
                            messages.error(request, "Error en validación de contraseña.")
                            return redirect('login')

                    elif tipo in ['vendedor', 'bodeguero', 'contador']:
                        # Contraseña en texto plano (por ahora)
                        if contrasena_ingresada == contrasena_guardada:
                            return iniciar_sesion(request, usuario)
                        else:
                            messages.error(request, "Contraseña incorrecta.")
                            return redirect('login')

            messages.error(request, "Usuario no encontrado.")
            return redirect('login')

        except Exception:
            messages.error(request, "Error al conectar con el servidor.")
            return redirect('login')

    return render(request, 'login.html')



def iniciar_sesion(request, usuario):
    request.session['idUsuario'] = usuario['idUsuario']
    request.session['nombreUsuario'] = usuario['nombreUsuario']
    request.session['tipoUsuario'] = usuario['tipoUsuario']

    # Forzamos cambio de contraseña si es admin y no la ha cambiado
    if usuario['tipoUsuario'] == 'administrador' and usuario.get('cambioPassword', False) == True:
        messages.info(request, "Por seguridad, debes cambiar tu contraseña inicial.")
        return redirect('cambiar_password')  # Asegurate de tener esta vista lista

    # Redireccionamos según tipo de usuario
    tipo = usuario['tipoUsuario']
    if tipo == 'cliente':
        return redirect('catalogo')
    elif tipo == 'vendedor':
        return redirect('vendedor')
    elif tipo == 'administrador':
        return redirect('administrador')
    elif tipo == 'bodeguero':
        return redirect('bodeguero')
    elif tipo == 'contador':
        return redirect('contador')
    else:
        messages.error(request, "Tipo de usuario no reconocido.")
        return redirect('login')




@csrf_exempt
def cambiar_password(request):
    if request.session.get("tipoUsuario") != "administrador":
        messages.error(request, "Acceso denegado.")
        return redirect('login')

    if request.method == 'POST':
        nueva_password = request.POST.get('nueva_password')
        confirmar_password = request.POST.get('confirmar_password')

        if nueva_password != confirmar_password:
            messages.error(request, "Las contraseñas no coinciden.")
            return redirect('cambiar_password')

        try:
            hashed = bcrypt.hashpw(nueva_password.encode(), bcrypt.gensalt()).decode()
        except Exception:
            messages.error(request, "Error al cifrar la contraseña.")
            return redirect('cambiar_password')

        try:
            id_usuario = request.session.get("idUsuario")

            # 1. Obtener los datos actuales del usuario
            response_get = requests.get(f"http://localhost:8080/api/usuarios/{id_usuario}")
            if response_get.status_code != 200:
                messages.error(request, "No se pudo obtener información del usuario.")
                return redirect('cambiar_password')

            usuario_actual = response_get.json()

            # 2. Reemplazar campos sensibles
            usuario_actual["contrasenia"] = hashed
            usuario_actual["cambioPassword"] = False

            # 3. Enviar PUT con objeto completo
            response_put = requests.put(f"http://localhost:8080/api/usuarios/{id_usuario}", json=usuario_actual)
            if response_put.status_code == 200:
                messages.success(request, "Contraseña actualizada correctamente.")
                return redirect('administrador')
            else:
                messages.error(request, "Error al actualizar la contraseña.")
                return redirect('cambiar_password')

        except Exception:
            messages.error(request, "No se pudo conectar con el servidor.")
            return redirect('cambiar_password')

    return render(request, 'cambiar_password.html')






def logout(request):
    request.session.flush()  # elimina todos los datos de sesión
    return redirect('login')


def contacto(request):
    return render(request, 'contacto.html')


def registrar_cliente(request):
    # 1. Traer las sucursales desde la API para el dropdown
    try:
        suc_res = requests.get("http://localhost:8080/api/sucursales")
        sucursales = suc_res.json()
    except:
        sucursales = []
        messages.error(request, "No se pudieron cargar las sucursales.")

    if request.method == 'POST':
        # Validar contraseña
        if request.POST['contrasenia'] != request.POST['confirmar_contrasenia']:
            messages.error(request, "Las contraseñas no coinciden.")
            return render(request, 'registro.html', {'sucursales': sucursales})

        # Verificar si usuario ya existe
        usuarios = requests.get("http://localhost:8080/api/usuarios").json()
        if any(u['nombreUsuario'] == request.POST['nombre_usuario'] for u in usuarios):
            messages.error(request, "El nombre de usuario ya está registrado.")
            return render(request, 'registro.html', {'sucursales': sucursales})

        # Encriptar contraseña
        contrasenia_encriptada = bcrypt.hashpw(
            request.POST['contrasenia'].encode('utf-8'),
            bcrypt.gensalt()
        ).decode('utf-8')

        # Armar el payload
        nuevo_cliente = {
            "nombre": request.POST['nombre'],
            "rut": request.POST['rut'],
            "correo": request.POST['correo'],
            "telefono": request.POST['telefono'],
            "nombreUsuario": request.POST['nombre_usuario'],
            "contrasenia": contrasenia_encriptada,
            "tipoUsuario": request.POST.get('tipo_usuario', 'cliente'),
            "cambioPassword": False,
            "sucursal": {
                "idSucursal": int(request.POST['sucursal_id'])
            }
        }

        res = requests.post("http://localhost:8080/api/usuarios", json=nuevo_cliente)

        if res.status_code in [200, 201]:
            messages.success(request, "¡Registro exitoso! Ahora puedes iniciar sesión.")
            return redirect('login')
        else:
            messages.error(request, "Ocurrió un error al registrar. Intenta nuevamente.")

    return render(request, 'registro.html', {'sucursales': sucursales})







'''def agregar_producto(request):
    if request.method == 'POST':
        nombre = request.POST.get('nombre')
        descripcion = request.POST.get('descripcion')
        precio = request.POST.get('precio')
        imagen_url = request.POST.get('imagen_url')
        
        producto = Producto(nombre=nombre, descripcion=descripcion, precio=precio, imagen_url=imagen_url)
        producto.save()
        
        return redirect('catalogo')
    return render(request, 'catalogo.html', {'mensaje': 'Producto agregado correctamente.'}) 
'''

'''def producto_del(request, pk):
    context = {}
    try:
        producto = Producto.objects.get(id=pk)
        producto.delete()

        mensaje = "Producto eliminado correctamente"
        productos = Producto.objects.all()
        context = {'productos': productos, 'mensaje': mensaje}
    except Producto.DoesNotExist:
        mensaje = "El producto no existe"
        productos = Producto.objects.all()
        context = {'productos': productos, 'mensaje': mensaje}
    except Exception as e:
        mensaje = f"Error al eliminar el producto: {str(e)}"
        productos = Producto.objects.all()
        context = {'productos': productos, 'mensaje': mensaje}

    return render(request, 'carrito.html', context)
'''











'''
#APIs:
#def api_productos(request):
    productos = Producto.objects.all()
    data = [{
        'id': p.id,
        'nombre': p.nombre,
        'descripcion': p.descripcion,
        'precio': p.precio,
        'stock': p.stock
    } for p in productos]
    return JsonResponse(data, safe=False)

'''
''''
#@csrf_exempt
#def api_usuarios(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        # Crear usuario
        usuario = Usuario.objects.create(**data)
        return JsonResponse({'success': True})

    usuarios = Usuario.objects.all()
    data = [{'id': u.id, 'nombre': u.nombre, 'correo': u.correo} for u in usuarios]
    return JsonResponse(data, safe=False)
'''
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

##def carrito(request):
  ##  productos= Producto.objects.all()
    ##context= {'productos':productos}
    ##return render(request, 'carrito.html', context)



def carrito(request):
    carrito_ids = request.session.get('carrito', [])
    productos = Producto.objects.filter(id__in=carrito_ids)

    subtotal = sum(p.precio for p in productos)
    envio = 3000 if productos else 0
    total = subtotal + envio

    context = {
        'productos': productos,
        'subtotal': subtotal,
        'envio': envio,
        'total': total,
    }
    return render(request, 'carrito.html', context)



@csrf_exempt
def api_agregar_al_carrito(request):
    if request.method == 'POST':
        producto_id_raw = request.POST.get('producto_id')
        try:
            producto_id = int(producto_id_raw)
            producto = Producto.objects.get(id=producto_id)
        except (ValueError, TypeError, Producto.DoesNotExist):
            return JsonResponse({'success': False, 'error': 'ID de producto inválido o inexistente'})

        carrito = request.session.get('carrito', [])
        if producto_id not in carrito:
            carrito.append(producto_id)
            request.session['carrito'] = carrito

        return JsonResponse({'success': True, 'contador': len(carrito)})
    return JsonResponse({'success': False, 'error': 'Método no permitido'})


@csrf_exempt
def api_eliminar_del_carrito(request):
    if request.method == 'POST':
        producto_id_raw = request.POST.get('producto_id')
        try:
            producto_id = int(producto_id_raw)
        except (ValueError, TypeError):
            return JsonResponse({'success': False, 'error': 'ID de producto inválido'})

        carrito = request.session.get('carrito', [])
        if producto_id in carrito:
            carrito.remove(producto_id)
            request.session['carrito'] = carrito

        return JsonResponse({'success': True, 'contador': len(carrito)})
    return JsonResponse({'success': False, 'error': 'Método no permitido'})

def vendedor(request):
    if request.session.get("tipoUsuario") != "vendedor":
        messages.error(request, "Acceso denegado. No tienes permisos para ingresar aquí.")
        return redirect('login')
    return render(request, 'administrador.html')

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

        # Enviar al backend 
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
            # Filtrado roles internos
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

    # Si es cliente, inicializa el carrito vacío al ingresar
    if usuario['tipoUsuario'] == 'cliente':
        request.session['carrito'] = []

    # Forzamos cambio de contraseña si es admin y no la ha cambiado
    if usuario['tipoUsuario'] == 'administrador' and usuario.get('cambioPassword', False) == True:
        messages.info(request, "Por seguridad, debes cambiar tu contraseña inicial.")
        return redirect('cambiar_password')

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




    # elimina los datos de sesión
def logout(request):
        request.session.flush()  
        return redirect('login')


def registrar_cliente(request):
     #Trae las sucursales desde la API para el dropdown
    try:
        suc_res = requests.get("http://localhost:8080/api/sucursales")
        sucursales = suc_res.json()
    except:
        sucursales = []
        messages.error(request, "No se pudieron cargar las sucursales.")

    # recibe una solicitud POST (formulario enviado)
    if request.method == 'POST':
        # Validar contraseña
        if request.POST['contrasenia'] != request.POST['confirmar_contrasenia']:
            messages.error(request, "Las contraseñas no coinciden.")
            return render(request, 'registro.html', {'sucursales': sucursales})

        # Verifica si el usuario ya existe
        try:
            usuarios = requests.get("http://localhost:8080/api/usuarios").json()
        except:
            messages.error(request, "No se pudo validar el nombre de usuario.")
            return render(request, 'registro.html', {'sucursales': sucursales})

        if any(u['nombreUsuario'] == request.POST['nombre_usuario'] for u in usuarios):
            messages.error(request, "El nombre de usuario ya está registrado.")
            return render(request, 'registro.html', {'sucursales': sucursales})

        # Encripta la contraseña
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

        # Enviar datos a la API
        res = requests.post("http://localhost:8080/api/usuarios", json=nuevo_cliente)

        if res.status_code in [200, 201]:
            messages.success(request, "¡Registro exitoso! Ahora puedes iniciar sesión.")
            return redirect('login')
        else:
            messages.error(request, "Ocurrió un error al registrar. Intenta nuevamente.")

    # Renderizar formulario (ya sea GET o después de error)
    return render(request, 'registro.html', {'sucursales': sucursales})


def contacto(request):
    return render(request, 'contacto.html')

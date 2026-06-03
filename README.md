# 🧪 FERRAMAS – Guía de Calidad y Pruebas (QA)

Este repositorio contiene la configuración, pruebas unitarias, de integración, automatización y de seguridad implementadas para el sistema eCommerce **FERREMAS** en la rama **Dev/Qa**.

---

## 📋 Resumen de Pruebas Implementadas

| Componente | Framework / Herramienta | Archivos clave | Cantidad de Casos |
|---|---|---|---|
| **Backend Java (`ferramas-api`)** | JUnit 5 + Mockito | `PagoServiceTest.java`, `UsuarioServiceTest.java`, `InventarioServiceTest.java`, `PedidoServiceTest.java`, `PagoControllerTest.java` | **42 casos** |
| **Frontend/Backend (`ferremas-master`)** | Django TestCase | `web/tests.py` | **12 casos** |
| **Automatización UI (`TestFerremas`)** | Selenium 4 + Cucumber | `EscenarioLoginAdmin.feature`, `PasosLoginAdmin.java` | **1 caso (Login flow)** |
| **Análisis Estático** | SonarQube | `run_sonar.bat` (dentro de `ferramas-api/`) | Inspección de código |
| **Seguridad / DAST** | OWASP-ZAP | Análisis dinámico local | Escaneo de vulnerabilidades |
| **Rendimiento / Carga** | Apache JMeter | Simulación de 100 usuarios concurrentes | Carga y estrés de API |

---

## ⚙️ 1. Ejecución de Pruebas Unitarias y de Integración

### A. Backend Java (Spring Boot)
Para ejecutar las **42 pruebas unitarias y de integración** del backend:
1. Abre una terminal en `ferramas-api/`.
2. Ejecuta el wrapper de Maven:
   ```bash
   .\mvnw.cmd test
   ```
*Nota: Se configuró Mockito con strictness `LENIENT` en `PagoServiceTest` para evitar caídas debido a stubs no utilizados, y se solucionó un conflicto de tipos (BigDecimal) en `InventarioServiceTest`.*

### B. Frontend/Backend (Django)
Para ejecutar las **12 pruebas de integración** de Django:
1. Abre una terminal en `ferremas-master/`.
2. Activa el entorno virtual:
   ```bash
   venv\Scripts\activate
   ```
3. Ejecuta los tests:
   ```bash
   python manage.py test web -v 2
   ```

---

## 🚀 2. Pruebas Automatizadas de Interfaz (Selenium + Cucumber)

El proyecto de automatización se encuentra en la carpeta del escritorio: `C:\Users\LAIIZU\Escritorio\Automatización\TestFerremas`.

### Instrucciones para ejecutarlo:
1. Asegúrate de tener el servidor local de Django encendido en `http://127.0.0.1:8000/` (`python manage.py runserver`).
2. Abre una terminal de PowerShell en la carpeta `TestFerremas`.
3. Ejecuta el test forzando el Runner de Cucumber:
   ```powershell
   .\mvnw.cmd clean test -Dtest=Runner
   ```
*Nota: Se actualizó el proyecto a **Selenium 4**, lo que activa **Selenium Manager** para descargar de forma automática el driver correcto de Google Chrome, eliminando fallos de versión del driver. También se corrigieron los XPaths para adecuarlos al HTML real de la página.*

---

## 📊 3. Inspección de Código Estático (SonarQube)

Para analizar la calidad de código de `ferramas-api`:
1. Asegúrate de tener levantado el servidor de SonarQube desde el archivo bat:
   `C:\Users\LAIIZU\Downloads\sonarqube-26.5.0.122743\sonarqube-26.5.0.122743\bin\windows-x86-64\StartSonar.bat`
2. Ve a la carpeta `ferramas-api/` y ejecuta el script automatizado:
   ```powershell
   .\run_sonar.bat
   ```
3. Revisa y captura los resultados en tu navegador: `http://localhost:9000/dashboard?id=ferramas-api`.

---

## 🔒 4. Credenciales de Base de Datos para Pruebas (Admin)

Las pruebas automatizadas de inicio de sesión requieren una cuenta válida. La base de datos local MySQL contiene las siguientes credenciales de prueba preconfiguradas:
* **Usuario Administrador**: `admin`
* **Contraseña**: `admin`
*(El hash de la contraseña en la base de datos es bcrypt y corresponde exactamente al texto plano `admin`).*

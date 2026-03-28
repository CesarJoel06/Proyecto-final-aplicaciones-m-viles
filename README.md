# SecurityQuotesRemaster

## Descripción general

**SecurityQuotesRemaster** es una aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose** para la gestión básica de usuarios y la generación de **cotizaciones en PDF** orientadas a servicios técnicos e instalaciones, especialmente en el rubro de **cámaras de seguridad, intercomunicadores y sistemas de alarma**.

El proyecto fue planteado en un contexto académico donde originalmente se esperaba el uso de **Firebase**. Sin embargo, esta implementación se desarrolló con una **arquitectura propia**, utilizando un **backend personalizado** desplegado en un **VPS propio**, con el objetivo de tener mayor control sobre la autenticación, la base de datos, la generación de PDFs y la administración de usuarios.

---

## Objetivo de la aplicación

La aplicación permite que un usuario pueda:

- registrarse en el sistema,
- iniciar sesión,
- gestionar su acceso de forma autenticada,
- completar un formulario de cotización,
- generar un documento PDF,
- almacenar y consultar sus documentos desde una API propia.

Además, desde el backend se cuenta con rutas administrativas para revisar usuarios registrados y documentos generados, lo que permite validar el funcionamiento completo del sistema con herramientas como **Postman**.

---

## Tecnologías utilizadas

### App móvil

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Retrofit** para consumo de API REST
- **OkHttp / Gson** para conexión y serialización

### Backend

- **Node.js**
- **Express**
- **SQLite**
- **JWT** para autenticación
- **PDFKit** o motor equivalente para generación de PDF
- **Multer** para carga de imágenes
- **Docker / Docker Compose** para despliegue

### Infraestructura

- **VPS propio**
- **Repositorio en GitHub** para control de versiones y actualización del backend

---

## Arquitectura del sistema

El proyecto sigue una arquitectura cliente-servidor:

### 1. Cliente móvil Android
La aplicación Android actúa como cliente. Desde ella, el usuario:

- se registra,
- inicia sesión,
- completa formularios,
- solicita la generación de un PDF,
- recibe la respuesta de la API.

### 2. Backend propio
La API REST recibe las solicitudes desde la app, procesa la autenticación, guarda usuarios y documentos en la base de datos, genera los archivos PDF y devuelve las rutas públicas correspondientes.

### 3. Base de datos SQLite
Se utiliza SQLite para almacenar:

- usuarios registrados,
- documentos generados,
- datos asociados a cada cotización.

### 4. Almacenamiento local del servidor
En el VPS se guardan carpetas como:

- `data/` → base de datos SQLite
- `uploads/` → imágenes de usuarios
- `generated/` → PDFs generados

---

## ¿Por qué no se usó Firebase?

Aunque el requerimiento académico sugería el uso de Firebase, este proyecto fue desarrollado con una solución propia para demostrar:

- capacidad de diseñar una arquitectura completa,
- control total del backend,
- manejo personalizado de autenticación,
- despliegue en infraestructura real,
- generación de documentos PDF desde servidor,
- administración técnica fuera del ecosistema Firebase.

Esto permite evidenciar un enfoque más cercano a un entorno profesional real, donde la aplicación no depende de servicios administrados de terceros para sus funciones principales.

---

## Flujo de funcionamiento

### Registro
El usuario crea una cuenta desde la app. Sus datos se envían al backend y se almacenan en la base de datos.

### Login
El usuario inicia sesión con su correo y contraseña. La API devuelve un **token JWT**, que luego se usa para autorizar las solicitudes posteriores.

### Generación de documento
Una vez autenticado, el usuario completa los datos del cliente y de la cotización. La app envía esa información al backend, el backend genera un PDF y devuelve la URL pública del archivo.

### Consulta administrativa
Mediante rutas protegidas con una **API Key de administrador**, se pueden listar todos los usuarios y revisar los documentos generados por cada uno.

---

## Despliegue

La API está desplegada en un **VPS propio**, accesible públicamente mediante la dirección:

```text
http://62.169.22.80:3000/
```

La aplicación Android está configurada para conectarse a esa URL base.

### Variables de entorno utilizadas

```env
PORT=3000
JWT_SECRET=cesar
ADMIN_API_KEY=kjkszpj
PUBLIC_BASE_URL=http://62.169.22.80:3000
```

Estas variables permiten:

- definir el puerto del backend,
- firmar los tokens JWT,
- proteger rutas administrativas,
- construir URLs públicas para archivos generados.

---

## Verificación con Postman

Uno de los puntos importantes del proyecto es que toda la comunicación entre la app y el backend puede validarse con **Postman**, lo que permite demostrar que la arquitectura funciona correctamente incluso sin depender directamente de la interfaz móvil.

### Endpoints principales

#### Verificar estado de la API
```http
GET /api/health
```

#### Login
```http
POST /api/auth/login
```

#### Registro
```http
POST /api/auth/register
```

#### Generar PDF autenticado
```http
POST /api/documents
Authorization: Bearer <TOKEN>
```

#### Listar documentos del usuario autenticado
```http
GET /api/documents
Authorization: Bearer <TOKEN>
```

#### Listar usuarios registrados (admin)
```http
GET /api/admin/users
x-api-key: <ADMIN_API_KEY>
```

#### Ver documentos de un usuario (admin)
```http
GET /api/admin/users/:userId/documents
x-api-key: <ADMIN_API_KEY>
```

#### Generar PDF como administrador
```http
POST /api/admin/documents
x-api-key: <ADMIN_API_KEY>
```

---

## Características visibles en la app

- splash screen inicial,
- pantalla de bienvenida,
- registro con foto,
- login,
- panel general,
- formulario para generar cotizaciones,
- conexión con backend propio,
- generación de PDF,
- opción de **mostrar/ocultar contraseña** con ícono de ojo,
- validaciones antes de enviar información al servidor.

---

## Estructura general del proyecto

### App Android
```text
SecurityQuotesRemast-app/
```

### Backend
```text
securityquotes-api/
```

### Carpetas relevantes del backend
```text
securityquotes-api/data/
securityquotes-api/uploads/
securityquotes-api/generated/
```

---

## Sobre la interfaz Android

La interfaz no está desarrollada con archivos XML tradicionales en `res/layout`, sino con **Jetpack Compose**. Por eso, gran parte del diseño visual está definido directamente en archivos Kotlin.

Archivo principal de interfaz:

```text
app/src/main/java/com/cesar/securityquotes/ui/SecurityQuotesApp.kt
```

Punto de entrada:

```text
app/src/main/java/com/cesar/securityquotes/MainActivity.kt
```

Esto significa que la UI se construye mediante funciones composables, lo cual representa una forma moderna de desarrollar interfaces en Android.

---

## Valor técnico del proyecto

Este proyecto demuestra conocimientos de:

- desarrollo móvil nativo en Android,
- consumo de servicios REST,
- autenticación con JWT,
- backend personalizado,
- despliegue en VPS,
- uso de Docker,
- persistencia en base de datos,
- generación de documentos PDF,
- pruebas de API con Postman.

---

## Conclusión

**SecurityQuotesRemaster** no es solo una app móvil de prueba, sino una solución completa compuesta por cliente, servidor, base de datos y despliegue en infraestructura real.

La decisión de usar un backend propio en lugar de Firebase permite mostrar un mayor dominio técnico del sistema completo, desde la interfaz Android hasta la publicación y verificación de servicios en un VPS.

Es un proyecto que puede presentarse no solo como aplicación funcional, sino también como una demostración de arquitectura, integración y despliegue real.

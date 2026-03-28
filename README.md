# SecurityQuotesRemaster - Android

Aplicación móvil en Kotlin + Jetpack Compose para:

- splash animado
- bienvenida
- registro con foto
- login
- panel general
- generación de PDF conectada a la API del VPS
- mostrar / ocultar contraseña con ícono de ojo en login y registro

## URL configurada en esta versión

La app apunta a:

```text
http://62.169.22.80:3000/
```

## Correcciones aplicadas

- agregado el ícono de ojo para mostrar/ocultar contraseña
- mensajes de error HTTP más claros
- validación previa antes de generar PDF
- compatibilidad con la API corregida de documentos

## Ejecutar

1. Abre `SecurityQuotesRemast-app` en Android Studio.
2. Espera la sincronización de Gradle.
3. Compila e instala.
4. Verifica que el backend del VPS esté levantado en `http://62.169.22.80:3000`.

## Requisitos

- Android Studio Hedgehog o superior
- Min SDK 26
- Compile SDK 34

## Nota

Se usa HTTP intencionalmente para conectar al VPS sin SSL. El manifiesto ya permite tráfico cleartext.

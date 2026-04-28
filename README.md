# Aplicación de Despacho con Firebase

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** que permite autenticación de usuarios, obtención de ubicación GPS, cálculo automático del despacho según reglas de negocio y almacenamiento de datos en **Firebase Realtime Database**.

---

## Integrantes

| Nombre |
|--------|
| FRANCISCO SILVA |
| CATALINA URIBE|

---

## Información general del proyecto

| Ítem | Descripción |
|------|-------------|
| Nombre del proyecto | Aplicación de Despacho con Firebase |
| Plataforma | Android |
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose |
| Base de datos | Firebase Realtime Database |
| Autenticación | Firebase Authentication |
| Ubicación | GPS del dispositivo |
| Tipo de proyecto | Aplicación móvil académica |

---

## Descripción

Este proyecto consiste en una aplicación móvil diseñada para automatizar el cálculo del valor de despacho de productos según la distancia entre un origen y un destino, además del monto total de compra.  
La aplicación permite iniciar sesión con correo y contraseña, registrar usuarios, obtener la ubicación GPS del dispositivo y guardar información en la nube mediante Firebase.

La solución fue creada para responder al caso de una empresa distribuidora de alimentos, donde se requiere calcular el despacho de forma automática y almacenar los datos en una base de datos en tiempo real.

---

## Objetivo general

Desarrollar una aplicación móvil que permita autenticar usuarios, calcular el despacho de productos según reglas de negocio y guardar la información en Firebase.

---

## Objetivos específicos

| Objetivo | Descripción |
|----------|-------------|
| Autenticación | Permitir inicio de sesión con correo y contraseña |
| Registro | Permitir registrar nuevos usuarios |
| GPS | Obtener la ubicación actual del dispositivo |
| Cálculo | Calcular automáticamente el costo de despacho |
| Almacenamiento | Guardar datos en Firebase Realtime Database |
| Documentación | Registrar el proyecto en GitHub con README y evidencias |

---

## Reglas de negocio del despacho

| Condición | Cálculo aplicado |
|-----------|------------------|
| Compra mayor o igual a $50.000 y distancia menor o igual a 20 km | Despacho gratuito |
| Compra entre $25.000 y $49.999 | $150 por kilómetro |
| Compra menor a $25.000 | $300 por kilómetro |

---

## Funcionalidades principales

| Funcionalidad | Estado |
|---------------|--------|
| Login con correo y contraseña | Implementado |
| Registro de usuario | Implementado |
| Obtención de GPS | Implementado |
| Cálculo de distancia | Implementado |
| Cálculo de despacho | Implementado |
| Guardado en Realtime Database | Implementado |
| Cierre de sesión | Implementado |

---

## Historias de usuario

| ID | Historia de usuario | Estado |
|----|---------------------|--------|
| HU1 | Como usuario, quiero iniciar sesión para acceder a la aplicación | Completada |
| HU2 | Como usuario, quiero registrarme para usar el sistema | Completada |
| HU3 | Como usuario, quiero ingresar compra y destino para calcular el despacho | Completada |
| HU4 | Como usuario, quiero ver el resultado del despacho en pantalla | Completada |
| HU5 | Como sistema, quiero guardar la ubicación GPS del dispositivo | Completada |
| HU6 | Como usuario, quiero que los datos queden almacenados en la nube | Completada |

---

## Tecnologías utilizadas

| Tecnología | Uso |
|------------|-----|
| Kotlin | Lógica principal de la app |
| Jetpack Compose | Interfaz de usuario |
| Firebase Authentication | Inicio de sesión y registro |
| Firebase Realtime Database | Almacenamiento de datos |
| Google Play Services Location | Obtención de ubicación GPS |
| Android Studio | Entorno de desarrollo |

---

## Estructura del proyecto

| Archivo | Descripción |
|---------|-------------|
| `MainActivity.kt` | Pantalla de login y registro |
| `MenuActivity.kt` | Pantalla principal con cálculo de despacho |
| `AndroidManifest.xml` | Permisos y declaración de actividades |
| `build.gradle.kts` | Dependencias y configuración del proyecto |
| `libs.versions.toml` | Versiones de dependencias |
| `google-services.json` | Configuración de Firebase |

---

## Configuración de Firebase

Para este proyecto se configuró Firebase Authentication y Firebase Realtime Database.  
Las reglas utilizadas en la base de datos son las siguientes:

{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}

Estas reglas permiten que solo los usuarios autenticados puedan leer y escribir datos en la base de datos.



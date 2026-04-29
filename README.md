# Aplicación de Despacho con Firebase
![Logo](EVIDENCIAS/AIEP.jpg)


Proyecto móvil desarrollado en **Kotlin** con **Jetpack Compose**, que permite iniciar sesión con Firebase Authentication, obtener la ubicación GPS del dispositivo, calcular automáticamente el costo de despacho según reglas de negocio y almacenar la información en Firebase Realtime Database.

---

## Integrantes

| Nombre y Apellido |
|--------|
| FRANCISCO SILVA |
| CATALINA URIBE |

---

## Información general del proyecto

| Ítem | Descripción |
|------|-------------|
| Nombre del proyecto | Aplicación de Despacho con Firebase |
| Plataforma | Android Studio |
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose |
| Base de datos | Firebase Realtime Database |
| Autenticación | Firebase Authentication |
| Ubicación | GPS del dispositivo |
| Tipo de proyecto | Aplicación móvil académica |

---

## Descripción del proyecto

Este proyecto consiste en una aplicación móvil diseñada para automatizar el cálculo del valor de despacho de productos según la distancia entre un punto de origen y un destino ingresado por el usuario, además del monto total de compra.

La aplicación permite iniciar sesión con correo electrónico y contraseña, registrar usuarios, obtener la ubicación GPS del dispositivo y guardar información en la nube mediante Firebase.  
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

## Distribución de responsabilidades

| Actividad | Responsable |
|----------|-------------|
| Creación de la estructura inicial del proyecto | FRANCISCO |
| Configuración de Firebase Authentication | CATALINA |
| Implementación de la pantalla de login | FRANCISCO |
| Implementación del registro de usuario | CATALINA |
| Desarrollo de la pantalla principal | FRANCISCO |
| Cálculo de distancia y despacho | CATALINA |
| Integración del GPS del dispositivo | FRANCISCO |
| Guardado de datos en Realtime Database | CATALINA |
| Configuración de permisos en AndroidManifest | FRANCISCO |
| Diseño y organización de la interfaz | CATALINA |
| Redacción del README y documentación | FRANCISCO |
| Revisión final y evidencias | CATALINA |

---

## Punto de origen del cálculo

Para realizar el cálculo de distancia y despacho se estableció una coordenada fija como punto de origen.  
Este punto corresponde a la **Plaza Colón de Antofagasta**, y fue utilizado como referencia principal para medir la distancia hacia el destino ingresado por el usuario.

**Coordenada de origen usada en la aplicación:**

- Latitud: `-23.647022`
- Longitud: `-70.398159`

Esta ubicación se emplea como base para el cálculo de la distancia angular y posteriormente para determinar el valor del despacho según las reglas de negocio.

---

## Configuración de Firebase y seguridad

La base de datos **Firebase Realtime Database** fue configurada con reglas de seguridad que permiten lectura y escritura únicamente a usuarios autenticados:

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}

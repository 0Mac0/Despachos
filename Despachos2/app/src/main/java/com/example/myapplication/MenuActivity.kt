package com.example.myapplication

// Importaciones necesarias para permisos, UI, Firebase, ubicación, etc.
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlin.math.*

// Activity principal del menú
class MenuActivity : ComponentActivity() {

    // Firebase Auth (para usuario logueado)
    private val auth by lazy { FirebaseAuth.getInstance() }

    // Cliente para obtener ubicación GPS
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    // Coordenadas de origen (punto base del despacho)
    private val origenLat = -23.647022
    private val origenLng = -70.398159

    // Launcher para pedir permiso de ubicación
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) guardarGps() // Si acepta → guardamos ubicación
            else toast("Sin permiso GPS") // Si no → mensaje
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Se ejecuta apenas se carga la pantalla
            LaunchedEffect(Unit) {
                pedirPermisoGps()
            }

            // UI principal
            MenuScreen(
                onGuardarCalculo = { radianes, distanciaKm, despacho, total, lat, lng ->
                    guardarCalculo(radianes, distanciaKm, despacho, total, lat, lng)
                },
                onLogout = {
                    auth.signOut() // Cierra sesión
                    finish()       // Cierra la activity
                }
            )
        }
    }

    // Verifica si ya tiene permiso GPS
    private fun pedirPermisoGps() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            guardarGps() // Ya tiene permiso → obtener ubicación
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) // Pedir permiso
        }
    }

    // Obtiene y guarda la ubicación en Firebase
    private fun guardarGps() {
        val uid = auth.currentUser?.uid ?: return // Si no hay usuario → salir

        // Verificación de seguridad de permiso
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            toast("Sin permiso GPS")
            return
        }

        // Obtiene última ubicación conocida
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                val data = if (location != null) {
                    // Si hay ubicación real
                    mapOf(
                        "lat" to location.latitude,
                        "lng" to location.longitude,
                        "time" to ServerValue.TIMESTAMP
                    )
                } else {
                    // Si no hay ubicación → usa origen por defecto
                    mapOf(
                        "lat" to origenLat,
                        "lng" to origenLng,
                        "time" to ServerValue.TIMESTAMP
                    )
                }

                // Guarda en Firebase
                FirebaseDatabase.getInstance()
                    .reference.child("usuarios").child(uid)
                    .setValue(data)
                    .addOnSuccessListener { toast("GPS guardado") }
                    .addOnFailureListener { toast("Error al guardar GPS") }
            }
            .addOnFailureListener {
                toast("No se pudo obtener ubicación")
            }
    }

    // Guarda el cálculo de despacho en Firebase
    private fun guardarCalculo(
        radianes: Double,
        distanciaKm: Double,
        despacho: Double,
        total: Double,
        latDest: Double,
        lngDest: Double
    ) {
        val uid = auth.currentUser?.uid ?: return

        val datos = mapOf(
            "radianes" to radianes,
            "distanciaKm" to distanciaKm,
            "despacho" to despacho,
            "total" to total,
            "destinoLat" to latDest,
            "destinoLng" to lngDest,
            "origenLat" to origenLat,
            "origenLng" to origenLng,
            "timestamp" to ServerValue.TIMESTAMP
        )

        // Guarda en nodo "calculos"
        FirebaseDatabase.getInstance()
            .reference.child("calculos").child(uid).push()
            .setValue(datos)
            .addOnSuccessListener { toast("Cálculo guardado") }
            .addOnFailureListener { e -> toast(e.message ?: "Error") }
    }

    // Función para mostrar mensajes rápidos
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

// UI en Jetpack Compose
@Composable
fun MenuScreen(
    onGuardarCalculo: (Double, Double, Double, Double, Double, Double) -> Unit,
    onLogout: () -> Unit
) {
    // Estados de los inputs
    var totalCompra by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Bienvenido")

        // Input: total compra
        OutlinedTextField(
            value = totalCompra,
            onValueChange = { totalCompra = it },
            label = { Text("Total compra") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // Input: latitud destino
        OutlinedTextField(
            value = lat,
            onValueChange = { lat = it },
            label = { Text("Latitud destino") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // Input: longitud destino
        OutlinedTextField(
            value = lng,
            onValueChange = { lng = it },
            label = { Text("Longitud destino") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // Botón de cálculo
        Button(onClick = {
            val total = totalCompra.toDoubleOrNull()
            val latDestino = lat.toDoubleOrNull()
            val lngDestino = lng.toDoubleOrNull()

            // Validación básica
            if (total == null || latDestino == null || lngDestino == null) {
                resultado = "Datos inválidos"
                return@Button
            }

            // Cálculo de distancia
            val radianes = angularDistanceRadians(-23.647022, -70.398159, latDestino, lngDestino)
            val distanciaKm = radianes * 6371.0 // radio de la Tierra

            // Cálculo de despacho
            val despacho = calcularDespacho(total, distanciaKm)

            // Total final
            val totalFinal = total + despacho

            // Resultado mostrado en pantalla
            resultado = """
                Distancia: %.2f km
                Despacho: $%.0f
                Total final: $%.0f
            """.trimIndent().format(distanciaKm, despacho, totalFinal)

            // Guardar en Firebase
            onGuardarCalculo(radianes, distanciaKm, despacho, totalFinal, latDestino, lngDestino)
        }) {
            Text("Calcular despacho")
        }

        Text(resultado)

        // Botón logout
        Button(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}

// Fórmula Haversine → calcula distancia entre 2 puntos en la Tierra
fun angularDistanceRadians(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    return 2 * atan2(sqrt(a), sqrt(1 - a))
}

// Lógica de negocio del despacho
fun calcularDespacho(total: Double, km: Double): Double {
    return when {
        total >= 50000 && km <= 20 -> 0.0 // envío gratis
        total in 25000.0..49999.99 -> km * 150 // tarifa media
        else -> km * 300 // tarifa alta
    }
}
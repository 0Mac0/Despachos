package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MenuActivity : ComponentActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private val origenLat = -23.647022
    private val origenLng = -70.398159

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) guardarGps()
            else toast("Sin permiso GPS")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(Unit) {
                pedirPermisoGps()
            }

            MenuScreen(
                onGuardarCalculo = { radianes, distanciaKm, despacho, total, lat, lng ->
                    guardarCalculo(radianes, distanciaKm, despacho, total, lat, lng)
                },
                onLogout = {
                    auth.signOut()
                    finish()
                }
            )
        }
    }

    private fun pedirPermisoGps() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            guardarGps()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun guardarGps() {
        val uid = auth.currentUser?.uid ?: return

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            toast("Sin permiso GPS")
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                val data = if (location != null) {
                    mapOf(
                        "lat" to location.latitude,
                        "lng" to location.longitude,
                        "time" to ServerValue.TIMESTAMP
                    )
                } else {
                    mapOf(
                        "lat" to origenLat,
                        "lng" to origenLng,
                        "time" to ServerValue.TIMESTAMP
                    )
                }

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

        FirebaseDatabase.getInstance()
            .reference.child("calculos").child(uid).push()
            .setValue(datos)
            .addOnSuccessListener { toast("Cálculo guardado") }
            .addOnFailureListener { e -> toast(e.message ?: "Error") }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MenuScreen(
    onGuardarCalculo: (radianes: Double, distanciaKm: Double, despacho: Double, total: Double, lat: Double, lng: Double) -> Unit,
    onLogout: () -> Unit
) {
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

        OutlinedTextField(
            value = totalCompra,
            onValueChange = { totalCompra = it },
            label = { Text("Total compra") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = lat,
            onValueChange = { lat = it },
            label = { Text("Latitud destino") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = lng,
            onValueChange = { lng = it },
            label = { Text("Longitud destino") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Button(onClick = {
            val total = totalCompra.toDoubleOrNull()
            val latDestino = lat.toDoubleOrNull()
            val lngDestino = lng.toDoubleOrNull()

            if (total == null || latDestino == null || lngDestino == null) {
                resultado = "Datos inválidos"
                return@Button
            }

            val radianes = angularDistanceRadians(-23.647022, -70.398159, latDestino, lngDestino)
            val distanciaKm = radianes * 6371.0
            val despacho = calcularDespacho(total, distanciaKm)
            val totalFinal = total + despacho

            resultado = """
                Distancia: %.2f km
                Despacho: $%.0f
                Total final: $%.0f
            """.trimIndent().format(distanciaKm, despacho, totalFinal)

            onGuardarCalculo(radianes, distanciaKm, despacho, totalFinal, latDestino, lngDestino)
        }) {
            Text("Calcular despacho")
        }

        Text(resultado)

        Button(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}

fun angularDistanceRadians(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    return 2 * atan2(sqrt(a), sqrt(1 - a))
}

fun calcularDespacho(total: Double, km: Double): Double {
    return when {
        total >= 50000 && km <= 20 -> 0.0
        total in 25000.0..49999.99 -> km * 150
        else -> km * 300
    }
}
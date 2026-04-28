package com.example.myapplication

// Importaciones necesarias
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

// Activity principal (pantalla de login)
class MainActivity : ComponentActivity() {

    // Instancia de Firebase Auth (maneja login y registro)
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se define la UI usando Jetpack Compose
        setContent {
            LoginScreen(
                // Acción cuando el usuario quiere iniciar sesión
                onLogin = { email, password ->
                    login(email, password)
                },
                // Acción cuando el usuario quiere registrarse
                onRegister = { email, password, onDone ->
                    register(email, password, onDone)
                }
            )
        }
    }

    // Función para iniciar sesión
    private fun login(email: String, password: String) {

        // Validación básica
        if (email.isBlank() || password.isBlank()) {
            toast("Completa correo y contraseña")
            return
        }

        // Firebase login
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                toast("Inicio de sesión exitoso")

                // Navega a MenuActivity
                startActivity(Intent(this, MenuActivity::class.java))

                // Cierra esta pantalla para que no vuelva atrás
                finish()
            }
            .addOnFailureListener { e ->
                toast(e.message ?: "Error al iniciar sesión")
            }
    }

    // Función para registrar usuario
    private fun register(email: String, password: String, onDone: () -> Unit) {

        // Validación básica
        if (email.isBlank() || password.isBlank()) {
            toast("Completa correo y contraseña")
            return
        }

        // Firebase registro
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                toast("Usuario registrado. Ahora inicia sesión.")

                // Cierra sesión automáticamente (para obligar login manual)
                auth.signOut()

                // Callback para limpiar campos en la UI
                onDone()
            }
            .addOnFailureListener { e ->
                toast(e.message ?: "Error al registrar")
            }
    }

    // Función helper para mostrar mensajes
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

// Composable que representa la pantalla de login
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, onDone: () -> Unit) -> Unit
) {
    // Estados para inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login Firebase")

        // Campo de correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it }, // Actualiza estado
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it }, // Actualiza estado
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Botón para iniciar sesión
        Button(onClick = { onLogin(email.trim(), password) }) {
            Text("Ingresar")
        }

        // Botón para registrar usuario
        Button(onClick = {
            onRegister(email.trim(), password) {
                // Limpia los campos después del registro
                email = ""
                password = ""
            }
        }) {
            Text("Registrar")
        }
    }
}
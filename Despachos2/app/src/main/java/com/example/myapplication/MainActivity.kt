package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(
                onLogin = { email, password ->
                    login(email, password)
                },
                onRegister = { email, password, onDone ->
                    register(email, password, onDone)
                }
            )
        }
    }

    private fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            toast("Completa correo y contraseña")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                toast("Inicio de sesión exitoso")
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                toast(e.message ?: "Error al iniciar sesión")
            }
    }

    private fun register(email: String, password: String, onDone: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            toast("Completa correo y contraseña")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                toast("Usuario registrado. Ahora inicia sesión.")
                auth.signOut()
                onDone()
            }
            .addOnFailureListener { e ->
                toast(e.message ?: "Error al registrar")
            }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, onDone: () -> Unit) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login Firebase")

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(onClick = { onLogin(email.trim(), password) }) {
            Text("Ingresar")
        }

        Button(onClick = {
            onRegister(email.trim(), password) {
                email = ""
                password = ""
            }
        }) {
            Text("Registrar")
        }
    }
}
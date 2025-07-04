package com.shrimpdevs.digitalassistant.screens.presentation

import android.R.attr.tint
import android.R.color.white
import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // Aunque no se usa en este fragmento, si lo tenías, mantenlo
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height // Aunque no se usa en este fragmento, si lo tenías, mantenlo
import androidx.compose.foundation.layout.padding // Nueva: para el modificador padding
import androidx.compose.material3.Button // Nueva: si usas Button más adelante
import androidx.compose.material3.Icon // Correcta: para el componente Icon de Material3
import androidx.compose.material3.Text // Correcta: para el componente Text de Material3
import androidx.compose.material3.TextField // Nueva: para el componente TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Nueva: para 'by remember'
import androidx.compose.runtime.mutableStateOf // Nueva: para 'mutableStateOf'
import androidx.compose.runtime.remember // Nueva: para 'remember'
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shrimpdevs.digitalassistant.ui.theme.Black
import com.shrimpdevs.digitalassistant.ui.theme.DarkBlue
import com.shrimpdevs.digitalassistant.ui.theme.White
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth // Correcta: si usas FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.shrimpdevs.digitalassistant.R // Correcta: para recursos R.drawable
import com.shrimpdevs.digitalassistant.ui.theme.BLueLight
import com.shrimpdevs.digitalassistant.ui.theme.BackgroundTextField
import com.shrimpdevs.digitalassistant.ui.theme.DarkText
import com.shrimpdevs.digitalassistant.ui.theme.GrayL
import com.shrimpdevs.digitalassistant.ui.theme.SelectedField
import com.shrimpdevs.digitalassistant.ui.theme.ShapeButton
import com.shrimpdevs.digitalassistant.ui.theme.UnselectedField
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation


@Composable
fun LoginScreen(
    navigateToInitial: () -> Unit,
    navigateToEvent: () -> Unit,
    auth: FirebaseAuth
) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBlue, Black)))
            .padding(horizontal = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        //Flecha de retroceso
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back Icon",
            tint = White,
            modifier = Modifier
                .padding(top = 20.dp)
                .clickable {navigateToInitial()}
                .align(Alignment.Start)
                .size(45.dp)
                .shadow(10.dp, shape = RoundedCornerShape(15.dp))
        )

        // LOGO y Titulo
        Image(
            painter = painterResource(id =R.drawable.logo_da),
            contentDescription = "Logo de Aplicacion DA"
        )
        Text(
            text = "Digital Assistant",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.weight(1f))

        // campo de correo
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp) // Espacio entre el icono y el campo de texto
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = "Email Icon",
                tint = GrayL
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                // texto blanco
                textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                placeholder = { Text("Correo", color = GrayL, fontWeight = FontWeight.Light, fontSize = 20.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundTextField,
                    focusedContainerColor = SelectedField,
                ),
                // Elimina el borde por defecto
                shape = RoundedCornerShape(0.dp)


            )

        }


        //Campo de contraseña

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp) // Espacio entre el icono y el campo de texto
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_email),/*aca agreguen plis el ic_loc que es un candadito*/
                contentDescription = "Email Icon",
                tint = GrayL
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                placeholder = { Text("Contraseña", color = GrayL, fontWeight = FontWeight.Light, fontSize = 20.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundTextField,
                    focusedContainerColor = SelectedField,
                ),
                // Elimina el borde por defecto
                shape = RoundedCornerShape(0.dp)
            )


        }

        // Agregar estas variables de estado
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        // En el botón de inicio de sesión
        Button(
            onClick = {
                isLoading = true
                errorMessage = null

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Log.d("Login", "Inicio de sesión exitoso. Usuario: ${auth.currentUser?.email}")
                            // Limpiar cualquier mensaje de error previo
                            errorMessage = null
                            navigateToEvent()
                        } else {
                            val message = when(task.exception) {
                                is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas: usuario o contraseña incorrectos"
                                is FirebaseAuthInvalidUserException -> "Usuario no encontrado: el correo electrónico no está registrado"
                                else -> "Error de conexión. Por favor, verifica tu conexión a internet"
                            }
                            errorMessage = message
                            Log.e("Login", "Error: $message", task.exception)
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
            },
            enabled = !isLoading, // Deshabilita el botón mientras se procesa
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 32.dp)
                .shadow(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkText,
                disabledContainerColor = DarkText.copy(alpha = 0.6f)
            )
        ) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Entrar",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }

        //Mostrar mensaje de error si existe
        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        //Texto te olvidaste tu contraseña
        Text(
            text = "¿Te olvidaste tu contraseña?",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable { /* Navegar a la pantalla de recuperación de contraseña */ }
        )


        Spacer(modifier = Modifier.weight(2f))

    }
}

package com.shrimpdevs.digitalassistant.screens.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shrimpdevs.digitalassistant.R
import com.shrimpdevs.digitalassistant.ui.theme.BackgroundTextField
import com.shrimpdevs.digitalassistant.ui.theme.Black
import com.shrimpdevs.digitalassistant.ui.theme.DarkBlue
import com.shrimpdevs.digitalassistant.ui.theme.DarkText
import com.shrimpdevs.digitalassistant.ui.theme.GrayL
import com.shrimpdevs.digitalassistant.ui.theme.SelectedField
import com.shrimpdevs.digitalassistant.ui.theme.White

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.shrimpdevs.digitalassistant.ui.theme.BLueLight
import com.shrimpdevs.digitalassistant.ui.theme.Gray
import com.shrimpdevs.digitalassistant.ui.theme.LightBlue

@Composable
fun SignUpScreen(
    navigateToInitial: () -> Unit,
    navigateToEvent: () -> Unit,
    auth: FirebaseAuth
) {
    //var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    val context = LocalContext.current

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
                .clickable { navigateToInitial()}
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

/*
        //Nombre de usuario
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp) // Espacio entre el icono y el campo de texto
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_account),
                contentDescription = "user",
                tint = GrayL
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Nombre", color = GrayL, fontWeight = FontWeight.Light, fontSize = 20.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundTextField,
                    focusedContainerColor = SelectedField,
                ),
                shape = RoundedCornerShape(0.dp)


            )

        }*/

        // campo de correo
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
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
                textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                placeholder = { Text("Correo", color = GrayL, fontWeight = FontWeight.Light, fontSize = 20.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundTextField,
                    focusedContainerColor = SelectedField,
                ),
                // elimina el borde por defecto del fieldtext
                shape = RoundedCornerShape(0.dp)


            )

        }


        //Campo de contraseña

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_panel_admin/*aca agreguen plis el ic_loc.xml que es un candadito */),
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




        // Boton de inicio de registar
        Button(
            onClick = {
                isLoading = true // Indica que la operación está en curso
                errorMessage = null // Limpia cualquier mensaje de error previo

                //Requisitos  de campos
                if (email.isEmpty() || password.isEmpty()) {
                    Log.e("SignUp", "Email y contraseña son requeridos")
                    return@Button
                }
                if (password.length < 6) {
                    Log.e("SignUp", "La contraseña debe tener al menos 6 caracteres")
                    return@Button
                }
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Log.i("SignUp", "User created successfully")
                            Toast.makeText(context, "Registro exitoso. ¡Bienvenido!", Toast.LENGTH_SHORT).show()
                            navigateToEvent() // Navega a la pantalla de inicio
                        } else {
                            // Manejo de errores específicos
                            val message = when (task.exception) {
                                is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico es inválido."
                                is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está registrado."
                                else -> "Error al registrar: ${task.exception?.message ?: "Desconocido"}"
                            }
                            var errorMessage = message
                            Log.e("SignUp", "Error creating user: $message")
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }

            },
            enabled = !isLoading, // Deshabilita el botón mientras se está cargando
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 32.dp) // Usa el padding original
                .shadow(10.dp, shape = RoundedCornerShape(15.dp)), // Aplica la sombra y el shape
            // Color de fondo del boton
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkText, // Cambiado a BLueLight para diferenciarlo del Login
                disabledContainerColor = LightBlue.copy(alpha = 0.10f), // Color cuando está deshabilitado
                // Color del texto del botón
                disabledContentColor = Gray.copy(alpha = 0.10f),
            ),

            // Establecer radio de borde
            shape = RoundedCornerShape(15.dp),
            contentPadding = PaddingValues(
                top = 15.dp,
                bottom = 15.dp,

                )
        ) {
            Text(
                text = "Registrar", color = Color.White,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.weight(2f))

    }
}
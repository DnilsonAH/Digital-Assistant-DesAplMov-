package com.shrimpdevs.digitalassistant.screens.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.shrimpdevs.digitalassistant.R
import com.shrimpdevs.digitalassistant.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val googleSignInClient = remember { getGoogleSignInClient(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBlue, Black)))
            .padding(horizontal = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Ajustes", color = White) },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Regresar",
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = DarkBlue
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                signOut(googleSignInClient) {
                    navHostController.navigate("initial") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkText)
        ) {
            Text("Cerrar Sesión", fontSize = 16.sp)
        }
    }
}

private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

private fun signOut(googleSignInClient: GoogleSignInClient, onComplete: () -> Unit) {

    // Primero cerrar sesión en Firebase
    FirebaseAuth.getInstance().signOut()

    // Luego revocar acceso y cerrar sesión de Google
    googleSignInClient.revokeAccess().addOnCompleteListener {
        googleSignInClient.signOut().addOnCompleteListener {
            onComplete()
        }
    }
}
package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.ui.components.FormCard
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

class SignIn {
    suspend fun onSignIn(email: String?, password: String?, navController: NavController) {
        Log.d("Initiating ", "Logging In")
        if (email.isNullOrBlank()) {
            throw Error("Empty Mail ID")
        }
        if (!email.isValidEmail()) {
            throw Error("Invalid E-Mail ID")
        }
        if (password.isNullOrBlank()) {
            throw Error("Empty Password")
        }
        try {
            val authResult =
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            Log.d("Finished", "Logging In")
            if (authResult == null) {
                Log.w("SignIn", "signInWithEmail:failure NullResponse")
                throw Error("Unknown Error: Couldn't Sign In")
            } else {
                navController.navigate("/home")
            }
        } catch (e: FirebaseAuthException) {
            throw Error(e.message)
        }
    }

    @Composable
    fun Create(modifier: Modifier = Modifier, navController: NavController) {
        UI(modifier, navController)
    }

    @Composable
    @Preview
    fun UI(modifier: Modifier = Modifier, navController: NavController = rememberNavController()) {
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier.padding(bottom = 10.dp),
                    painter = painterResource(id = R.drawable.group_155),
                    contentDescription = "Locate"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Welcome Back",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp,
                        fontFamily = monsterratFamily,
                        color = Color(0xffdadada)
                    )
                    Text(
                        "Log in to access your account",
                        fontWeight = FontWeight.Light,
                        fontSize = 15.sp,
                        fontFamily = monsterratFamily,
                        color = Color(0xffdadada)
                    )
                }
            }
            FormCard().Create(
                fields = listOf(
                    FormCard.FormCardField.TextField(
                        label = "Email",
                        key = "email",
                        Icons.Default.Person,
                        isSingleline = false
                    ),
                    FormCard.FormCardField.PasswordField(label = "Password", key = "password"),
                ),
                navController = navController,
                actionName = "Sign In"
            ) { data ->
                onSignIn(
                    email = data["email"],
                    password = data["password"],
                    navController
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "New Member?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = monsterratFamily,
                    color = Color.White
                )
                TextButton(onClick = { navController.navigate("/sign_up") }) {
                    Text("Sign Up Now", color = Color(0xffff6b35))
                }
            }
        }
    }
}

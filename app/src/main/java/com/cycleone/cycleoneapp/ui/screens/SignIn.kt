package com.cycleone.cycleoneapp.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.PrestyledText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignIn {
    fun onSignIn(context: Context, email: String, password: String) {
        Log.d("Initiating ", "Logging In")
        Log.i("Creds", "Username:  $email")
        Log.i("Creds", "Password:  $password")
        print(email)
        print(password)
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error while doing authentication$it",
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnSuccessListener { authResult ->

                    Log.d("Finished", "Logging In")
                    if (authResult == null) {
                        Toast.makeText(
                            context,
                            "Error while doing authentication",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        NavProvider.controller.navigate("/home")
                    }
                }
        } catch (e: Error) {
            Toast.makeText(
                context,
                "Error while doing authentication",
                Toast.LENGTH_LONG
            ).show()
            Log.e("SignIn", e.toString())
        }
    }

    @Composable
    fun Create(modifier: Modifier = Modifier) {
        UI(modifier, NavProvider.controller)
    }

    @Composable
    @Preview
    fun UI(modifier: Modifier = Modifier, navController: NavController = rememberNavController()) {
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                alpha = if (isSystemInDarkTheme()) {
                    0.0F
                } else {
                    1.0F
                }, painter = painterResource(id = R.drawable.locate), contentDescription = "Locate"
            )
            Text("Welcome Back", style = MaterialTheme.typography.titleMedium)
            Text("login to access your account", style = MaterialTheme.typography.labelMedium)
            PrestyledText().Regular(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = "Enter your Email",
                onChange = { x -> email = x },
                label = "Mail",
                icon = Icons.Outlined.Email
            )
            PrestyledText().Password(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = "Password", onChange = { x ->
                    password = x

                }, label = "Password", icon = Icons.Outlined.Lock
            )
            Text(
                "Forgot Password?",
                modifier = Modifier
                    .padding(horizontal = 55.dp, vertical = 10.dp)
                    .align(AbsoluteAlignment.Left),
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        onSignIn(
                            context = context,
                            email = email,
                            password = password
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.75F),
                shape = RoundedCornerShape(15.dp)

            ) {
                Text("Sign In")
            }
            TextButton(onClick = { navController.navigate("/sign_up") }) {
                Text("Not Registered? Sign Up", modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.PrestyledText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration

class SignIn {
    @Composable
    @Preview
    public fun Create() {
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var navController = NavProvider.controller
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextButton(onClick = {navController.popBackStack()}, modifier = Modifier
                .background(Color.Transparent)
                .align(AbsoluteAlignment.Left)) {
                Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
            }
            Image(painter = painterResource(id = R.drawable.locate), "Locate")
            Text("Welcome Back", style = MaterialTheme.typography.titleLarge)
            Text("login to access your account", style = MaterialTheme.typography.labelMedium)
            PrestyledText().Regular(placeholder = "Enter your Email", onChange = {x -> email = x}, label = "Mail", icon = Icons.Default.Email)
            PrestyledText().Password(placeholder = "Password", onChange = {x ->
                password = x
                Log.d("Password",password)

                                                                          }, label = "Password", icon = Icons.Default.Lock)
                Text("Forgot Password?",

                    modifier = Modifier
                        .padding(horizontal = 55.dp, vertical = 10.dp)
                        .align(AbsoluteAlignment.Left),
                    )
            Button(onClick = {
                        Log.d("Initiating ", "Logging In")
                Log.i("Creds", "Username:  $email")
                Log.i("Creds", "Password:  $password")
                print(email)
                print(password)
                        val authResult =
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnFailureListener {
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
                                    navController.navigate("/home")
                            }  }
                             }, modifier = Modifier.fillMaxWidth(0.75F)
                ,shape = RoundedCornerShape(15.dp)

            ) {
                Text("Sign In")
            }
            TextButton(onClick = {navController.navigate("/sign_up")}) {
                Text("Not Registered?, Sign Up", modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

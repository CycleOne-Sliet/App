package com.cycleone.cycleoneapp.ui.screens

import android.content.Context
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
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.PrestyledText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.UserProfileChangeRequest

class SignUp {
    @Composable
    @Preview
    public fun Create() {
        val navController = NavProvider.controller
        var name by remember {
            mutableStateOf("")
        }
        var email by remember {
            mutableStateOf("")
        }
        var phone_number by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }

        val context = LocalContext.current

       Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
           TextButton(onClick = {navController.popBackStack()}, modifier = Modifier
               .background(Color.Transparent)
               .align(AbsoluteAlignment.Left)) {
               Text("‹", fontSize = 50.sp, style = MaterialTheme.typography.titleLarge)
           }
           Image(painter = painterResource(id = R.drawable.locate), "Locate")
           Text("Get Started", style = MaterialTheme.typography.titleLarge)
           Text("by creating a account", style = MaterialTheme.typography.labelMedium)
           PrestyledText().Regular(placeholder = "Full Name", onChange = {x -> name = x}, label = "Full Name", icon = Icons.Default.Person)
           PrestyledText().Regular(placeholder = "Email", onChange = {x -> email = x}, label = "Mail", icon = Icons.Default.Email)
           // PrestyledText().Regular(placeholder = "Phone Number", onChange = {x -> phone_number = x}, label = "Phone Number", icon = Icons.Default.Phone)
           PrestyledText().Password(placeholder = "Password", onChange = {x -> password = x}, label = "Password", icon = Icons.Default.Lock)
           Row( modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
               Checkbox(checked = false, onCheckedChange = {})
               Text("By checking the box, you agree to our Terms and Conditions.", style = MaterialTheme.typography.labelSmall)
           }
           Button(onClick = {
                            val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
                                    authResult.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
                                    navController.navigate("/home")
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Error while creating account: $it", Toast.LENGTH_LONG).show()
                            }

           }, modifier = Modifier.fillMaxWidth(0.75F), shape = RoundedCornerShape(15.dp)
           ) {
               Text("Sign Up")
           }
           TextButton(onClick = { navController.navigate("/sign_in") }) {
               Text("Already a Member?, Log In")
           }
       }
    }
}
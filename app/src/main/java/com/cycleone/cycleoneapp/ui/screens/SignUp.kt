package com.cycleone.cycleoneapp.ui.screens

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch


class SignUp {
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun onSignUp(
        context: Context,
        email: String,
        password: String,
        password2: String,
        name: String,
        termsCondition: Boolean
    ) {
        if (email.isBlank()) {
            Toast.makeText(context, "Empty Mail ID", Toast.LENGTH_LONG).show()
            return
        }
        if (!email.isValidEmail()) {
            Toast.makeText(context, "Invalid E-Mail ID", Toast.LENGTH_LONG).show()
            return

        }
        if (password.isBlank()) {
            Toast.makeText(context, "Empty Password", Toast.LENGTH_LONG).show()
            return
        }
        if (!termsCondition) {
            Toast.makeText(context, "Check the terms and conditions", Toast.LENGTH_LONG).show()
            return
        }
        if (password.length < 8) {
            Toast.makeText(
                context,
                "Password should be atleast 8 characters",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (password != password2) {
            Toast.makeText(
                context,
                "Passwords do not match",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        try {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(name)
                            .build()
                    )
                    NavProvider.controller.navigate("/home")
                }.addOnFailureListener {
                    Log.e("Sign In", it.toString())
                    Toast.makeText(
                        context,
                        "Unable to create account",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } catch (e: Error) {
            Toast.makeText(
                context,
                "Unable to create account",
                Toast.LENGTH_LONG
            ).show()
            Log.e("SignUp", e.toString())
        }
    }

    @Composable
    fun Create(modifier: Modifier = Modifier) {
        UI(modifier, navController = NavProvider.controller)
    }

    @Composable
    @Preview
    fun UI(modifier: Modifier = Modifier, navController: NavController = rememberNavController()) {
        var name by remember {
            mutableStateOf("")
        }
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var password2 by remember {
            mutableStateOf("")
        }
        var termsCondition by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val scrollState  = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize().verticalScroll(scrollState)
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
            Text("Get Started", style = MaterialTheme.typography.titleMedium)
            Text("by creating a account", style = MaterialTheme.typography.labelMedium)
            PrestyledText().Regular(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = "Full Name",
                onChange = { x -> name = x },
                label = "Full Name",
                icon = Icons.Outlined.Person
            )
            PrestyledText().Regular(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = "Email",
                onChange = { x -> email = x },
                label = "Mail",
                icon = Icons.Outlined.Email
            )
            // PrestyledText().Regular(placeholder = "Phone Number", onChange = {x -> phone_number = x}, label = "Phone Number", icon = Icons.Default.Phone)
            PrestyledText().Password(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = "Password",
                onChange = { x -> password = x },
                label = "Password",
                icon = Icons.Outlined.Lock
            )
            PrestyledText().Password(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                placeholder = "Confirm Password",
                onChange = { x -> password2 = x },
                label = "Confirm Password",
                icon = Icons.Outlined.Lock
            )
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsCondition,
                    onCheckedChange = { termsCondition = it })
                Text(
                    "By checking the box, you agree to our Terms and Conditions.",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        onSignUp(
                            context,
                            email = email,
                            password = password,
                            password2 = password2,
                            name = name,
                            termsCondition
                        )
                    }
                }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium
            ) {
                Text("Sign Up", style = MaterialTheme.typography.bodyLarge)
            }
            TextButton(onClick = {
                navController.navigate("/sign_in")
            }) {
                Text("Already a Member? Log In")
            }
        }
    }
}
package com.cycleone.cycleoneapp.ui.screens

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.cycleone.cycleoneapp.ui.components.PrestyledText
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


class SignUp {
    fun CharSequence?.isValidEmail() =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun onSignUp(
        context: Context,
        email: String,
        password: String,
        password2: String,
        name: String,
        termsCondition: Boolean,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        if (name.isBlank()) {
            Toast.makeText(context, "Empty Name", Toast.LENGTH_LONG).show()
            return
        }
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
            onStart()
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email.filterNot { it.isWhitespace() }, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(name)
                            .build()
                    )
                    authResult.user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Verification Email sent",
                                Toast.LENGTH_LONG
                            ).show()

                            Toast.makeText(
                                context,
                                "Check your email",
                                Toast.LENGTH_LONG
                            ).show()
                        }?.addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Unable to send verification email",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }.addOnFailureListener {
                    Log.e("Sign In", it.toString())
                    Toast.makeText(
                        context,
                        "Unable to create account",
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnCompleteListener {
                    onComplete()
                }.addOnSuccessListener {
                    onSuccess()
                }
        } catch (e: Error) {
            Toast.makeText(
                context,
                "Account already created",
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
        var loading by remember {
            mutableStateOf(false)
        }
        var accountCreated by remember {
            mutableStateOf(false)
        }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        AnimatedVisibility(accountCreated) {
            LaunchedEffect(Unit) {
                while(true) {
                    delay(3.seconds)
                    navController.navigate("/home")
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = painterResource(R.drawable.sticker), contentDescription = "Success")
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.8F)
                        .padding(top = 10.dp),
                    text = "Account Created Successfully",
                    fontWeight = FontWeight.Medium,
                    fontSize = 30.sp,
                    fontFamily = monsterratFamily,
                    textAlign = TextAlign.Center,
                    color = Color(0xffdadada)
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.8F)
                        .padding(top = 10.dp),
                    text = "Make sure to verify your account by clicking on the link sent to your email",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    fontFamily = monsterratFamily,
                    textAlign = TextAlign.Center,
                    color = Color(0xffdadada)
                )
            }
        }
        AnimatedVisibility(!accountCreated) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 30.dp, vertical = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    alpha = if (isSystemInDarkTheme()) {
                        0.0F
                    } else {
                        1.0F
                    },
                    painter = painterResource(id = R.drawable.group_155),
                    contentDescription = "Locate"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Get Started",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                    Text(
                        "by creating a account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                }
                Column {
                    PrestyledText().Regular(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        placeholder = "Full Name",
                        onChange = { x -> name = x },
                        icon = Icons.Outlined.Person
                    )
                    PrestyledText().Regular(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        placeholder = "Email",
                        onChange = { x -> email = x },
                        icon = Icons.Outlined.Email
                    )
                    // PrestyledText().Regular(placeholder = "Phone Number", onChange = {x -> phone_number = x}, label = "Phone Number", icon = Icons.Default.Phone)
                    PrestyledText().Regular(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        placeholder = "Password",
                        onChange = { x -> password = x },
                        isPassword = true,
                        icon = Icons.Outlined.Lock
                    )
                    PrestyledText().Regular(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        placeholder = "Confirm Password",
                        onChange = { x -> password2 = x },
                        isPassword = true,
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
                            color = Color.White, fontSize = 12.sp,
                        )
                    }
                }
                if (loading) {
                    CircularProgressIndicator()
                } else {

                    FancyButton(
                        enabled = !loading,
                        onClick = {
                            coroutineScope.launch {
                                onSignUp(
                                    context,
                                    email = email,
                                    password = password,
                                    password2 = password2,
                                    name = name,
                                    termsCondition,
                                    onStart = {
                                        loading = true
                                    },
                                    onComplete = {
                                        loading = false
                                    }, onSuccess = {
                                        accountCreated = true
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        text = "Sign Up"
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Already a member? ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = monsterratFamily,
                        color = Color.White
                    )
                    TextButton(onClick = { navController.navigate("/sign_in") }) {
                        Text("Log In", color = Color(0xffff6b35))
                    }
                }
            }
        }

    }
}
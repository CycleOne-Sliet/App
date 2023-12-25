@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.cycleone.ttest2.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cycleone.ttest2.R
import com.cycleone.ttest2.ui.theme.MaterialTextInput
import com.google.firebase.auth.FirebaseAuth

class SignIn(private val controller: NavHostController, private val authController: FirebaseAuth) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Login(modifier: Modifier = Modifier) {
        var showingError by rememberSaveable {
            mutableStateOf(false)
        }
        var mostRecentError by rememberSaveable {
            mutableStateOf("")
        }
        if (showingError) {
            AlertDialog(onDismissRequest = {showingError = false;}, content = { Text(mostRecentError) }, properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true))
        }
        var username by rememberSaveable {
            mutableStateOf("")
        }
        var password by rememberSaveable {
            mutableStateOf("")
        }
        Column() {
            Button(onClick = { controller.popBackStack() }) {
                Image(
                    painter = painterResource(id = R.drawable.left_arrow),
                    contentDescription = "left-arrow",
                    modifier = Modifier
                        .requiredSize(size = 30.dp)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.map),
                contentDescription = "bike",
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .requiredWidth(width = 195.dp)
                    .requiredHeight(height = 215.dp)
            )
            Text(
                text = "Welcome Back",
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)

            )

            Text(
                text = "Log in to access your account",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .requiredWidth(width = 300.dp)
                    .requiredHeight(height = 154.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                MaterialTextInput().RegularTextInput(
                    label = "Enter your E-Mail",
                    icon = Icons.Outlined.Mail,
                    onInputChange = { new_username: String -> username = new_username }
                )
                MaterialTextInput().PasswordInput(onPasswordChange = { new_password: String ->
                    password = new_password
                })

            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .requiredWidth(width = 300.dp)
                .fillMaxHeight(0.5F),

            ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 13.sp
                        )
                    ) { append("New Member?") }
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff6c63ff),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append("Sign Up Now") }
                },
                modifier = Modifier.offset(
                    x = 40.dp,
                    y = 600.dp
                )
            )
            Button(
                onClick = {
                    val missingFields = mutableListOf<String>();
                    if (username.isBlank()) {
                        missingFields += "Username";
                    }
                    if (password.isBlank()) {
                        missingFields += "Password";
                    }
                    if (missingFields.isNotEmpty()) {
                        mostRecentError = "The following fields are not filled\n";
                        showingError = true;
                    }
                    for (key: String in missingFields) {
                        mostRecentError += "- ${key}\n"
                    };
                    if (missingFields.isNotEmpty()) {
                        return@Button
                    }

                    try {
                        authController.signInWithEmailAndPassword(username, password)
                            .addOnSuccessListener { controller.navigate("/dashboard") }
                    } catch (e: Exception) {
                        Log.e("Error: ", e.toString());
                    }
                }, modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .requiredWidth(width = 340.dp)
                    .requiredHeight(height = 56.dp)
            ) {
                Text(
                    text = "Sign In",
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun LoginPreview() {
    SignIn(rememberNavController(), FirebaseAuth.getInstance()).Login(Modifier)
}
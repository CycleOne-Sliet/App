@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.cycleone.ttest2.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.ttest2.R
import com.cycleone.ttest2.ui.theme.MaterialTextInput
import com.google.firebase.auth.FirebaseAuth

class SignUp(private val controller: NavController, private val authController: FirebaseAuth) {
    @Composable
    fun CreateAccount() {
        var showingError by rememberSaveable {
            mutableStateOf(false)
        }
        var mostRecentError by rememberSaveable {
            mutableStateOf("")
        }
        if (showingError) {
            AlertDialog(onDismissRequest = {showingError = false;}, content = { Text(mostRecentError) }, properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true))
        }

        var name by rememberSaveable {
            mutableStateOf("")
        }
        var password by rememberSaveable {
            mutableStateOf("")
        }
        var email by rememberSaveable {
            mutableStateOf("")
        }
        var phone by rememberSaveable {
            mutableStateOf("")
        }
        Column(verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.scrollable(
            ScrollableState { x: Float -> x }, Orientation.Vertical )) {
            Button(onClick = { controller.popBackStack() }, modifier = Modifier
                .align(Alignment.Start)
                .offset(x = 20.dp)) {
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
            Column() {
                Text(
                    text = "Get Started",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = "by creating a account",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .requiredWidth(width = 300.dp)
                    .fillMaxHeight(0.5F),

            ) {
                MaterialTextInput().RegularTextInput(
                    label = "Full Name",
                    icon = Icons.Outlined.Person,
                    onInputChange = { new_name: String -> name = new_name }
                )
                MaterialTextInput().RegularTextInput(
                    label = "Email",
                    icon = Icons.Outlined.Email,
                    onInputChange = { new_email: String -> email = new_email })
                MaterialTextInput().RegularTextInput(
                    label = "Phone Number",
                    icon = Icons.Outlined.Phone,
                    onInputChange = { new_phone: String -> phone = new_phone })
                MaterialTextInput().PasswordInput(onPasswordChange = { new_password: String ->
                    password = new_password
                })
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .requiredWidth(width = 300.dp)
                    .fillMaxHeight(0.5F),

                ) {
                Button(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .fillMaxWidth(0.8F)
                        .requiredHeight(height = 50.dp),
                    onClick = {
                        val missingFields = mutableListOf<String>();
                        if (name.isBlank()) {
                            missingFields += "Username";
                        }
                        if (password.isBlank()) {
                            missingFields += "Password";
                        }
                        if (email.isBlank()) {
                            missingFields += "Email";
                        }
                        if (phone.isBlank()) {
                            missingFields += "Phone";
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
                            authController.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { controller.navigate("/dashboard") }
                        } catch (e: Error) {
                            Log.e("Error: ", e.toString())
                        }
                    }
                ) {
                    Text(
                        text = "Sign up",
                        style = TextStyle(
                            fontSize = 20.sp
                        ),
                    )
                }
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 13.sp
                        )
                    ) { append("Already a member?") }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append(" ") }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff6c63ff),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append("Log In") }
                },
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .clickable(onClick = { controller.navigate("/sign_in") })
            )
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun CreateAccountPreview() {
    SignUp(rememberNavController(), FirebaseAuth.getInstance()).CreateAccount()
}
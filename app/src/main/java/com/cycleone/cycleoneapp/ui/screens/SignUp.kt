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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.cycleone.cycleoneapp.ui.components.FormCard
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration.Companion.seconds


fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

class SignUp {

    private suspend fun onSignUp(
        context: Context,
        email: String?,
        password: String?,
        password2: String?,
        name: String?,
        termsCondition: Boolean,
    ) {
        if (name.isNullOrBlank()) {
            throw Error("Empty Name")
        }
        if (email.isNullOrBlank()) {
            throw Error("Empty Mail ID")
        }
        if (!email.isValidEmail()) {
            throw Error("Invalid E-Mail ID")
        }
        if (password.isValidEmail()) {
            throw Error("Empty Password")
        }
        if (!termsCondition) {
            throw Error("Check the terms and conditions")
        }
        if (password == null || password.length < 8) {
            throw Error("Password should be atleast 8 characters")
        }
        if (password != password2) {
            throw Error("Passwords do not match")
        }
        val authResult = password.let {
            try {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.filterNot { it.isWhitespace() }, it)
                    .await()
            } catch (e: Throwable) {
                throw Error(e.message)
            }
        }

        Toast.makeText(
            context,
            "Account Created",
            Toast.LENGTH_LONG
        ).show()


        try {
            authResult?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name)
                    .build()
            )?.await()
        } catch (e: Throwable) {
            throw Error(e.message)
        }

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
          val user = FirebaseAuth.getInstance().currentUser
          val uid = user?.uid


        try {
                if (uid != null) {

                    val db = Firebase.firestore
                    val userDocRef = db.collection("users").document(uid)

                    userDocRef.get().addOnSuccessListener { snapshot ->
                        if (!snapshot.exists() || snapshot.get("Coins") == null) {
                            userDocRef.update("Coins", 100)
                                .addOnSuccessListener {
                                    Log.d("firestore", "Coins field initialized to 100")
                                }
                                .addOnFailureListener {
                                    // If update fails (e.g. field doesn't exist), use set() with merge
                                    userDocRef.set(mapOf("Coins" to 100), SetOptions.merge())
                                    Log.d("firestore", "Coins field added via set()")
                                }
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.e("firestore", "Error initializing Coins field", e)
            }
        }




    @Composable
    fun Create(modifier: Modifier = Modifier, navController: NavController) {
        UI(modifier, navController)
    }

    @Composable
    @Preview
    fun UI(modifier: Modifier = Modifier, navController: NavController = rememberNavController()) {
        var termsCondition by remember {
            mutableStateOf(false)
        }
        var accountCreated by remember {
            mutableStateOf(false)
        }

        val context = LocalContext.current
        val scrollState = rememberScrollState()

        AnimatedVisibility(accountCreated) {
            LaunchedEffect(Unit) {
                while (true) {
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
                FormCard().Create(
                    fields = listOf(
                        FormCard.FormCardField.TextField("Full Name", "name", Icons.Default.Person,
                            isSingleline = false),
                        FormCard.FormCardField.TextField("Email", "email", Icons.Default.Email,
                            isSingleline = false),
                        FormCard.FormCardField.CreatePasswordField("Password", "password"),
                    ),
                    navController = navController
                ) { data ->
                    onSignUp(
                        context,
                        email = data["email"],
                        password = data["password"],
                        password2 = data["password2"],
                        name = data["name"],
                        termsCondition,
                    )
                    accountCreated = true
                }
                Column {
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
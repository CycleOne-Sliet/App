package com.cycleone.cycleoneapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

const val PICK_PDF_FILE = 2

class Profile {
    @Composable
    fun Create(modifier: Modifier = Modifier, navController : NavController) {
        val context = LocalContext.current
        val user = FirebaseAuth.getInstance().currentUser
        var userHasCycle: Boolean? by remember {
            mutableStateOf(
                null
            )
        }
        var userCycleId: Long? by remember {
            mutableStateOf(null)
        }

        UI(modifier, user, userHasCycle, userCycleId, navController = navController, loadUserData = {
            val userData =
                Firebase.firestore.collection("users").document(user?.uid!!).get().await()
            userHasCycle = userData.data?.get("HasCycle") as Boolean?
            userCycleId = userData.data?.get("CycleOccupied") as Long?
        }, onVerificationRequested = {
            user?.reload()
            user?.sendEmailVerification()
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
        })
    }

    @Composable
    @Preview
    fun UI(
        modifier: Modifier = Modifier,
        user: FirebaseUser? = null,
        userHasCycle: Boolean? = null,
        userCycleId: Long? = null,
        loadUserData: suspend () -> Unit = {},
        onVerificationRequested: () -> Unit = {},
        navController: NavController = rememberNavController()
    ) {
        var loadedUserData by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(loadUserData) {
            if (!loadedUserData) {
                loadUserData()
                loadedUserData = true
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Log.d("Profile", user?.photoUrl.toString())
            Box(
                modifier = Modifier
                    .width(256.dp)
                    .height(256.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(user?.photoUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "Profile Photo",
                    fallback = rememberVectorPainter(Icons.Default.Person),
                    placeholder = rememberVectorPainter(Icons.Default.Person),
                    modifier = Modifier
                        .fillMaxWidth(0.3F),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90F)
                    .background(
                        MaterialTheme.colorScheme.inversePrimary,
                    )
                    .padding(top = 50.dp, start = 10.dp, end = 10.dp),
            )
            {
                user?.displayName?.let {
                    Text(it)
                }
                Text("Email: ")
                user?.email?.let {
                    Text(it)
                }
                if (user?.isEmailVerified != true) {
                    Button(
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                        onClick = onVerificationRequested
                    ) {
                        Text("Verify Email")
                    }
                }
                if (userHasCycle == true) {
                    Text("You currently have a cycle with id: ${userCycleId}")
                } else {
                    Text("You currently have no cycle allocated")
                }
                FancyButton(onClick = {
                    navController.navigate("/edit_profile")
                }, text = "Edit Profile")
                FancyButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    NavProvider.addLogEntry("Signed Out")
                }, text = "Logout")
            }
        }
    }
}

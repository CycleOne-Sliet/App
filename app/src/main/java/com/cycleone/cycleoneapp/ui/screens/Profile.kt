package com.cycleone.cycleoneapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

const val PICK_PDF_FILE = 2

class Profile {
    @Composable
    fun Create(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val user = FirebaseAuth.getInstance().currentUser
        val pickFileLauncher =
            user?.uid?.let {
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { FileUri ->
                    val profilePhotoRef =
                        FirebaseStorage.getInstance().reference.child("userImages").child(it)
                    Log.d("Profile", "Profile Name: ${profilePhotoRef.name}")
                    FileUri?.let { profilePhotoRef.putFile(it) }
                    profilePhotoRef.downloadUrl.addOnSuccessListener {
                        user.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setPhotoUri(it).build()
                        )
                    }
                }
            }
        UI(modifier, user, onPhotoChangeRequest = {
            pickFileLauncher?.launch("*/*")

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
        onPhotoChangeRequest: () -> Unit = {},
        onVerificationRequested: () -> Unit = {}
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween

        ) {


            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(user?.photoUrl)
                    .crossfade(true).build(),
                contentDescription = "Profile Photo",
                fallback = rememberVectorPainter(Icons.Default.Person),
                placeholder = rememberVectorPainter(Icons.Default.Person),
                modifier = Modifier
                    .fillMaxWidth(0.3F).heightIn(0.dp, 150.dp)
                    .clickable {
                        onPhotoChangeRequest()
                    }.clip(RoundedCornerShape(20.dp)),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth

            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90F)
                    .background(MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
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
            }
        }
    }
}
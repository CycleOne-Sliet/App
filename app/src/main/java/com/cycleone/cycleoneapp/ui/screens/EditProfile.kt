package com.cycleone.cycleoneapp.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.cycleone.cycleoneapp.ui.components.FormCard
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class EditProfile {
    @Composable
    fun Create(modifier: Modifier, navController: NavController) {
        val user = FirebaseAuth.getInstance().currentUser
        val userProfilePicker =
            user?.uid?.let {
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { fileUri ->
                    val profilePhotoRef =
                        FirebaseStorage.getInstance().reference.child("userImages").child(it)
                    Log.d("Profile", "Profile Name: ${profilePhotoRef.name}")
                    fileUri?.let { profilePhotoRef.putFile(it) }
                    profilePhotoRef.downloadUrl.addOnSuccessListener {
                        user.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setPhotoUri(it).build()
                        )
                    }
                }
            }
        UI(modifier, user, userProfilePicker, navController) { password ->
            user?.let { user -> onDeleteUser(navController = navController, user, password) }
        }
    }

    private suspend fun onDeleteUser(
        navController: NavController,
        user: FirebaseUser,
        password: String
    ) {
        user.email?.let { email ->
            try {
                user.reauthenticate(
                    EmailAuthProvider.getCredential(
                        email,
                        password
                    )
                )
            } catch (e: FirebaseAuthException) {
                throw Error(e.message)
            }
        }
        try {
            user.delete().await()
        } catch (e: Throwable) {
            throw Error(e.message)
        }
        navController.navigate("/sign_in")
    }

    private suspend fun onEditProfile(user: FirebaseUser, profile: Map<String, String>) {
        val name = profile["name"]
        var profileChangeRequest = UserProfileChangeRequest.Builder()
        name?.let {
            profileChangeRequest = profileChangeRequest.setDisplayName(it)
        }
        try {
            user.updateProfile(profileChangeRequest.build()).await()
        } catch (e: FirebaseAuthException) {
            throw Error(e.message)
        }
        val email = profile["email"]
        email?.let {
            try {
                FirebaseAuth.getInstance().currentUser?.verifyBeforeUpdateEmail(email)
            } catch (e: FirebaseAuthException) {
                throw Error(e.message)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UI(
        modifier: Modifier,
        user: FirebaseUser?,
        userProfilePhotoPicker: ManagedActivityResultLauncher<String, Uri?>?,
        navController: NavController,
        deleteUser: suspend (String) -> Unit
    ) {
        var openDialog by remember { mutableStateOf(false) }
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit Profile")
            Box(
                modifier = Modifier
                    .width(256.dp)
                    .height(256.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(user?.photoUrl)
                        .build(),
                    contentDescription = "Profile Photo",
                    fallback = rememberVectorPainter(Icons.Default.Person),
                    placeholder = rememberVectorPainter(Icons.Default.Person),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            userProfilePhotoPicker?.launch("*/*")
                        },
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth
                )
            }
            Column {
                FormCard().Create(
                    fields = listOf(
                        FormCard.FormCardField.TextField(
                            label = "Name",
                            key = "name",
                            icon = Icons.Default.Person
                        ),
                        FormCard.FormCardField.TextField(
                            label = "Email",
                            key = "email",
                            icon = Icons.Default.Email
                        ),
                        FormCard.FormCardField.TextField(
                            label = "Phone Number",
                            key = "phone",
                            icon = Icons.Default.Phone
                        ),
                    ),
                    actionName = "Save Changes",
                    navController = navController,
                    onSubmit = { data -> user?.let { user -> onEditProfile(user, data) } },
                )
            }
            if (openDialog) {
                BasicAlertDialog(onDismissRequest = {
                    openDialog = false
                }) {
                    FormCard().Create(
                        fields = listOf(
                            FormCard.FormCardField.PasswordField(
                                label = "Password for verification",
                                key = "password"
                            )
                        ),
                        navController = navController
                    ) { data ->
                        data["password"]?.let { deleteUser(it) }
                    }
                }
            }
            FancyButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    openDialog = true
                }, text = "Delete Account"
            )
        }
    }
}

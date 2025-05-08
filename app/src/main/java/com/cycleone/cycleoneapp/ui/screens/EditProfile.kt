package com.cycleone.cycleoneapp.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.cycleone.cycleoneapp.ui.components.FormCard
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            } catch (e: Throwable) {
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

    private suspend fun onEditProfile(user: FirebaseUser, profile: Map<String, String>
                                      ) {
        val name = profile["name"]
        val phone = profile["phone"]
        val branch = profile["branch"]
        val year = profile["year"]

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
        // 3. Update custom fields in Firestore
        val db = FirebaseFirestore.getInstance()
        val userData = mutableMapOf<String, Any>()

        phone?.let { userData["phone"] = it }
        branch?.let { userData["branch"] = it }
        year?.let { userData["year"] = it }

        if (userData.isNotEmpty()) {
            try {
                db.collection("users").document(user.uid)
                    .set(userData, SetOptions.merge())
                    .await()
                Log.d("EditProfile", "Custom fields saved to Firestore: $userData")
            } catch (e: Throwable) {
                Log.e("EditProfile", "Failed to update custom fields: ${e.message}")
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
            modifier = modifier.fillMaxSize().padding(start = 8.dp,end=8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit Profile",
                fontSize = 20.sp,
                color = Color(0xffff6b35),

                )
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(215.dp)
                    .border(3.dp, Color(0xffff6b35), CircleShape)
            ){
                Box(
                    modifier = Modifier
                        .width(215.dp)
                        .height(215.dp),
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

            }

            Column {
                FormCard().Create(
                    fields = listOf(
                        FormCard.FormCardField.TextField(
                            label = "Name",
                            key = "name",
                            icon = Icons.Default.Person,
                            isSingleline = true
                        ),
                        FormCard.FormCardField.TextField(
                            label = "Email",
                            key = "email",
                            icon = Icons.Default.Email,
                            isSingleline = false
                        ),
                        FormCard.FormCardField.TextField(
                            label = "Phone Number",
                            key = "phone",
                            icon = Icons.Default.Phone,
                            isSingleline = true
                        ),
                        FormCard.FormCardField.TextField(
                            label = "Branch",
                            key = "branch",
                            icon =Icons.Default.Abc,
                            isSingleline = true
                        ),
                        FormCard.FormCardField.TextField(
                            label = "Course Year",
                            key = "year",
                            icon = Icons.Default.Numbers,
                            isSingleline = true
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

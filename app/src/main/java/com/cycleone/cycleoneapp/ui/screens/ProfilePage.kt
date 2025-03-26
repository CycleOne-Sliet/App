package com.cycleone.cycleoneapp.ui.screens

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.NavProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePage {
    @Composable
    fun Create(modifier: Modifier = Modifier) {
        ProfileScreen( NavProvider.controller, context = LocalContext.current)
    }
    fun String.isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
    @Composable
    fun ProfileScreen(navController: NavController = rememberNavController(),
                     context:Context,
                  //    modifier: Modifier = Modifier,
                  //    user: FirebaseUser? = null,
                 //     loadUserData: suspend () -> Unit = {},
                  //    onPhotoChangeRequest: () -> Unit = {}

                     // onPhotoChangeRequest: () -> Unit = {}
        ) {
        val name = remember { mutableStateOf(TextFieldValue("")) }
        var email = remember { mutableStateOf(TextFieldValue("")) }
        var phone = remember { mutableStateOf(TextFieldValue("")) }
        var branch = remember { mutableStateOf(TextFieldValue("")) }
        var year = remember { mutableStateOf(TextFieldValue("")) }
        val context = LocalContext.current
        val imageUri = remember { mutableStateOf<Uri?>(null) }
       // val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        var emailError = remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(0.dp)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(55.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xffff6b35)
                    )
                }
                Text(
                    text = "Edit Profile",
                    modifier = Modifier.padding(bottom = 0.dp),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xffff6b35)
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            ProfileImage()
            Spacer(modifier = Modifier.height(55.dp))
            CustomTextField(
                label = "Name",
                value = name.value,
                onValueChange = { newValue -> name.value = newValue })
            Spacer(modifier = Modifier.height(25.dp))
            CustomTextField(
                label = "Email",
                value = email.value,
                onValueChange = {
                        newValue -> email.value = newValue

                   }
       )
            Spacer(modifier = Modifier.height(25.dp))
            CustomTextField(
                label = "Phone No",
                value = phone.value,
                onValueChange = { newValue -> phone.value = newValue })
            Spacer(modifier = Modifier.height(25.dp))
            CustomTextField(
                label = "Branch",
                value = branch.value,
                onValueChange = { newValue -> branch.value = newValue })
            Spacer(modifier = Modifier.height(25.dp))
            CustomTextField(
                label = "Year",
                value = year.value,
                onValueChange = { newValue -> year.value = newValue })
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {

onClickButton(context ,

    email = email.value.text,
    name = name.value.text,
    phone = phone.value.text,
    branch = branch.value.text,
    year = year.value.text


    )
                    Log.d("Tag","Text is ${email}")
                    Log.d("Tag","Text is ${name}")
                    Log.d("Tag","Text is ${phone}")
                    Log.d("Tag","Text is ${branch}")
                    Log.d("Tag","Text is ${year}")

                },
                modifier = Modifier
                    .size(width = 250.dp, height = 50.dp)
                    .border(2.dp, Color(0xffff6b35)),
                colors = ButtonColors(
                    contentColor = Color(0xffff6b35),
                    containerColor = Color.Black,
                    disabledContentColor = Color(0xffff6b35),
                    disabledContainerColor = Color.Black
                )

            ) {
                Text(
                    text = "Update Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xffff6b35)
                )
            }

        }
    }

    @Composable
    fun ProfileImage() {
        val imageUri = rememberSaveable { mutableStateOf("") }
        val painter = rememberAsyncImagePainter(
            if (imageUri.value.isEmpty())
                R.drawable.forgot_password
            else
                imageUri.value
        )
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri.let { imageUri.value = it.toString() }
            }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(150.dp)
                    .border(3.dp, Color(0xffff6b35), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(shape = CircleShape)
                ) {
                    Image(
                        painter = painter, contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize().fillMaxSize()
                            .clickable {
                                launcher.launch("image/*")
                            },
                        contentScale = ContentScale.Crop
                    )
                }

            }
        }
    }

    @Composable
    fun CustomTextField(
        label: String,
        value: TextFieldValue,

        onValueChange: (TextFieldValue) -> Unit
    ) {

        TextField(
            value = value, onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
            },
            modifier = Modifier.border(2.dp, Color(0xffff6b35)),
            colors = TextFieldDefaults.colors(),
            singleLine = true

        )

    }
    fun onClickButton(
        context: Context,
        email: String,
        name: String,
        phone: String,
        branch: String,
        year: String
    ) {
        if (!email.isValidEmail()) {
            Toast.makeText(context, "Invalid E-Mail ID", Toast.LENGTH_LONG).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email,phone)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {

                                val userData = hashMapOf(
                                    "uid" to user.uid,
                                    "email" to email,
                                    "name" to name,
                                    "phone" to phone,
                                    "branch" to branch,
                                    "year" to year
                                )

                                db.collection("users").document(user.uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context.applicationContext,
                                            "Registration successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context.applicationContext,
                                            "Error saving user data: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    context.applicationContext,
                                    "Profile update failed: ${profileTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context.applicationContext,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}

/*@Preview(showBackground = true)
@Composable
fun DefaultPreview(){

        ProfileScreen(modifier = Modifier.padding())
    }*/


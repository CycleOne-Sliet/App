package com.cycleone.cycleoneapp.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.cycleone.cycleoneapp.services.Feedback

import com.cycleone.cycleoneapp.services.mapMessageToFirestoreMap
import com.cycleone.cycleoneapp.uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FeedbackScreen(modifier: Modifier = Modifier, navController: NavController) {
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Pair<String, Uri?>>() }
    var showAttachmentOptions by remember { mutableStateOf(false) }
    val database = FirebaseDatabase.getInstance()
    val feedbackRef = database.getReference("feedbacks")


    val context = LocalContext.current
    val phoneNumber = "tel:+91 6205538058" // Replace with actual mobile number
    val mainColor = Color(0xffff6b35)

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { messages.add("" to uri) }
    }

    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { messages.add("" to uri) }
    }

    Scaffold {
        Box(modifier = Modifier.fillMaxSize()) {

            // Background Canvas
          /*  Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Random curved path
                val curvePath = Path().apply {
                    moveTo(0f, height * 0.2f)
                    cubicTo(
                        width * 0.25f, height * 0.1f,
                        width * 0.75f, height * 0.3f,
                        width, height * 0.2f
                    )
                }
                drawPath(path = curvePath, color = mainColor, style = Stroke(width = 6f))

                // Circle shape
                drawCircle(color = mainColor, center = Offset(width * 0.1f, height * 0.8f), radius = 60f, style = Stroke(width = 6f))

                // Rectangle with rounded corners
                drawRoundRect(
                    color = mainColor,
                    topLeft = Offset(width * 0.7f, height * 0.1f),
                    size = Size(150f, 100f),
                    cornerRadius = CornerRadius(30f, 30f),
                    style = Stroke(width = 6f)
                )
            }*/

            Column(modifier = Modifier.fillMaxSize()) {

                // Top Box with "Feedback Page" and Call Icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(mainColor, shape = RoundedCornerShape(bottomEnd = 60.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Feedback Page", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse(phoneNumber)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(mainColor, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = Color.Green)
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        reverseLayout = true
                    ) {
                        items(messages.size) { index ->
                            val (text, uri) = messages[messages.size - 1 - index]
                            if (uri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = text,
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .background(mainColor, shape = RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        AnimatedVisibility(
                            visible = showAttachmentOptions,
                            enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                AttachmentFAB(
                                    icon = Icons.Default.Image,
                                    label = "Image",
                                    onClick = {
                                        imagePicker.launch("image/*")
                                        showAttachmentOptions = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                AttachmentFAB(
                                    icon = Icons.Default.Videocam,
                                    label = "Video",
                                    onClick = {
                                        videoPicker.launch("video/*")
                                        showAttachmentOptions = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                AttachmentFAB(
                                    icon = Icons.Default.CameraAlt,
                                    label = "Camera",
                                    onClick = {
                                        // Add camera logic
                                        showAttachmentOptions = false
                                    }
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(mainColor)
                                    .padding(horizontal = 8.dp, vertical = 12.dp)
                            ) {
                                if (message.isEmpty()) {
                                    Text("Write your feedback", color = Color.LightGray)
                                }
                                BasicTextField(
                                    value = message,
                                    onValueChange = { message = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                                )
                            }

                            IconButton(
                                onClick = { showAttachmentOptions = !showAttachmentOptions },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(mainColor, CircleShape)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = Color.White)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {

                                      /*  if (message.isNotBlank()) {
                                            // Add to local list
                                            messages.add(message to null)

                                            // Push to Firebase
                                            val feedback = Feedback(message = message)
                                            feedbackRef.push().setValue(feedback)

                                            // Clear the input
                                            message = ""
                                        }*/

                                    val db = FirebaseFirestore.getInstance()
                                    val feedbackMap = mapMessageToFirestoreMap(message = message, uri = uri?.toString())

                                    db.collection("feedbacks")
                                        .add(feedbackMap)
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Feedback added successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Error adding feedback", e)
                                        }


                                },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(mainColor, CircleShape)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentFAB(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = Color(0xffff6b35),
            shape = CircleShape,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White)
        }
        Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

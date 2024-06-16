package com.cycleone.cycleoneapp.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cycleone.cycleoneapp.services.NavProvider
import com.cycleone.cycleoneapp.services.StandLocation
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationCard {
    @Composable
    fun Create(
        standInfo: StandLocation = StandLocation(
            photoUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_light_color_272x92dp.png",
            location = "Test",
        ), navigator: NavController = NavProvider.controller, href: String = "/home"
    ) {

        OutlinedButton(
            onClick = { navigator.navigate(href) },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.background(Color(100, 100, 100, 40))
        ) {
            var thumbnail: Bitmap? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(standInfo) {
                launch {
                    Log.d("Started Download", "Downloading started")
                    val imageBytes =
                        FirebaseStorage.getInstance()
                            .getReferenceFromUrl(standInfo.photoUrl)
                            .getBytes(2097152).await()
                    Log.d("Download", "Download Complete")
                    thumbnail = BitmapFactory.decodeByteArray(
                        imageBytes, 0, imageBytes.size
                    )
                }
            }
            Column(
                modifier =
                if (thumbnail == null) {
                    Modifier
                } else {
                    Modifier.paint(
                        BitmapPainter(
                            thumbnail!!.asImageBitmap(),
                            IntOffset.Zero,
                            IntSize(thumbnail!!.width, thumbnail!!.height)
                        )
                    )
                }
                    .size(160.dp, 120.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .padding(top = 10.dp),

                ) {
                if (thumbnail == null) {
                    Icon(Icons.Default.Refresh, "Loading")
                }
                Text(
                    standInfo.location,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }

    @Composable
    @Preview
    fun Preview() {
        Create()
    }
}
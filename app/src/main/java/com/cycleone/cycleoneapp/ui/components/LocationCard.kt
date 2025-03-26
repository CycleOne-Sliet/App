package com.cycleone.cycleoneapp.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
        ), navigator: NavController, href: String = "/home"
    ) {

        var thumbnail: Bitmap? by rememberSaveable(key = standInfo.photoUrl) {
            mutableStateOf(null)
        }
        LaunchedEffect(standInfo.photoUrl) {
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
            Modifier
                //.size(160.dp, 120.dp)
                .border(
                    3.dp, Color.LightGray,
                    RoundedCornerShape(20.dp)
                )
                .clickable(true, onClick = { navigator.navigate(href) }),

            ) {
            if (thumbnail == null) {
                Icon(Icons.Default.Refresh, "Loading")
            } else {
                Image(
                    modifier = Modifier
                        .padding(
                            10.dp
                        )
                        .border(
                            3.dp, Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .height(180.dp)
                        .width(180.dp),
                    bitmap = thumbnail!!.asImageBitmap(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "some useful description",
                )
            }
            Text(
                standInfo.location,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(20.dp)
            )
        }
    }

}
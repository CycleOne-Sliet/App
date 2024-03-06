package com.cycleone.cycleoneapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R

class LocationCard {
    @Composable
    fun Create(imagePainter: Painter = painterResource(id = R.drawable.forgot_password_image), label: String = "Fictional Department", navigator: NavController = rememberNavController(), href: String = "") {
        OutlinedButton(onClick = {navigator.navigate(href)}, shape = RoundedCornerShape(15.dp), modifier = Modifier.background(Color(100, 100, 100, 40))) {
            Column(modifier = Modifier) {
                Image(painter = imagePainter, label, modifier = Modifier
                    .size(160.dp, 120.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ).padding(top = 10.dp))
                Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 20.dp))
            }
        }
    }
    @Composable
    @Preview
    fun Preview() {
        Create()
    }
}
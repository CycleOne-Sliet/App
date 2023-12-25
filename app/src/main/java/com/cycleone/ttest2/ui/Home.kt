package com.cycleone.ttest2.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.ttest2.R
import com.google.firebase.auth.FirebaseAuth

class Home(private val controller: NavController, private val authController: FirebaseAuth) {
    @Composable
    fun HomePage(modifier: Modifier = Modifier) {
        Log.d("INFO", "User is ${authController.currentUser}");
        if (authController.currentUser != null) {
            controller.navigate("/dashboard")
        }
        Column(
            modifier = Modifier.paint(painterResource(id = R.drawable.image5), sizeToIntrinsics = true, contentScale = ContentScale.Crop),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Column(modifier = Modifier.background(
                Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    1.0f to Color.Black
                    , startY = 0.0f, endY = 500.0f )
                ).fillMaxWidth().requiredHeight(350.dp), verticalArrangement = Arrangement.SpaceAround) {
            Text(
                "CycleOne",
                style = TextStyle(
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.offset(x = 20.dp),
            )
            Text(
                text = "Connect - Commute - Conserve\nwith us ",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.offset(x = 20.dp),
            )
            Button(
                onClick = { controller.navigate("/sign_up") }, modifier = Modifier
                    .requiredHeight(height = 56.dp)
                    .fillMaxWidth(0.75F)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Get Started",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Text(
                textAlign = TextAlign.Center,
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp
                        )
                    ) { append("Already Have an account?\n ") }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append("Login In Now") }
                },
                modifier = Modifier
                    .requiredWidth(width = 274.dp)
                    .clickable(onClick = {
                        controller.navigate("/sign_in")
                    }).align(Alignment.CenterHorizontally)
            )
        }}
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun HomePagePreview() {
    Home(rememberNavController(), FirebaseAuth.getInstance()).HomePage(Modifier)
}
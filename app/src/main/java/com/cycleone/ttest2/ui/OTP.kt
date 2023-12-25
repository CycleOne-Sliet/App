package com.cycleone.ttest2.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.ttest2.R

class OTP {
    @Composable
    fun OTPVerification(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .requiredWidth(width = 390.dp)
                .requiredHeight(height = 844.dp)
                .clip(shape = RoundedCornerShape(50.dp))
                .background(color = Color(0xfffcfcfc))
        ) {
            Text(
                text = "Forgot password",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 16.sp),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 73.dp,
                        y = 80.dp)
                    .requiredWidth(width = 192.dp))
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 35.dp,
                        y = 75.dp)
                    .requiredSize(size = 30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.left_arrow),
                    contentDescription = "left-arrow_271220 2",
                    modifier = Modifier
                        .requiredSize(size = 30.dp))
            }
            Text(
                text = "OTP code",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 20.sp),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 35.dp,
                        y = 298.dp)
                    .requiredWidth(width = 115.dp))
            Text(
                text = "OTP Verification",
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 56.dp,
                        y = 164.dp)
                    .requiredWidth(width = 277.dp))
            Text(
                text = "We sent 4 digit code to your email.\n    This code will expire in 00:13",
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 12.sp),
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 83.dp,
                        y = 213.dp)
                    .requiredWidth(width = 211.dp))
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 40.dp,
                        y = 356.dp)
                    .requiredWidth(width = 310.dp)
                    .requiredHeight(height = 45.dp)
            ) {
                Box(
                    modifier = Modifier
                        .requiredSize(size = 45.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .background(color = Color.White)
                        .border(border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(5.dp)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 89.dp,
                            y = 0.dp)
                        .requiredSize(size = 45.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .background(color = Color.White)
                        .border(border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(5.dp)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 177.dp,
                            y = 0.dp)
                        .requiredSize(size = 45.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .background(color = Color.White)
                        .border(border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(5.dp)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 265.dp,
                            y = 0.dp)
                        .requiredSize(size = 45.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .background(color = Color.White)
                        .border(border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(5.dp)))
                Text(
                    text = "5",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 16.dp,
                            y = 6.dp))
                Text(
                    text = "2",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 105.dp,
                            y = 6.dp))
                Text(
                    text = "6",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 193.dp,
                            y = 6.dp))
            }
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .offset(x = 35.dp,
                        y = 459.dp)
                    .requiredWidth(width = 334.dp)
                    .requiredHeight(height = 342.dp)
            ) {
                Box(
                    modifier = Modifier
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 0.dp,
                            y = 70.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 0.dp,
                            y = 140.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 0.dp,
                            y = 210.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 115.dp,
                            y = 210.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 230.dp,
                            y = 210.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 115.dp,
                            y = 140.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 230.dp,
                            y = 140.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 115.dp,
                            y = 70.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 230.dp,
                            y = 70.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 115.dp,
                            y = 0.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 230.dp,
                            y = 0.dp)
                        .requiredWidth(width = 90.dp)
                        .requiredHeight(height = 45.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color(0xffc4c4c4).copy(alpha = 0.27f)))
                Text(
                    text = "1",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 38.dp,
                            y = 6.dp))
                Text(
                    text = "2",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 153.dp,
                            y = 6.dp))
                Text(
                    text = "3",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 268.dp,
                            y = 6.dp))
                Text(
                    text = "6",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 268.dp,
                            y = 76.dp))
                Text(
                    text = "5",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 153.dp,
                            y = 76.dp))
                Text(
                    text = "4",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 38.dp,
                            y = 76.dp))
                Text(
                    text = "7",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 38.dp,
                            y = 146.dp))
                Text(
                    text = "8",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 153.dp,
                            y = 146.dp))
                Text(
                    text = "9",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 268.dp,
                            y = 146.dp))
                Text(
                    text = "#",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 268.dp,
                            y = 216.dp))
                Text(
                    text = "0",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 153.dp,
                            y = 216.dp))
                Text(
                    text = "*",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 20.sp),
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 38.dp,
                            y = 216.dp))
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 34.dp,
                            y = 292.dp)
                        .requiredWidth(width = 300.dp)
                        .requiredHeight(height = 50.dp)
                        .clip(shape = RoundedCornerShape(40.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(x = 127.dp,
                                y = 0.dp)
                            .requiredWidth(width = 159.dp)
                            .requiredHeight(height = 50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .requiredWidth(width = 159.dp)
                                .requiredHeight(height = 50.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(shape = RoundedCornerShape(25.dp))
                                    .background(color = Color(0xff6c63ff)))
                            Text(
                                text = "Verify",
                                color = Color(0xfffcfcfc),
                                style = TextStyle(
                                    fontSize = 20.sp),
                                modifier = Modifier
                                    .fillMaxSize())
                            Image(
                                painter = painterResource(id = R.drawable.vector),
                                contentDescription = "Vector",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(border = BorderStroke(2.dp, Color.White)))
                        }
                    }
                }
            }
        }
    }
    @Preview(widthDp = 390, heightDp = 844)
    @Composable
    private fun OTPVerificationPreview() {
        OTPVerification(Modifier)
    }
}
package com.cycleone.cycleoneapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

class savedProfile {
    //@Preview(showBackground = true)
    @Composable
    fun SavedScreen(
        navController: NavController, name: String, email: String,
        phone: String, branch: String, year: String/*imageUri: Uri*/
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black).padding(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(350.dp),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFC24F19)
                )
            )
            {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .size(150.dp)
                            .border(3.dp, Color.White, CircleShape)
                    ) {
                        /*  Box(modifier = Modifier
                          .size(150.dp)
                          .clip(shape = CircleShape)) {
                          Image(painter=rememberAsyncImagePainter(imageUri), contentDescription = null,
                              modifier = Modifier
                                  .wrapContentSize()
                              ,
                              contentScale = ContentScale.Crop)
                      }*/

                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = email,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W200,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )

            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        CustomText(label = "Phone No", textValue = phone)
        Spacer(modifier = Modifier.height(15.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.height(15.dp))
        CustomText(label = "Branch", textValue = branch)
        Spacer(modifier = Modifier.height(15.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.height(15.dp))
        CustomText(label = "Year", textValue = year)
        Spacer(modifier = Modifier.height(15.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.height(80.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    navController.navigate(route = "_profile")
                },
                modifier = Modifier.size(width = 250.dp, height = 50.dp)
                    .border(2.dp, Color(0xffff6b35)),
                colors = ButtonColors(
                    contentColor = Color(0xffff6b35),
                    containerColor = Color.Black,
                    disabledContentColor = Color(0xffff6b35),
                    disabledContainerColor = Color.Black
                )

            ) {
                Text(
                    text = "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xffff6b35)
                )
            }
        }


    }


    @Composable

    fun CustomText(
        label: String,
        textValue: String
    ) {
        Column(modifier = Modifier.padding(start = 60.dp)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.W200,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = textValue,
                fontSize = 25.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xffff6b35),
                fontFamily = FontFamily.Serif
            )
        }


    }
}
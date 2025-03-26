package com.cycleone.cycleoneapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cycleone.cycleoneapp.R
import com.cycleone.cycleoneapp.services.OnboardingContent
import com.cycleone.cycleoneapp.ui.components.FancyButton
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily

class Onboarding {
    @Composable
    fun Create(modifier: Modifier, navController: NavController) {
        UI(modifier, navController = navController)
    }

    @Preview
    @Composable
    fun UI(
        modifier: Modifier = Modifier,
        content: List<OnboardingContent> = OnboardingContent.default_content(),
        navController: NavController = rememberNavController()
    ) {
        var indexShown by remember {
            mutableStateOf(0)
        }
        Box(
            modifier = modifier
                .background(Color.Black)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            // TODO: Include the proper logo in onboard
            Image(
                painter = painterResource(R.drawable.onboard_1),
                "Background",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                content.mapIndexed { index, c ->
                    AnimatedVisibility(indexShown == index) {
                        Image(
                            painter = painterResource(c.image),
                            "Onboarding $index",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(3.dp, Color(0x40423F3F)),
                            RoundedCornerShape(
                                topStart = 50.dp,
                                topEnd = 50.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .padding(top = 3.dp)
                        .background(
                            Color(0xff252322),
                            RoundedCornerShape(
                                topStart = 50.dp,
                                topEnd = 50.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content.mapIndexed { index, c ->
                        AnimatedVisibility(indexShown == index) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    c.title,
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = monsterratFamily
                                )
                                Text(
                                    c.content,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontFamily = monsterratFamily
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in content.indices) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i > indexShown) Color(0xffD9D9D9) else Color(
                                            0xffff6b35
                                        )
                                    )
                            )
                        }
                    }
                    FancyButton(
                        modifier = Modifier.fillMaxWidth(0.6F),
                        onClick = {
                            if (indexShown >= content.size - 1)
                                navController.navigate("/sign_up")
                            else indexShown += 1
                        }, text = if (indexShown >= content.size - 1)
                            "GET STARTED"
                        else
                            "NEXT"

                    )
                }
            }

        }
    }

}
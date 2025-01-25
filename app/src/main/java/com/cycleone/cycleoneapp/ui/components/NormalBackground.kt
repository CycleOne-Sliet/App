package com.cycleone.cycleoneapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.cycleone.cycleoneapp.R

@Composable
@Preview
fun NormalBackground(
    modifier: Modifier = Modifier,
    backgroundImage: Int = R.drawable.normal_background,
    content: @Composable() (BoxScope.() -> Unit) = {}
) {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(backgroundImage),
            "Background",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        content()
    }

}
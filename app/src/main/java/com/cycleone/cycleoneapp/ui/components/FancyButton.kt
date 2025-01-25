package com.cycleone.cycleoneapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily

@Composable
@Preview
fun FancyButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}, text: String = "NEXT", enabled : Boolean = true) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonColors(
            containerColor = Color.Black,
            contentColor = Color(0xffff6b35),
            disabledContentColor = Color.Red,
            disabledContainerColor = Color.Gray
        ),
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, if (enabled) Color(0xffff6b35) else Color.Red)
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, fontFamily = monsterratFamily)
    }
}
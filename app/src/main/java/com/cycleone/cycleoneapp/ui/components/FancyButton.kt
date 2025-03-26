package com.cycleone.cycleoneapp.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@Composable
@Preview
fun FancyButton(
    modifier: Modifier = Modifier,
    onClick: suspend () -> Unit = {},
    text: String = "NEXT",
    enabled: Boolean = true
) {
    var errorText: String? by remember {
        mutableStateOf(null)
    }
    var loading by remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
            val job = coroutineScope.launch(CoroutineExceptionHandler { _, throwable ->
                Log.e("FancyBtnExceptionMsg", throwable.message.toString())
                Log.e("FancyBtnExceptionCause", throwable.cause.toString())
                Log.e("FancyBtnExceptionTrace", throwable.stackTraceToString())
                errorText = throwable.message
            }) {
                loading = true
                try {
                    onClick()
                } catch (throwable: Error) {
                    errorText = throwable.message
                    Log.e("FancyBtnExceptionMsgCatch", throwable.message.toString())
                    Log.e("FancyBtnExceptionCauseCatch", throwable.cause.toString())
                    Log.e("FancyBtnExceptionTraceCatch", throwable.stackTraceToString())
                }
                loading = false
            }
        },
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
        if (loading) {
            CircularProgressIndicator()
        } else {
            Text(
                text,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = monsterratFamily
            )
        }
    }
    errorText?.let { error ->
        Text(error, color = Color.Red)
    }
}
package com.cycleone.cycleoneapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.ui.theme.monsterratFamily

class PrestyledText {
    @Composable
    fun Regular(
        modifier: Modifier = Modifier,
        placeholder: String = "",
        onChange: (String) -> Unit = {},
        enabled: Boolean = true,
        isPassword: Boolean = false,
        icon: ImageVector? = null,
    ) {
        var c by remember { mutableStateOf("") }
        TextField(
            modifier = modifier,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0x33c4c4c4),
                focusedContainerColor = Color(0x53c4c4c4),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = MaterialTheme.shapes.medium,
            value = c,
            onValueChange = { x: String -> c = x; onChange(x) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            placeholder = {
                Text(
                    placeholder,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = monsterratFamily,
                    color = Color(0xffdadada)
                )
            },
            enabled = enabled,
            textStyle = TextStyle.Default.merge(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                fontFamily = monsterratFamily,
                color = Color.White
            ),
            trailingIcon = {
                icon?.let {
                    Icon(it, "Icon", tint = Color.White)
                }
            })
    }

    @Composable
    fun OTP(onFill: (Int) -> Unit = {}, enabled: Boolean = true, digits: Int = 4) {
        var otpCode by remember {
            mutableStateOf("")
        }
        BasicTextField(
            value = otpCode,
            onValueChange = { newValue ->
                otpCode = newValue
                if (otpCode.length == digits) {
                    onFill(otpCode.toInt())
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            )

        ) {}
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(digits) { index ->
                val number = when {
                    index >= otpCode.length -> ""
                    else -> otpCode[index].toString()
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = number,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(Color.Black)
                    )
                }
            }
        }
    }

    @Composable
    @Preview
    fun prev() {
        Column {
            Regular(modifier  = Modifier, placeholder = "Preview", onChange = {}, enabled = true,  icon = Icons.Default.Build)
            Regular(modifier  = Modifier, placeholder = "Password", onChange = {}, enabled = true,  icon = Icons.Default.Build, isPassword = true)
            OTP()
        }
    }

}
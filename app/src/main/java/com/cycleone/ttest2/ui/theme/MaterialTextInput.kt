@file:OptIn(ExperimentalMaterial3Api::class)

package com.cycleone.ttest2.ui.theme

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

class MaterialTextInput {
    @Composable
    fun RegularTextInput(label: String, icon: ImageVector, onInputChange: (pass: String) -> Unit) {
        var input by rememberSaveable { mutableStateOf("") }
        TTest2Theme {
            TextField(
                value = input,
                onValueChange = { onInputChange(it); input = it },
                label = { Text(label) },
                singleLine = true,
                placeholder = { Text(label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

                )
        }
    }
    @Composable
    fun PasswordInput(onPasswordChange: (pass: String) -> Unit) {
        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        TTest2Theme {
            TextField(
                value = password,
                onValueChange = { onPasswordChange(it); password = it },
                label = { Text("Password") },
                singleLine = true,
                placeholder = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, description)
                    }
                }
            )}
    }
}

@Composable
fun DisplayError(error: String) {
    AlertDialog(
        icon = {
            Icon(Icons.Filled.Error, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Error")
        },
        text = {
            Text(text = error)
        },
        onDismissRequest = {
        },
        confirmButton = {
            TextButton(
                onClick = {
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
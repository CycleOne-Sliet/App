package com.cycleone.cycleoneapp.ui.components

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Date
import java.util.Locale

class FormCard {
    sealed class FormCardField {
        class TextField(val label: String, val key: String, val icon: ImageVector?) :
            FormCardField()

        class PasswordField(val label: String, val key: String) : FormCardField()
        class CreatePasswordField(val label: String, val key: String) : FormCardField()
        class DateField(val label: String, val key: String) : FormCardField()
    }

    @Composable
    fun Create(
        fields: List<FormCardField>,
        actionName: String = "Submit",
        navController: NavController,
        onSubmit: suspend (Map<String, String>) -> Unit,
    ) {
        UI(fields, actionName, onSubmit, navController)
    }

    private fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UI(
        fields: List<FormCardField>,
        actionName: String,
        onSubmit: suspend (Map<String, String>) -> Unit,
        navController: NavController = rememberNavController(),
    ) {
        val state = remember {
            mutableStateMapOf<String, String>()
        }
        Column {
            fields.forEach { field ->
                when (field) {
                    is FormCardField.TextField -> {
                        PrestyledText().Regular(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            placeholder = "Enter ${field.label}",
                            onChange = { x -> state[field.key] = x },
                            iconVector = field.icon
                        )
                    }

                    is FormCardField.DateField -> {
                        var showDatePicker by remember { mutableStateOf(false) }
                        val datePickerState = rememberDatePickerState()
                        val selectedDate = datePickerState.selectedDateMillis?.let {
                            convertMillisToDate(it)
                        } ?: ""
                        state[field.key] = selectedDate

                        PrestyledText().Regular(
                            onChange = { x -> state[field.key] = x },
                            placeholder = "Enter ${field.label}",
                            icon = {
                                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select date"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        )
                        if (showDatePicker) {
                            Popup(
                                onDismissRequest = { showDatePicker = false },
                                alignment = Alignment.TopStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(y = 64.dp)
                                        .shadow(elevation = 4.dp)
                                        .padding(16.dp)
                                ) {
                                    DatePicker(
                                        state = datePickerState,
                                        showModeToggle = false
                                    )
                                }
                            }
                        }
                    }

                    is FormCardField.PasswordField -> {
                        PrestyledText().Regular(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            placeholder = "Enter ${field.label}",
                            onChange = { x -> state[field.key] = x },
                            isPassword = true,
                            iconVector = Icons.Outlined.Lock
                        )
                        Text(
                            modifier = Modifier
                                .clickable(true, onClick = {
                                    navController.navigate("/forgot_password")
                                })
                                .padding(bottom = 15.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.End,
                            color = Color.White, fontSize = 9.sp,
                            text = "Forgot Password?",
                        )
                    }

                    is FormCardField.CreatePasswordField -> {
                        PrestyledText().Regular(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            placeholder = "Enter ${field.label}",
                            onChange = { x -> state[field.key] = x },
                            isPassword = true,
                            iconVector = Icons.Outlined.Lock
                        )
                        PrestyledText().Regular(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            placeholder = "Enter ${field.label} Again",
                            onChange = { x -> state[field.key + "2"] = x },
                            isPassword = true,
                            iconVector = Icons.Outlined.Lock
                        )
                    }
                }
            }
            FancyButton(
                onClick = {
                    onSubmit(state)
                },
                text = actionName,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

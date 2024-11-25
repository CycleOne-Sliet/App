package com.cycleone.cycleoneapp.services

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController

// Uitility class to make managing navigation easier
// Allows for getting the NavController without having to pass it in
class NavProvider {
    companion object {
        lateinit var controller: NavController

        var updateDebugModalView: (Boolean) -> Unit = {}
        var updateDebugModalContent: () -> Unit = {}

        @Volatile
        var logs: List<String> = listOf()

        @Composable
        fun debugModal() {
            var shouldShowModal by remember {
                mutableStateOf(false)
            }
            var logsList: List<String> by remember {
                mutableStateOf(listOf())
            }
            updateDebugModalView = {
                shouldShowModal = it
            }
            updateDebugModalContent = {
                logsList = logs
            }
            var scrollState = rememberScrollState()
            if (shouldShowModal) {
                Dialog(onDismissRequest = { hideDebugModal() }, properties = DialogProperties()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scrollable(scrollState, Orientation.Vertical)
                    ) {
                        for (v in logsList) {
                            Text(
                                v,
                                color = MaterialTheme.colorScheme.background,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onBackground,
                                        RoundedCornerShape(10.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        fun showDebugModal() {
            logs = listOf()
            updateDebugModalView(true)
        }

        fun addLogEntry(entry: String) {
            logs += entry
            updateDebugModalContent()
        }

        fun removeLogEntry() {
            logs = logs.drop(1)
            updateDebugModalContent()
        }

        fun hideDebugModal() {
            updateDebugModalView(false)
        }
    }
}
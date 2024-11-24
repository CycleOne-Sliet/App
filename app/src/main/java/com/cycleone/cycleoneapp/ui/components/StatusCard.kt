package com.cycleone.cycleoneapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

class StatusCard(val text: String) {
    @Composable
    fun Create() {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

class StatusCardQueue(var statusCards: List<StatusCard>) {
    @Composable
    fun Render() {
    }
}
package com.cycleone.cycleoneapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth

class Profile {
    @Composable
    @Preview
    fun Create() {
        Column {
            FirebaseAuth.getInstance().currentUser?.photoUrl
        }
    }
}
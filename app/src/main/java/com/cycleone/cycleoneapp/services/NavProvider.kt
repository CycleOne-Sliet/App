package com.cycleone.cycleoneapp.services

import androidx.navigation.NavController

// Uitility class to make managing navigation easier
// Allows for getting the NavController without having to pass it in
class NavProvider {
    companion object {
        lateinit var controller: NavController
    }
}
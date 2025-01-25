package com.cycleone.cycleoneapp.services

import com.cycleone.cycleoneapp.R

data class OnboardingContent(val title: String, val content: String, val image: Int) {
    companion object {
        fun default_content(): List<OnboardingContent> {
            return listOf(
                OnboardingContent(
                    "Welcome to CycleOne ! ",
                    "Your ride to convenience, sustainability, and exploration starts here.",
                    R.drawable.image_19
                ),
                OnboardingContent(
                    "Scan to Unlock",
                    "Your ride to convenience, sustainability, and exploration starts here. ",
                    R.drawable.scanning_qr_code
                ),
                OnboardingContent(
                    "Enjoy Your Ride !",
                    "Your ride to convenience, sustainability, and exploration starts here. ",
                    R.drawable.bicycle_sport_illustration_vector_01_removebg_preview_1
                )
            )
        }
    }
}
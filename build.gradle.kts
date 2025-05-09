// import jdk.tools.jlink.resources.plugins

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"

    id("com.google.gms.google-services") version "4.4.2" apply false


}

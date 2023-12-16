buildscript {
    repositories {
        google()
        mavenCentral()

        maven { url = uri("https://www.jitpack.io" ) }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("com.google.gms:google-services:4.4.0")

    }


}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false

}


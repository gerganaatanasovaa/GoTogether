buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.google.com/")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath ("com.google.gms:google-services:4.4.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
    // build.gradle.kts (Project-level)
}

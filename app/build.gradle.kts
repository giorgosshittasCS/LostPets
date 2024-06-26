plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") // Apply the google maps plugin

}

android {
    namespace = "com.example.lostpets"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.lostpets"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true

    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    // Google Play services for location [This wasn't added by default]
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Google Play services for maps [This wasn't added by default]
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // LiveData and Kotlin Extensions for data observability
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")
    // ViewModel and Kotlin Extensions for managing UI-related data
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Navigation fragment
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    // Navigation UI components
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics:21.3.0")
    implementation("com.google.firebase:firebase-messaging:23.2.1")
    implementation("com.google.firebase:firebase-storage:20.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Call API Package
    implementation("com.google.android.gms:play-services-cronet:18.0.1")
    // Display image package
    implementation("com.squareup.picasso:picasso:2.8")
    //for the toast
    implementation("androidx.core:core-ktx:1.9.0")
    // JUnit for unit testing
    testImplementation("junit:junit:4.13.2")
    // AndroidX JUnit extensions
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // Espresso for UI testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


}
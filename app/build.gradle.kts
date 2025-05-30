plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.linktothe.ps5payloadsender"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.linktothe.ps5payloadsender"
        minSdk = 21
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat) // Assuming libs.appcompat points to a valid version
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // Material Design components (includes TabLayout)
    implementation("com.google.android.material:material:1.12.0") // Or the latest version
    
    // ViewPager2 for tab navigation
    implementation("androidx.viewpager2:viewpager2:1.0.0")
  
    // CardView and ConstraintLayout
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
}

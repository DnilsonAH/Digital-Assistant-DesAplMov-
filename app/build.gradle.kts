plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //Google services plugins
    id("com.google.gms.google-services")
}

android {
    namespace = "com.shrimpdevs.digitalassistant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shrimpdevs.digitalassistant"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation ("androidx.media3:media3-exoplayer:1.2.0")
    implementation ("androidx.media3:media3-session:1.2.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.compose.material3:material3:1.2.1")
    //Firebase y google services dependencias
    implementation(platform("com.google.firebase:firebase-bom:32.8.0")) // Verifica la versión más reciente en Firebase docs
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.config)
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    // Facebook Sign In
    implementation("com.facebook.android:facebook-login:16.3.0")
    // Para los íconos de Material Design Extended (como Play/Pause, Add, Refresh)
    implementation("androidx.compose.material:material-icons-extended:1.6.7") // Usa la versión actual de tu Compose BOM o la última estable
    // O si estás usando material3, la dependencia es ligeramente diferente para los íconos extendidos
    implementation("androidx.compose.material:material-icons-core:1.6.7")
    implementation("androidx.compose.material:material-icons-extended:1.6.7")
    // O la versión de tu BOM
    implementation("androidx.compose.material:material-icons-extended:1.6.7")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
}
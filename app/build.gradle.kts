import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

// Chargement du keystore partagé (ignoré si absent, ex: GitHub Actions)
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties()
val hasKeystore = keystorePropsFile.exists()
if (hasKeystore) {
    keystoreProps.load(keystorePropsFile.inputStream())
}

android {
    namespace = "com.paullouis.travel"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.paullouis.travel"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (hasKeystore) {
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasKeystore) signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Keystore partagé en local → même SHA-1 pour tous
            // GitHub Actions → debug signing par défaut (pas de keystore.properties)
            if (hasKeystore) signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.swiperefreshlayout)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.ai)
    
    // Additional Dependencies
    implementation(libs.guava)
    implementation(libs.reactive.streams)
    
    // Cloudinary & Glide
    implementation(libs.cloudinary) {
        exclude(group = "com.facebook.fresco")
    }
    implementation(libs.glide)
    
    // Map (OSMDroid)
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Google Play Services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

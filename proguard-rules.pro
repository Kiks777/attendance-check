//
//plugins {
//    alias(libs.plugins.androidApplication)
//}
//
//android {
//    namespace = "com.termproject.ac"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.termproject.ac"
//        minSdk = 27
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//        }
//    }
//    compileOptions {
//
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//}
//
//dependencies {
//
//    implementation("com.google.zxing:core:3.4.1")
//    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
//    implementation("androidx.appcompat:appcompat:1.3.1")
//    implementation("androidx.legacy:legacy-support-v13:1.0.0")
//    implementation("androidx.activity:activity:1.3.1")
//    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
//    implementation("androidx.multidex:multidex:2.0.1")
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    implementation(libs.recyclerview)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//}


// Top-level build file for your module

// Plugin declaration
plugins {
    alias(libs.plugins.androidApplication)
}

// Add the repositories block to specify where to find dependencies
repositories {
    google() // Google's Maven repository
    mavenCentral() // Maven Central repository
}

// Android-specific configuration
android {
    namespace = "com.termproject.ac"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.termproject.ac"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// Dependencies configuration
dependencies {
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.legacy:legacy-support-v13:1.0.0")
    implementation("androidx.activity:activity:1.3.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation ("net.sourceforge.jexcelapi:jxl:2.6.12")
    implementation ("org.apache.poi:poi:5.2.3")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

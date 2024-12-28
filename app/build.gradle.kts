plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 24
        targetSdk = 34
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
        viewBinding = true
    }

    aaptOptions{
        noCompress("tflite")
    }
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.picasso)
    implementation(libs.jsoup.jsoup)
    implementation(libs.okhttp)
    implementation ("org.tensorflow:tensorflow-lite:2.13.0")
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.12.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation (libs.libvlc.all)
    implementation (libs.exoplayer.v2190)

//    implementation ("com.github.WritingMinds:FFmpeg-Android:1.1.2")
//    implementation ("org.ksoap2:ksoap2-android:3.6.4")
    implementation (libs.android.mjpeg.view)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
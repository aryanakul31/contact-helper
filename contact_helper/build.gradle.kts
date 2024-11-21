plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.android.library")
    id("kotlin-parcelize")
    id("maven-publish")
    id("kotlin-android")
}

android {
    namespace = "com.nakul.contact_helper"
    //noinspection GradleDependency
    compileSdk = 33
    buildToolsVersion = "33.0.2"
    version = "1.0.0"

    defaultConfig {
        minSdk = 24
        namespace = "com.nakul.contact_helper" // Replace with your package name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    from(components["release"])
                    groupId = "com.nakul.contacthelper" // Replace with your group ID
                    artifactId = "contact-helper" // Replace with your library name
                    version = "1.0.0"
                }
            }
        }
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


//    Phone Number Validator
    implementation(libs.libphonenumber)

//     Preference
    api(libs.androidx.preference.ktx)

//     Security
    api(libs.androidx.security.crypto)

//    Gson & Json Converter
    implementation(libs.gson)
}


import java.util.Properties

plugins {
    kotlin("kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("maven-publish")
}

val keystorePropertiesFile = rootProject.file("../keys_caregiver/keystore.properties")
val keystoreProperties = Properties().apply {
    load(keystorePropertiesFile.inputStream())
}

buildscript {
    val kotlin_version  = "1.7.20"

    repositories {
        google()
        mavenCentral()
        mavenLocal() // Add this line
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

android {
    namespace = "com.siloamhospitals.siloamcaregiver"
    compileSdk = 34
    val URL_CAREGIVER = "\"https://mysiloam-api-staging.siloamhospitals.com/caregiver/\""
    val ALGORITHM_HASH = "\"HmacSHA256\""
    val ALGORITHM_ENCRYPT = "\"AES\""
    val CHIPER_MODE = "\"AES/CBC/PKCS5PADDING\""

    defaultConfig {
        minSdk = 27
        targetSdk = 34
        version = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "ALGORITHM_HASH", ALGORITHM_HASH)
        buildConfigField("String", "ALGORITHM_ENCRYPT", ALGORITHM_ENCRYPT)
        buildConfigField("String", "CHIPER_MODE", CHIPER_MODE)
    }

    signingConfigs {
        create("releaseConfig") {
            storeFile = rootProject.file(keystoreProperties["releaseStoreFile"] as String)
            storePassword = keystoreProperties["releaseStorePassword"] as String
            keyAlias = keystoreProperties["releaseKeyAlias"] as String
            keyPassword = keystoreProperties["releaseKeyPassword"] as String
        }

        create("debugConfig") {
            storeFile = rootProject.file(keystoreProperties["debugStoreFile"] as String)
            storePassword = keystoreProperties["debugStorePassword"] as String
            keyAlias = keystoreProperties["debugKeyAlias"] as String
            keyPassword = keystoreProperties["debugKeyPassword"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "URL_CAREGIVER", URL_CAREGIVER)
            signingConfig = signingConfigs.getByName("releaseConfig")

        }

        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            applicationIdSuffix = ".dev"
//            versionNameSuffix = "-dev"
            buildConfigField("String", "URL_CAREGIVER", URL_CAREGIVER)
            signingConfig = signingConfigs.getByName("debugConfig")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    kapt {
        correctErrorTypes = true
    }


}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)        // << --- ADD This
    }
}


java {
    sourceCompatibility = JavaVersion.VERSION_17            // << --- ADD This
    targetCompatibility = JavaVersion.VERSION_17
}


dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.4.0-alpha02")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")

    //Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation(  "com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //Koin
    implementation("io.insert-koin:koin-android:3.3.2")

    //chucker
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")

    //Gson
    implementation("com.google.code.gson:gson:2.10.1")

    //okhttp3.logging.HttpLoggingInterceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //retrofit2.converter.gson.GsonConverterFactory
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    //multidex
    implementation("androidx.multidex:multidex:2.0.1")

    //socket.io
    implementation("io.socket:socket.io-client:2.1.0")

    //logger
    implementation("com.orhanobut:logger:2.2.0")

    //recyclical
    implementation("com.afollestad:recyclical:1.1.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")

    //RecyclerViewDivider
    implementation("com.github.fondesa:recycler-view-divider:3.6.0")

    //lottie
    implementation("com.airbnb.android:lottie:5.2.0")

    implementation("com.arthenica:mobile-ffmpeg-full:4.4")

    implementation("com.google.android.flexbox:flexbox:3.0.0")

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.noviangilangbithealth"
            artifactId = "caregiver"
            version = "1.21"

            pom {
                description.set("Caregiver Library")
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

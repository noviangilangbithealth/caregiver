import java.util.Properties

plugins {
    kotlin("kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("maven-publish")
}

val keystorePropertiesFile = rootProject.file("../keys_caregiver/keystore.properties")
val keystoreProperties = Properties().apply {
    load(keystorePropertiesFile.inputStream())
}

buildscript {
    val kotlinVersion = "1.7.20"

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
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
            buildConfigField("String", "URL_CAREGIVER", URL_CAREGIVER)
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        allWarningsAsErrors = false
        freeCompilerArgs = listOf("-Xmodule-name=${project.name}")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        warningsAsErrors = false
    }
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

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Koin
    implementation("io.insert-koin:koin-android:3.3.2")

    // Chucker
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // OkHttp Logging Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Retrofit Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutine Call Adapter
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // Multidex
    implementation("androidx.multidex:multidex:2.0.1")

    // Socket.IO
    implementation("io.socket:socket.io-client:2.1.0")

    // Logger
    implementation("com.orhanobut:logger:2.2.0")

    // Recyclical
    implementation("com.afollestad:recyclical:1.1.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")

    // RecyclerView Divider
    implementation("com.github.fondesa:recycler-view-divider:3.6.0")

    // Lottie
    implementation("com.airbnb.android:lottie:5.2.0")

    // FFmpeg
    implementation("com.arthenica:mobile-ffmpeg-full:4.4")

    // Flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // ExoPlayer
    val mediaVersion = "1.0.1"
    implementation("androidx.media3:media3-exoplayer:$mediaVersion")
    implementation("androidx.media3:media3-ui:$mediaVersion")
    implementation("androidx.media3:media3-exoplayer-dash:$mediaVersion")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-migration:$roomVersion")

    // SQLCipher
    val sqlcipherVersion = "4.5.3"
    implementation("net.zetetic:android-database-sqlcipher:$sqlcipherVersion")

    // SQLite
    val sqliteVersion = "2.3.0"
    implementation("androidx.sqlite:sqlite:$sqliteVersion")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.noviangilangbithealth"
            artifactId = "caregiver"
            version = "1.31"

            pom {
                description.set("Caregiver Library")
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

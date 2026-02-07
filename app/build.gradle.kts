import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

android {
    namespace = "com.hanto.hook"
    compileSdk = 36 // 주의: Android 16(Preview)입니다. 안정적인 배포를 원하시면 35로 낮추세요.

    signingConfigs {
        create("release") {
            storeFile = file("${rootProject.projectDir}/app/release.keystore")
            storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
            keyAlias = "release_key"
            keyPassword = localProperties.getProperty("KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "com.hanto.hook"
        minSdk = 24
        targetSdk = 36
        versionCode = 18
        versionName = "1.1.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            cmake {
                // [수정됨] 16KB 페이지 사이즈 지원을 위한 올바른 설정
                arguments("-DCMAKE_SHARED_LINKER_FLAGS=-Wl,-z,max-page-size=16384")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7") // 버전 안정화 추천
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Android Default
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // ViewPager2 & Indicator
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    // Material & Design
    implementation("com.google.android.material:material:1.11.0")
    implementation("me.grantland:autofittextview:0.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Retrofit & Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okio:okio:3.6.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // OkHttp (안정화 버전 추천)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Jsoup (중복 제거됨)
    implementation("org.jsoup:jsoup:1.17.2")

    // Glide (중복 제거됨)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")

    // Lifecycle & Coroutines
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("com.google.dagger:hilt-android-testing:2.48.1")
    kspTest("com.google.dagger:hilt-compiler:2.48.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48.1")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.48.1")
}
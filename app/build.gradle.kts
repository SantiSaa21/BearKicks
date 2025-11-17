plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    // Quitamos aplicación directa del plugin de Google Services para poder deshabilitarlo en CI.
}

android {
    namespace = "com.bearkicks.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bearkicks.app"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }

    testOptions {
        unitTests.isReturnDefaultValues = true // Evita NPE en clases Android no mockeadas
        unitTests.isIncludeAndroidResources = true // Permite Robolectric/composed tests si se agregan
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("com.google.zxing:core:3.5.2")
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage.ktx)

    implementation(libs.bundles.local)
    ksp(libs.room.compiler)

    implementation(libs.datastore)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.kotlinx.coroutines.play.services)

    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.6")
    // Robolectric para futuros tests que necesiten Android framework sin instrumentación
    testImplementation("org.robolectric:robolectric:4.12.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

}

// --- Secrets / Safety Check ---
// Fail early if the real google-services.json is missing (sample is not used by the plugin)
tasks.register("verifyGoogleServices") {
    doLast {
        val real = file("google-services.json")
        // Allow CI (e.g. Bitrise) to skip strict verification if the file
        // is provided through secure env vars or not needed for pure unit tests.
        val isCi = System.getenv("CI")?.equals("true", ignoreCase = true) == true
        val skipFlag = System.getenv("GOOGLE_SERVICES_SKIP_VERIFY")?.equals("true", ignoreCase = true) == true
        val disableFirebaseProp = gradle.startParameter.projectProperties.containsKey("disableFirebase")
                if (!real.exists()) {
            if (isCi || skipFlag || disableFirebaseProp) {
                                // Crear stub para evitar fallo del plugin si se aplica accidentalmente.
                                logger.warn("[verifyGoogleServices] Missing google-services.json on CI; generating stub.")
                                real.writeText(
                                        """
                                        {
                                            "project_info": {
                                                "project_number": "123456789012",
                                                "project_id": "stub-project",
                                                "storage_bucket": "stub-project.appspot.com"
                                            },
                                            "client": [
                                                {
                                                    "client_info": {
                                                        "mobilesdk_app_id": "1:123456789012:android:stubstubstub",
                                                        "android_client_info": {"package_name": "com.bearkicks.app"}
                                                    },
                                                    "oauth_client": [],
                                                    "api_key": [{"current_key": "STUB_KEY"}],
                                                    "services": {"appinvite_service": {"other_platform_oauth_client": []}}
                                                }
                                            ],
                                            "configuration_version": "1"
                                        }
                                        """.trimIndent()
                                )
                        } else {
                                error("Missing app/google-services.json. Copy app/google-services.sample.json, replace REPLACE_ME with your real API key, and keep the file untracked.")
                        }
                }
    }
}

// Ensure presence before any build tasks
tasks.named("preBuild").configure { dependsOn("verifyGoogleServices") }

// Aplicar plugin Google Services sólo si NO se pasa -PdisableFirebase
if (!project.hasProperty("disableFirebase")) {
    plugins.apply("com.google.gms.google-services")
} else {
    logger.warn("Google Services plugin deshabilitado por -PdisableFirebase (tests sin Firebase)")
}

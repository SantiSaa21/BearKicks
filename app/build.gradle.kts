import java.io.File
import java.util.Properties
import java.net.URL
import java.net.HttpURLConnection
 
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.bearkicks.application"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bearkicks.application"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.3"
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
    buildFeatures { buildConfig = true }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
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
    implementation(libs.androidx.work.runtime.ktx)

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

tasks.register("verifyGoogleServices") {
    doLast {
        val real = file("google-services.json")
        val isCi = System.getenv("CI")?.equals("true", ignoreCase = true) == true
        val skipFlag = System.getenv("GOOGLE_SERVICES_SKIP_VERIFY")?.equals("true", ignoreCase = true) == true
        val disableFirebaseProp = gradle.startParameter.projectProperties.containsKey("disableFirebase")
                if (!real.exists()) {
            if (isCi || skipFlag || disableFirebaseProp) {
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
                                                        "android_client_info": {"package_name": "com.bearkicks.application"}
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

tasks.named("preBuild").configure { dependsOn("verifyGoogleServices") }

fun getLocalProperty(name: String): String? {
    val propsFile = rootProject.file("local.properties")
    if (!propsFile.exists()) return null
    return Properties().apply { propsFile.inputStream().use { load(it) } }.getProperty(name)
}

val localeMapping = mapOf(
    "es-ES" to "values",
    "es-BO" to "values-es-rBO",
    "en-US" to "values-en",
    "zh-CN" to "values-zh-rCN"
)

fun downloadFile(url: String, target: File) {
    val content = URL(url).readText()
    if (!target.parentFile.exists()) target.parentFile.mkdirs()
    target.writeText(content)
}

tasks.register("downloadLocoStrings") {
    group = "localisation"
    description = "Descarga strings.xml desde Localise.biz para cada locale configurado"
    doLast {
        val apiKey = System.getenv("LOCO_API_KEY") ?: getLocalProperty("LOCO_API_KEY")
        if (apiKey.isNullOrBlank()) {
            logger.warn("[downloadLocoStrings] Sin LOCO_API_KEY: se omite descarga de traducciones.")
            return@doLast
        }
        val projectId = System.getenv("LOCO_PROJECT_ID") ?: getLocalProperty("LOCO_PROJECT_ID")
        if (projectId.isNullOrBlank()) {
            logger.warn("[downloadLocoStrings] Sin LOCO_PROJECT_ID: se omite descarga.")
            return@doLast
        }
        localeMapping.forEach { (localeCode, folderName) ->
            val url = "https://localise.biz/api/export/locale/${localeCode}.xml?format=android&key=$apiKey"
            val target = file("src/main/res/$folderName/strings.xml")
            logger.lifecycle("[downloadLocoStrings] Descargando $localeCode → ${target.path}")
            try {
                downloadFile(url, target)
            } catch (e: Exception) {
                logger.error("[downloadLocoStrings] Error descargando $localeCode: ${e.message}")
            }
        }
    }
}

tasks.register("uploadLocoStrings") {
    group = "localisation"
    description = "Sube el archivo base de strings.xml al proyecto Localise.biz"
    doLast {
        val apiKey = System.getenv("LOCO_API_KEY") ?: getLocalProperty("LOCO_API_KEY")
        val projectId = System.getenv("LOCO_PROJECT_ID") ?: getLocalProperty("LOCO_PROJECT_ID")
        if (apiKey.isNullOrBlank() || projectId.isNullOrBlank()) {
            logger.warn("[uploadLocoStrings] Falta LOCO_API_KEY o LOCO_PROJECT_ID; se omite upload.")
            return@doLast
        }
        val baseFile = file("src/main/res/values/strings.xml")
        if (!baseFile.exists()) {
            logger.warn("[uploadLocoStrings] No existe archivo base: ${'$'}{baseFile.path}")
            return@doLast
        }
        val xml = baseFile.readText()
        val url = URL("https://localise.biz/api/import/xml?locale=en-US&overwrite=false&key=${'$'}apiKey")
        try {
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/xml; charset=utf-8")
            conn.outputStream.use { it.write(xml.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val resp = (conn.inputStream ?: conn.errorStream).bufferedReader().readText()
            if (code in 200..299) {
                logger.lifecycle("[uploadLocoStrings] OK ($code). Respuesta: $resp")
            } else {
                logger.error("[uploadLocoStrings] Error ($code). Respuesta: $resp")
            }
        } catch (e: Exception) {
            logger.error("[uploadLocoStrings] Excepción: ${e.message}")
        }
    }
}

tasks.named("preBuild").configure { dependsOn("downloadLocoStrings") }

if (!project.hasProperty("disableFirebase")) {
    plugins.apply("com.google.gms.google-services")
} else {
    logger.warn("Google Services plugin deshabilitado por -PdisableFirebase (tests sin Firebase)")
}

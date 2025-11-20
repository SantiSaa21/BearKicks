import java.io.File
import java.util.Properties
import java.net.URL
import java.net.HttpURLConnection
 
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
    // Ensure BuildConfig is generated
    buildFeatures { buildConfig = true }

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

// --- Localise.biz Strings Download ---
// Descarga las strings de Localise.biz antes de compilar si hay API key.
// Se salta silenciosamente si no hay clave (ej: contributors sin acceso o CI limitado).

fun getLocalProperty(name: String): String? {
    val propsFile = rootProject.file("local.properties")
    if (!propsFile.exists()) return null
    return Properties().apply { propsFile.inputStream().use { load(it) } }.getProperty(name)
}

val localeMapping = mapOf(
    // en-US va al folder base para mantener fallback
    "en-US" to "values",
    "es-ES" to "values-es",
    "es-BO" to "values-es-rBO",
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
        // Algunos proyectos no requieren projectId explícito en la ruta (token ya lo asocia)
        localeMapping.forEach { (localeCode, folderName) ->
            // Endpoint correcto: /api/export/locale/<LOCALE>.xml?format=android&key=TOKEN
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

// --- Localise.biz Upload (subir strings base) ---
// Sube el archivo base (values/strings.xml) al proyecto para crear/actualizar assets.
// Usa la misma LOCO_API_KEY (full access). Evita sobreescribir traducciones; sólo agrega/actualiza claves.
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
        // Endpoint de import XML (según docs de Localise). Uso de POST simple.
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

// Asegurar descarga antes de compilar (después de verificación de google-services)
tasks.named("preBuild").configure { dependsOn("downloadLocoStrings") }

// Aplicar plugin Google Services sólo si NO se pasa -PdisableFirebase
if (!project.hasProperty("disableFirebase")) {
    plugins.apply("com.google.gms.google-services")
} else {
    logger.warn("Google Services plugin deshabilitado por -PdisableFirebase (tests sin Firebase)")
}

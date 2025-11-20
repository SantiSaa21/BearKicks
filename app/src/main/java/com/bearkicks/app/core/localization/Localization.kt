package com.bearkicks.app.core.localization

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Helpers para forzar un locale distinto al del sistema.
 * Uso:
 * val newContext = wrapContextWithLocale(context, Locale("es", "ES"))
 * Luego pasa newContext a setContent / Activity reinicio.
 */
fun wrapContextWithLocale(base: Context, locale: Locale): Context {
    val config = Configuration(base.resources.configuration)
    config.setLocale(locale)
    return base.createConfigurationContext(config)
}

/** Lista de locales soportados por la app. */
val SupportedLocales: List<Locale> = listOf(
    Locale("en", "US"),
    Locale("es", "ES"),
    Locale("es", "BO"),
    Locale("zh", "CN")
)

fun Locale.displayNameShort(): String = when (language) {
    "en" -> "English"
    "es" -> if (country == "BO") "Español (BO)" else "Español"
    "zh" -> "中文 (简体)"
    else -> displayName
}

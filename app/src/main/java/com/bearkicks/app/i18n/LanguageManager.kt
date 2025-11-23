package com.bearkicks.app.i18n

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import java.util.Locale

/**
 * Punto único para cambiar el idioma de la app en runtime.
 * Usa SharedPreferences para persistir selección. Llama a [applySavedLanguage] en App.onCreate.
 * Para cambiar manualmente: LanguageManager.setLanguage(context, AppLanguage.SPANISH)
 */
object LanguageManager {
    private const val PREFS_NAME = "settings"
    private const val KEY_LANG = "app_lang"

    enum class AppLanguage(val tag: String) {
        SPANISH("es"),
        ENGLISH("en"),
        CHINESE("zh")
    }

    private fun prefs(ctx: Context): SharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSavedLanguage(ctx: Context): AppLanguage {
        val tag = prefs(ctx).getString(KEY_LANG, AppLanguage.SPANISH.tag) ?: AppLanguage.SPANISH.tag
        return AppLanguage.values().firstOrNull { it.tag == tag } ?: AppLanguage.SPANISH
    }

    fun setLanguage(ctx: Context, lang: AppLanguage) {
        prefs(ctx).edit().putString(KEY_LANG, lang.tag).apply()
        updateLocale(ctx, lang)
    }

    fun applySavedLanguage(ctx: Context) {
        updateLocale(ctx, getSavedLanguage(ctx))
    }

    private fun updateLocale(ctx: Context, lang: AppLanguage) {
        val locale = Locale.forLanguageTag(lang.tag)
        Locale.setDefault(locale)
        val res = ctx.resources
        val config = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            config.locale = locale
        }
        res.updateConfiguration(config, res.displayMetrics)
        if (ctx is Activity) {
            ctx.recreate() // Forzar recomposición si es Activity normal
        }
    }
}

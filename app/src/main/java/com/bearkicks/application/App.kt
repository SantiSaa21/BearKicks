package com.bearkicks.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.bearkicks.application.R
import com.bearkicks.application.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Aplicar idioma guardado antes de inicializar librerías que puedan leer recursos.
        com.bearkicks.application.i18n.LanguageManager.applySavedLanguage(this)
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        // Create notification channel for promotional reminders
        createNotificationChannel()

        // Schedule promos: in DEBUG every 5 min (one-time, self-rescheduling); in release daily periodic
        val wm = WorkManager.getInstance(this)
        val debugPromos = resources.getBoolean(R.bool.bk_debug_promos)
        if (debugPromos) {
            val debugReq = OneTimeWorkRequestBuilder<com.bearkicks.application.notifications.PeriodicPromoWorker>()
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
            wm.enqueueUniqueWork(
                com.bearkicks.application.notifications.PROMO_WORK_DEBUG_NAME,
                ExistingWorkPolicy.REPLACE,
                debugReq
            )
        } else {
            val request = PeriodicWorkRequestBuilder<com.bearkicks.application.notifications.PeriodicPromoWorker>(24, TimeUnit.HOURS)
                .build()
            wm.enqueueUniquePeriodicWork(
                com.bearkicks.application.notifications.PROMO_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                com.bearkicks.application.notifications.PROMO_CHANNEL_ID,
                getString(R.string.promo_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.promo_channel_desc)
            }
            val mgr = getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }
}

package com.bearkicks.app

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
// Avoid importing R; use fully-qualified to prevent ambiguity
import com.bearkicks.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        // Create notification channel for promotional reminders
        createNotificationChannel()

        // Schedule promos: in DEBUG every 5 min (one-time, self-rescheduling); in release daily periodic
        val wm = WorkManager.getInstance(this)
        val debugPromos = resources.getBoolean(com.bearkicks.app.R.bool.bk_debug_promos)
        if (debugPromos) {
            val debugReq = OneTimeWorkRequestBuilder<com.bearkicks.app.notifications.PeriodicPromoWorker>()
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
            wm.enqueueUniqueWork(
                com.bearkicks.app.notifications.PROMO_WORK_DEBUG_NAME,
                ExistingWorkPolicy.REPLACE,
                debugReq
            )
        } else {
            val request = PeriodicWorkRequestBuilder<com.bearkicks.app.notifications.PeriodicPromoWorker>(24, TimeUnit.HOURS)
                .build()
            wm.enqueueUniquePeriodicWork(
                com.bearkicks.app.notifications.PROMO_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                com.bearkicks.app.notifications.PROMO_CHANNEL_ID,
                getString(com.bearkicks.app.R.string.promo_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(com.bearkicks.app.R.string.promo_channel_desc)
            }
            val mgr = getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }
}

package com.bearkicks.app.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.getKoin
// Avoid importing R to prevent ambiguity with library R classes
import com.bearkicks.app.features.auth.data.datastore.AuthDataStore
import com.bearkicks.app.R

const val PROMO_CHANNEL_ID = "bk_promo"
const val PROMO_WORK_NAME = "bk_periodic_promo"
const val PROMO_WORK_DEBUG_NAME = "bk_debug_promo"

class PeriodicPromoWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val ds: AuthDataStore = getKoin().get()
            val name = ds.getDisplayName() ?: "👟 Fan de BearKicks"
            showNotification(
                title = "Hola, $name",
                text = "Descubre tus ofertas y favoritos de hoy"
            )

            // En modo DEBUG (flag de recursos), reprograma en 5 minutos
            if (applicationContext.resources.getBoolean(com.bearkicks.app.R.bool.bk_debug_promos)) {
                scheduleNextDebug()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(title: String, text: String) {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val builder = NotificationCompat.Builder(applicationContext, PROMO_CHANNEL_ID)
            .setSmallIcon(com.bearkicks.app.R.mipmap.icono_bearkicks)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        NotificationManagerCompat.from(applicationContext).notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), builder.build())
    }

    private fun scheduleNextDebug() {
        val req = OneTimeWorkRequestBuilder<PeriodicPromoWorker>()
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            PROMO_WORK_DEBUG_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }
}

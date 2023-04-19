package com.kieronquinn.app.classicpowermenu.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.github.UpdateChecker
import com.kieronquinn.app.classicpowermenu.ui.activities.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class UpdateCheckWorker(private val context: Context, workerParams: WorkerParameters): Worker(context, workerParams), KoinComponent {

    private val updateChecker by inject<UpdateChecker>()

    companion object {
        private const val UPDATE_CHECK_WORK_TAG = "update_check"
        private const val UPDATE_NOTIFICATION_ID = 1002
        private const val UPDATE_CHECK_HOUR = 12L

        //Removed from WorkManager for some reason???
        private const val MIN_BACKOFF_MILLIS = 10 * 1000L // 10 seconds.

        private fun clearCheckWorker(context: Context){
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(UPDATE_CHECK_WORK_TAG)
        }

        fun queueCheckWorker(context: Context){
            clearCheckWorker(context)
            val checkWorker = Builder().build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(UPDATE_CHECK_WORK_TAG, ExistingPeriodicWorkPolicy.KEEP, checkWorker)
        }
    }

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        GlobalScope.launch {
            updateChecker.getLatestRelease().collect { update ->
                update ?: return@collect
                val notificationManager = NotificationManagerCompat.from(context)
                val channelId = "update_notification"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId, context.getString(R.string.update_notification_channel_title), NotificationManager.IMPORTANCE_DEFAULT)
                    channel.description = context.getString(R.string.update_notification_channel_description)
                    notificationManager.createNotificationChannel(channel)
                }
                val intent = Intent(context, MainActivity::class.java)
                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_settings_power_options)
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setContentTitle(context.getString(R.string.update_notification_title))
                    .setContentText(context.getString(R.string.update_notification_content, BuildConfig.VERSION_NAME, update.name))
                    .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE))
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(UPDATE_NOTIFICATION_ID, notification)
            }
        }
        return Result.success()
    }

    class Builder {
        fun build() : PeriodicWorkRequest {
            val delay = if (LocalDateTime.now().hour < UPDATE_CHECK_HOUR) {
                Duration.between(ZonedDateTime.now(), ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).plusHours(UPDATE_CHECK_HOUR)).toMinutes()
            } else {
                Duration.between(ZonedDateTime.now(), ZonedDateTime.now().toLocalDate().atStartOfDay(ZoneId.systemDefault()).plusDays(1).plusHours(UPDATE_CHECK_HOUR)).toMinutes()
            }

            val constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequest.Builder(UpdateCheckWorker::class.java, 24, TimeUnit.HOURS).addTag(UPDATE_CHECK_WORK_TAG)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build()
        }
    }

}
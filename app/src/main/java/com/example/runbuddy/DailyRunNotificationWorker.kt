package com.example.runbuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDate

class DailyRunNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()

        // TODO: make sure this matches the start Monday you’re using elsewhere.
        val startMonday = LocalDate.of(2025, 12, 15)

        val plan = generateIntermediate1Plan(startMonday)
        val run = plan[today] ?: return Result.success()

        showNotification(run)
        return Result.success()
    }

    private fun showNotification(run: TrainingRun) {
        val context = applicationContext
        createChannelIfNeeded(context)

        val content = buildContentText(run)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or
                    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            pendingIntentFlags,
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Today's run")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun buildContentText(run: TrainingRun): String {
        val distancePart = run.distanceMiles?.let { miles ->
            val milesText = if (miles % 1.0 == 0.0) {
                "${miles.toInt()} mi"
            } else {
                "${miles} mi"
            }
            "$milesText "
        } ?: ""

        val typePart = when (run.type) {
            RunType.PACE -> "pace run"
            RunType.LONG_RUN -> "long run"
            RunType.CROSS_TRAIN -> "cross train"
            RunType.REST -> "rest day"
            RunType.EASY -> "easy run"
            RunType.TEMPO -> "tempo run"
            RunType.INTERVAL -> "interval workout"
        }

        return "Today: $distancePart$typePart"
    }

    private fun createChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily run reminders"
            val descriptionText = "Reminders for each day's scheduled run"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "daily_run_channel"
        const val NOTIFICATION_ID = 1001
    }
}

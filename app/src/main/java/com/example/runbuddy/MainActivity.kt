package com.example.runbuddy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.runbuddy.ui.theme.RunBuddyTheme
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermissionIfNeeded()
        scheduleDailyRunNotifications()

        enableEdgeToEdge()

        setContent {
            RunBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    RunCalendarScreen()
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100,
                )
            }
        }
    }

    private fun scheduleDailyRunNotifications() {
        val workManager = WorkManager.getInstance(this)

        val initialDelayMillis = calculateInitialDelayTo9am()

        val request =
            PeriodicWorkRequestBuilder<DailyRunNotificationWorker>(
                24, TimeUnit.HOURS,
            )
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_run_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    private fun calculateInitialDelayTo9am(): Long {
        val now = ZonedDateTime.now()
        var nextRun = now.withHour(9).withMinute(0).withSecond(0).withNano(0)
        if (now >= nextRun) {
            nextRun = nextRun.plusDays(1)
        }
        return Duration.between(now, nextRun).toMillis()
    }
}

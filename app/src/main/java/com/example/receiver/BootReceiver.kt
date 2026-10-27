package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.WordWalkDatabase
import com.example.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = WordWalkDatabase.getDatabase(context)
                val prefs = db.userPreferencesDao().getUserPreferencesSync()
                if (prefs != null && prefs.reminderEnabled) {
                    NotificationHelper.scheduleDailyReminder(
                        context,
                        prefs.reminderHour,
                        prefs.reminderMinute
                    )
                }
            }
        }
    }
}

package com.shrimpdevs.digitalassistant.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

class TaskReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Tarea pendiente"
        val message = intent.getStringExtra("message") ?: ""

        NotificationHelper.showNotification(context, title, message)
    }
}
fun scheduleEventNotification(
    context: Context,
    eventId: Int,
    title: String,
    description: String,
    eventTimeMillis: Long
) {
    val intent = Intent(context, TaskReminderReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("message", description)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        eventId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerTime = eventTimeMillis+86400000  // hora exacta corregida

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}









// NotificationHelper.kt
package com.example.dancemusicapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dancemusicapp.MainActivity
import com.example.dancemusicapp.R

private const val CHANNEL_ID_LESSONS = "channel_lessons_reminders"
private const val NOTIFICATION_ID_LESSON_REMINDER = 1001

object NotificationHelper {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Напоминания о занятиях"
            val descriptionText = "Напоминания о предстоящих занятиях"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_LESSONS, name, importance).apply {
                description = descriptionText
            }
            // Регистрируем канал в системе
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showLessonReminderNotification(context: Context, lessonTitle: String, lessonTime: String) {
        // Создаём Intent для открытия MainActivity при нажатии на уведомление
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // Используй FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_LESSONS)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Замени на реальный ресурс
            .setContentTitle("Напоминание о занятии")
            .setContentText("У вас запланировано занятие: $lessonTitle в $lessonTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Устанавливаем PendingIntent
            .setAutoCancel(true) // Уведомление исчезнет после нажатия

        with(NotificationManagerCompat.from(context)) {
            // Убедись, что канал создан
            createNotificationChannel(context)
            // Показываем уведомление
            notify(NOTIFICATION_ID_LESSON_REMINDER, builder.build())
        }
    }
}
// CalendarHelper.kt
package com.example.dancemusicapp.utils // Убедись, что пакет правильный

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

// --- Класс-делегат для работы с календарём ---
object CalendarHelper {

    private const val CALENDAR_NAME = "DanceMusicApp Calendar"
    private const val CALENDAR_OWNER_ACCOUNT = "1989ars1989@gmail.com" // Установи реальный email, если возможно

    // Пример метода для проверки разрешений
    fun hasCalendarPermissions(context: Context): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
        val writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
    }

    // Пример метода для запроса разрешений (вызывается из Activity/Fragment)
    fun requestCalendarPermissions(fragment: androidx.fragment.app.Fragment, requestCode: Int) {
        fragment.requestPermissions(
            arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
            requestCode
        )
    }

    // Пример метода для добавления события в календарь
    fun addEventToCalendar(context: Context, title: String, description: String, startTime: Long, endTime: Long, eventId: Long? = null): Boolean {
        if (!hasCalendarPermissions(context)) {
            // Логика обработки отсутствия разрешений (например, вызов requestPermissions)
            println("Calendar permissions not granted.")
            return false
        }

        val cr: ContentResolver = context.contentResolver

        val values = ContentValues().apply {
            put(Events.TITLE, title)
            put(Events.DESCRIPTION, description)
            put(Events.DTSTART, startTime)
            put(Events.DTEND, endTime)
            put(Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            // Найдём ID календаря (например, первичного или с определённым названием)
            val calendarId = getOrCreateCalendarId(cr, context)
            if (calendarId != null) {
                put(Events.CALENDAR_ID, calendarId)
            } else {
                println("Could not find or create a suitable calendar.")
                return false
            }
        }

        return try {
            val uri = if (eventId != null) {
                // Обновление существующего события
                val eventUri = Events.CONTENT_URI.buildUpon().appendPath(eventId.toString()).build()
                cr.update(eventUri, values, null, null)
                eventUri
            } else {
                // Создание нового события
                cr.insert(Events.CONTENT_URI, values)
            }

            if (uri != null) {
                println("Event added/updated to calendar: $uri")
                true
            } else {
                println("Failed to add/update event to calendar.")
                false
            }
        } catch (e: SecurityException) {
            println("SecurityException: Calendar permissions might have been revoked during runtime. $e")
            false
        } catch (e: Exception) {
            println("Error adding/updating event to calendar: $e")
            false
        }
    }

    // Вспомогательный метод для получения ID календаря
    // Попробуем найти календарь по названию. Если не найдём - создадим новый.
    private fun getOrCreateCalendarId(cr: ContentResolver, context: Context): Long? {
        // 1. Попробуем найти календарь по названию
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.CALENDAR_DISPLAY_NAME} = ? AND ${CalendarContract.Calendars.OWNER_ACCOUNT} = ?"
        val selectionArgs = arrayOf(CALENDAR_NAME, CALENDAR_OWNER_ACCOUNT)

        cr.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                val id = cursor.getLong(columnIndex)
                println("Found existing calendar with ID: $id")
                return id
            }
        }

        // 2. Если не найден, попробуем создать
        println("Creating new calendar named: $CALENDAR_NAME")
        val accountName = CALENDAR_OWNER_ACCOUNT // Используем email как имя аккаунта
        val calendarColor = android.graphics.Color.BLUE // Установи цвет по желанию

        val calendarValues = ContentValues().apply {
            put(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
            put(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google") // или другой тип аккаунта
            put(CalendarContract.Calendars.NAME, CALENDAR_NAME.replace(" ", "_")) // Имя календаря без пробелов
            put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME) // Отображаемое имя
            put(CalendarContract.Calendars.CALENDAR_COLOR, calendarColor)
            put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
            put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName)
            put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        }

        val calendarUri = cr.insert(CalendarContract.Calendars.CONTENT_URI, calendarValues)
        return if (calendarUri != null) {
            val newId = java.lang.Long.parseLong(calendarUri.lastPathSegment!!)
            println("Created new calendar with ID: $newId")
            newId
        } else {
            println("Failed to create calendar.")
            null
        }
    }
}
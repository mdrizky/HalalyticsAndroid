package com.example.halalyticscompose.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts a date string to relative time in Indonesian.
 * Handles ISO 8601 and common date formats from the backend.
 */
fun String?.toRelativeTime(): String {
    if (this == null || this == "-" || this.isBlank()) return "-"

    return try {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )

        var date: Date? = null
        for (format in formats) {
            try {
                date = SimpleDateFormat(format, Locale.Builder().setLanguage("id").setRegion("ID").build()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(this)
                if (date != null) break
            } catch (_: Exception) {
                continue
            }
        }

        if (date == null) return this

        val now = Date()
        val diffMs = now.time - date.time
        val diffSec = diffMs / 1000
        val diffMin = diffSec / 60
        val diffHour = diffMin / 60
        val diffDay = diffHour / 24

        when {
            diffSec < 60 -> "Baru saja"
            diffMin < 60 -> "$diffMin menit yang lalu"
            diffHour < 24 -> "$diffHour jam yang lalu"
            diffDay == 1L -> "Kemarin"
            diffDay < 7 -> "$diffDay hari yang lalu"
            else -> {
                SimpleDateFormat("d MMM yyyy", Locale.Builder().setLanguage("id").setRegion("ID").build()).format(date)
            }
        }
    } catch (_: Exception) {
        this
    }
}

/**
 * Formats a date string for history grouping headers.
 */
fun formatHistoryDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.Builder().setLanguage("id").setRegion("ID").build())
        val date = sdf.parse(dateStr) ?: return dateStr
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val targetCal = Calendar.getInstance().apply { time = date }

        when {
            today.get(Calendar.DAY_OF_YEAR) == targetCal.get(Calendar.DAY_OF_YEAR) &&
                    today.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) -> "Hari Ini"
            yesterday.get(Calendar.DAY_OF_YEAR) == targetCal.get(Calendar.DAY_OF_YEAR) &&
                    yesterday.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) -> "Kemarin"
            else -> SimpleDateFormat("EEEE, d MMMM yyyy", Locale.Builder().setLanguage("id").setRegion("ID").build()).format(date)
        }
    } catch (_: Exception) {
        dateStr
    }
}

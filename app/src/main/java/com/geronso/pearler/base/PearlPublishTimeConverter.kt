package com.geronso.pearler.base

import java.time.Instant
import java.util.*
import kotlin.math.max

object PearlPublishTimeConverter {

    fun convert(timestamp: Long): String {
        val timeSeconds = (Instant.now().epochSecond - timestamp)
        val timeMinutes = timeSeconds / 60L
        val timeHours = timeMinutes / 60L
        val timeDays = timeHours / 24L
        return when {
            timeSeconds < 1 -> {
                "pearled just now"
            }
            timeSeconds < 60 -> {
                "pearled ${max(timeSeconds, 0)} seconds ago"
            }
            timeMinutes < 2 -> {
                "pearled $timeMinutes minute ago"
            }
            timeMinutes < 60 -> {
                "pearled $timeMinutes minutes ago"
            }
            timeHours < 2 -> {
                "pearled $timeHours hour ago"
            }
            timeHours < 24 -> {
                "pearled $timeHours hours ago"
            }
            else -> {
                "pearled $timeDays days ago"
            }
        }
    }
}
package com.example.prm1

import java.util.*

class Utils {
    companion object {
        fun millisToCalendar(millis: Long): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            return calendar
        }
    }

}
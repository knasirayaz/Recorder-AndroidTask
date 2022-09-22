package com.knasirayaz.recorder.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeHelpers {

    private val TAG = this::class.java.simpleName
    fun Long.formatDuration() : String = if(this <= 9) "0$this" else "$this"

    fun getCalendarOfDate(date : String): Calendar? {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val calendar = GregorianCalendar()
        calendar.time = formatter.parse(date) as Date
        return calendar
    }


    fun changeDateFormat(date: String, currentFormat: String, requiredFormat: String): String {

//        Log.i(TAG, "changeDateFormat: From: $date as $currentFormat to $requiredFormat")

        if (date.isNullOrEmpty())
            return "--:--"
        return try {
            val formatter = SimpleDateFormat(currentFormat, Locale.ENGLISH)
            val value: Date = formatter.parse(date) as Date
            val dateFormatter = SimpleDateFormat(requiredFormat, Locale.ENGLISH)
            dateFormatter.format(value)
        } catch (e: Exception) {
            e.printStackTrace()
            "Loading..."
        }
    }

    fun getDate(date: String, currentFormat: String): Date {
        val formatter = SimpleDateFormat(currentFormat, Locale.ENGLISH)
        return formatter.parse(date)
    }

    fun minutesToHours(minutes: String): String? {

        val hours: Int = minutes.toInt() / 60
        val min: Int = minutes.toInt() % 60

        return "$hours:$min"
    }

    fun dateToHours(dateStr: String, currentFormat: String): Int {

        val formatter = SimpleDateFormat(currentFormat, Locale.ENGLISH)
        val date: Date = formatter.parse(dateStr)
        val calendar = GregorianCalendar.getInstance() // creates a new calendar instance
        calendar.time = date

        return ((calendar.get(Calendar.HOUR_OF_DAY) * 60) + (calendar.get(Calendar.MINUTE)))
    }

    private fun getDatesDifference(date1Str: String, date2Str: String, dateFormat: String): Long {
        val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        val date1: Date = formatter.parse(date1Str)
        val date2: Date = formatter.parse(date2Str)
        return date2.time - date1.time
    }

    fun getDatesDifference(date1Str: String, date2Str: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date1: Date = formatter.parse(date1Str)
        val date2: Date = formatter.parse(date2Str)
        return date2.time - date1.time
    }

    fun formatDialogDate(year: Int, month: Int, dayOfMonth: Int, dateFormat: String): String {
        return changeDateFormat(
            "$year-${month + 1}-$dayOfMonth",
            "yyyy-MM-dd",
            dateFormat
        )
    }

    fun formatDateWithOrdinal(date: String, dateFormat: String): String {
        val day = changeDateFormat(date, dateFormat, "dd").toIntOrNull() ?: 0
        return String.format(date, getOrdinal(day))
    }

    fun getFormattedTime(millisUntilFinished: Long) : String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        val seconds = (millisUntilFinished / 1000 % 60)
        return "${hours.formatDuration()}:${minutes.formatDuration()}:${seconds.formatDuration()}"
    }



    fun getFormattedDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        var month: String? = null
        var day: String? = null
        val mMonth = monthOfYear + 1

        month = if (mMonth <= 9) {
            "0$mMonth"
        } else {
            "$mMonth"
        }

        day = if (dayOfMonth <= 9) {
            "0$dayOfMonth"
        } else {
            "$dayOfMonth"
        }

        return "$year-$month-$day"
    }


    var date1 = "2021-1-2"
    var date2 = "2021-2-1"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDay(): String {
        val date1 = "01-Jan-2017"
        val date2 = "02-Feb-2017"

        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val d1: LocalDate = LocalDate.parse(date1, df)
        val d2: LocalDate = LocalDate.parse(date2, df)

        val datediff: Long = ChronoUnit.DAYS.between(d1, d2)
        return datediff.toString()
    }

    fun getTodayDate(): String {
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val formattedDate = df.format(c)
        return formattedDate
    }

    //    Get all days
    fun getDatesDifferenceInDays(date1Str: String, date2Str: String): Int {
        val diff = getDatesDifference(date1Str, date2Str)
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    //    Get all days
    fun getDatesDifferenceInDays(date1Str: String, date2Str: String, dateFormat: String): Int {
        val diff = getDatesDifference(date1Str, date2Str, dateFormat)
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

//    //    Get days without weeks
//    fun getDatesOnlyDays(date1Str: String, date2Str: String): Int {
//        val diff = getDatesDifference(date1Str, date2Str)
//        val days = (diff % (1000 * 60 * 60 * 24 * 7))
//        return (days * 24).toInt()
//    }

    //    Get all weeks
    fun getDatesDifferenceInWeeks(date1Str: String, date2Str: String): Int {
        val diff = getDatesDifference(date1Str, date2Str)
        return (diff / (1000 * 60 * 60 * 24 * 7)).toInt()
    }

    fun getDateDifferenceWithCurrentDateInMilliSeconds(date1Str: String): Long {

        val date = changeDateFormat(date1Str, "HH:mm:ss", "HH:mm:ss")

        val formatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        val date1: Date = formatter.parse(date)
        val date2: Date = formatter.parse(getCurrentDateStr("HH:mm:ss"))

        return date1.time - date2.time
    }

    fun millisecondsToDaysHrMinSec(milliseconds: Long): String {
        val seconds: Long = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return days.toString() + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60
    }

    fun millisecondsToHours(milliseconds: Long): String {
        return ((milliseconds / (1000 * 60 * 60 * 24)).toInt()).toString()
    }

    fun getDateDifferenceWithCurrentDateInMinutes(date1Str: String): Long {

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val date1: Date = formatter.parse(date1Str)
        val date2: Date = formatter.parse(getCurrentDateStr("yyyy-MM-dd HH:mm:ss"))

        return (date2.time - date1.time) / 60000
    }

    fun getCurrentDateStr(requiredFormat: String): String {
        val sdf = SimpleDateFormat(requiredFormat, Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentDate() : Date{
        return Calendar.getInstance().time
    }
    fun checkTodayOrTomorrow(date: String, currentFormat: String): String {
        var changedDate = date
        val calendar = Calendar.getInstance()
        if (date == getCurrentDateStr(currentFormat)) {
            changedDate = "Today"
        } else {
            calendar.add(Calendar.DATE, 1)
            if (date == changeDateFormat(
                    calendar.time.toString(),
                    "EEE MMM dd HH:mm:ss Z yyyy",
                    currentFormat
                )
            ) {
                changedDate = "Tomorrow"
            }
        }

        return changedDate
    }

    fun getDatesComparision(
        date1Str: String,
        date2Str: String,
        compareBy: String
    ): Boolean {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date1: Date = formatter.parse(date1Str)
        val date2: Date = formatter.parse(date2Str)

        return when (compareBy) {
            "Equal" -> {
                date1.time == date2.time
            }
            "Low" -> {
                date1.time < date2.time
            }
            "High" -> {
                date1.time > date2.time
            }
            else -> {
                false
            }
        }
    }


    fun convertIntMonthToNameString(month: Int): String {
        when (month) {
            1 -> {
                return "January"
            }
            2 -> {
                return "February"
            }
            3 -> {
                return "March"
            }
            4 -> {
                return "April"
            }
            5 -> {
                return "May"
            }

            6 -> {
                return "June"
            }

            7 -> {
                return "July"
            }

            8 -> {
                return "August"
            }

            9 -> {
                return "September"
            }
            10 -> {
                return "October"
            }
            11 -> {
                return "November"
            }
            12 -> {
                return "December"
            }
            else -> {
                return "January"
            }
        }
    }

    fun secondsToHours(seconds: Int): String {
        val hours = seconds / 3600
        val secondsLeft = seconds - hours * 3600
        val minutes = secondsLeft / 60
        val sec = secondsLeft - minutes * 60
        var formattedTime = ""
        if (hours < 10) formattedTime += "0"
        formattedTime += "$hours:"
        if (minutes < 10) formattedTime += "0"
        formattedTime += "$minutes:"
        if (sec < 10) formattedTime += "0"
        formattedTime += sec

        return formattedTime
    }

    fun getOrdinal(x: Int?): String {
        return if (x != null) {
            if (x == 11 || x == 12 || x == 13) {
                "th"
            } else {
                when (x % 10) {
                    1 -> "st"
                    2 -> "nd"
                    3 -> "rd"
                    else -> "th"
                }
            }

        } else
            ""
    }

    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    fun getTimeAgo(time: Long): String {
        var time = time
        if (time < 1000000000000L) {
            time *= 1000
        }
        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return ""
        }
        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "a min ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
           ( diff / MINUTE_MILLIS).toString() + " " + "mins ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "an hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
           ( diff / HOUR_MILLIS).toString() + " " + "hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            "yesterday"
        } else {
            (diff / DAY_MILLIS).toString() + " " + "days ago"
        }
    }

    fun getDurationString(seconds: Int?): String? {
        val date: Date = Date(seconds?.times(1000L) ?: 0)
        val formatter = SimpleDateFormat(if (seconds!! >= 3600) "HH:mm:ss" else "mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("GMT")
        return formatter.format(date)
    }

    fun formateMilliSeccond(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"
        return finalTimerString
    }

}
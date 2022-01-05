package top.iseason.heping.utils

import java.util.*
import kotlin.math.abs

object Util {
    @JvmStatic
    fun longTimeFormat(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = ((time - hours * 3600000) / 60000).toInt()
        if (hours == 0) return "${minutes}分钟"
        return "${hours}小时${minutes}分钟"
    }

    @JvmStatic
    fun longTimeFormat2(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = ((time - hours * 3600000) / 60000).toInt()
        return "${formatTime2(hours)}:${formatTime2(minutes)}"
    }

    @JvmStatic
    fun longTimeFormat3(time: Long): String {
        val minutes = (time / 60000).toInt()
        val second = (((time - minutes * 60000)) / 1000L).toInt()
        return "${formatTime2(minutes)}:${formatTime2(second)}"
    }


    @JvmStatic
    fun longTimeFormatDetail(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = (time - hours * 3600000) / 60000
        val second = (time - hours * 3600000 - minutes * 60000) / 1000
        return "${hours}时${minutes}分${second}秒"
    }

    @JvmStatic
    fun longTimeFormatDetail2(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = ((time - hours * 3600000) / 60000).toInt()
        val second = ((time - hours * 3600000 - minutes * 60000) / 1000).toInt()
        if (hours == 0) return "${formatTime2(minutes)}:${formatTime2(second)}"
        return "${formatTime2(hours)}:${formatTime2(minutes)}:${formatTime2(second)}"
    }

    @JvmStatic
    fun longTimeFormatDetail3(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = ((time - hours * 3600000) / 60000).toInt()
        val second = ((time - hours * 3600000 - minutes * 60000) / 1000).toInt()
        return "${formatTime2(hours)}:${formatTime2(minutes)}:${formatTime2(second)}"
    }

    @JvmStatic
    fun getDate(day: Int): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DATE, day)
    }

    @JvmStatic
    fun toHour(hour: Int): Int = if (hour > 23 || hour < 0) 24 - abs(hour) else hour

    @JvmStatic
    fun toMinute(minute: Int): Int = if (minute > 59 || minute < 0) 64 - abs(minute) else minute

    fun formatTime2(num: Int): String = if (num in 0..9) "0$num" else num.toString()


}
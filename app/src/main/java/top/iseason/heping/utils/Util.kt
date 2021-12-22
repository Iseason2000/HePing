package top.iseason.heping.utils

object Util {
    @JvmStatic
    fun longTimeFormat(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = (time - hours * 3600000) / 60000
        if (hours == 0) return "${minutes}分钟"
        return "${hours}小时${minutes}分钟"
    }

    @JvmStatic
    fun longTimeFormatDetail(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = (time - hours * 3600000) / 60000
        val second = (time - hours * 3600000 - minutes * 60000) / 1000
        return "${hours}时${minutes}分${second}秒"
    }
}
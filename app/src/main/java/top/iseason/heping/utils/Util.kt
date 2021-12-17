package top.iseason.heping.utils

object Util {
    @JvmStatic
    fun longTimeFormat(time: Long): String {
        val hours = (time / 3600000).toInt()
        val minutes = (time - hours * 3600000) / 60000
        return "${hours}小时${minutes}分钟"
    }
}
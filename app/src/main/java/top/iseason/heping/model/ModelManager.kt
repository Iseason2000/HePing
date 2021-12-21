package top.iseason.heping.model

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context.USAGE_STATS_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import top.iseason.heping.MainActivity
import java.util.*


object ModelManager {
    lateinit var usageStatsManager: UsageStatsManager
    private lateinit var activity: MainActivity
    private lateinit var packageManager: PackageManager

    fun setMainActivity(activity: MainActivity) {
        this.activity = activity
        usageStatsManager = activity.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        packageManager = activity.packageManager
    }

    fun getMainActivity() = this.activity

    private fun getPackageManager() = this.packageManager

    //核心算法
    fun queryUsageStatsAllDay(): List<AppInfo> {
        //时间范围 1天
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        calendar.add(Calendar.DATE, 1)
        val endTime = calendar.timeInMillis
        var currentEvent: UsageEvents.Event
        val eventsByPackage: MutableMap<String, MutableList<UsageEvents.Event>> = mutableMapOf()
        val packageManager = getPackageManager()
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val infoList = mutableListOf<AppInfo>()
        //按包名分类
        while (usageEvents.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)
            val eventType = currentEvent.eventType
            if (!(eventType == UsageEvents.Event.ACTIVITY_RESUMED || eventType == UsageEvents.Event.ACTIVITY_PAUSED || eventType == UsageEvents.Event.ACTIVITY_STOPPED))
                continue
            val packageName = currentEvent.packageName
            if (eventsByPackage.containsKey(packageName)) {
                val eventList = eventsByPackage[packageName]!!
                eventList.add(currentEvent)
            } else {
                eventsByPackage[packageName] = mutableListOf(currentEvent)
            }
        }

        for ((packName, eventList) in eventsByPackage) {
            var launchCount = 0
            var timeZone = 0 //0..23
            var startTimeN = startTime
            val size = eventList.size
            val fistEvent = eventList[0]
            val appInfo = AppInfo(packName)
            val applicationInfo: ApplicationInfo
            try {
                applicationInfo = packageManager.getApplicationInfo(
                    packName,
                    PackageManager.GET_META_DATA
                )
            } catch (e: Exception) {
                continue
            }
            if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1 ||
                (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1
            ) continue
            appInfo.appName = packageManager.getApplicationLabel(applicationInfo).toString()
            appInfo.icon = applicationInfo.loadIcon(packageManager).toBitmap().asImageBitmap()

            //从昨天用到现在的补偿
            if (fistEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED || fistEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                var expand = 0
                val fistTime = fistEvent.timeStamp
                //确定跨区
                while (fistTime >= startTime + 3600000L * (expand + 1)) expand++
                timeZone = expand
                startTimeN = startTime + 3600000L * expand
                appInfo.useTime[timeZone] = fistEvent.timeStamp - startTimeN
            }
            for (index in 0 until size - 1) {
                val event1 = eventList[index]
                val event2 = eventList[index + 1]
                if (event1.eventType != UsageEvents.Event.ACTIVITY_RESUMED) continue
                if (!(event2.eventType == UsageEvents.Event.ACTIVITY_STOPPED || event2.eventType == UsageEvents.Event.ACTIVITY_PAUSED)) continue
                //开始分配时间区间
                val time1 = event1.timeStamp
                val time2 = event2.timeStamp
                if (time2 - time1 > 1000L) launchCount++
                //时间区域
                if (time1 in startTimeN..(startTimeN + 3600000L)) {
                    //前端位于区间内
                    if (time2 < (startTimeN + 3600000L)) {
                        //后端位于区间内,时间区域不变
                        appInfo.useTime[timeZone] += (time2 - time1)
                    } else {
                        //后端位于区间外
                        //添加当前时间
                        appInfo.useTime[timeZone] += (startTimeN + 3600000L - time1)
                        var expand = 1
                        //找到右移区间数
                        while (time2 >= (startTimeN + 3600000L * (expand + 1))) expand++
                        //区间,时间起始点位移
                        //添加填充区间
                        for (i in 1 until expand) {
                            appInfo.useTime[timeZone + i] += 3600000L
                        }
                        //区间位移
                        timeZone += expand
                        //设置
                        appInfo.useTime[timeZone] += (time2 - (startTimeN + 3600000L * expand))
                        startTimeN += (3600000L * expand)
                    }
                } else {
                    //前端不位于区间内 = 整体位于区间外,不会超过endTIme
                    var expand = 1
                    //确定跨区
                    while (time1 >= (startTimeN + 3600000L * (expand + 1))) expand++
                    timeZone += expand
                    startTimeN += (3600000L * expand)
                }
            }
            //还在运行，不计入时间,但统计次数
            val lastEvent = eventList[size - 1]
            if (lastEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) launchCount++
            //时间统计完毕，设置其他属性
            appInfo.launchCount = launchCount
            infoList.add(appInfo)
        }
        return infoList
    }

}

data class AppInfo(
    var packageName: String,
    var appName: String = "",
    var launchCount: Int = 0,
    var useTime: Array<Long> = Array(24) { 0L },
    var icon: ImageBitmap = ImageBitmap(1, 1),
) {
    fun getTotalTime(): Long {
        var total = 0L
        for (l in useTime) {
            total += l
        }
        return total
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AppInfo
        if (packageName != other.packageName) return false
        if (appName != other.appName) return false
        if (launchCount != other.launchCount) return false
        if (!useTime.contentEquals(other.useTime)) return false
        if (icon != other.icon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + appName.hashCode()
        result = 31 * result + launchCount
        result = 31 * result + useTime.contentHashCode()
        result = 31 * result + icon.hashCode()
        return result
    }
}

package top.iseason.heping.model

import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.app.usage.UsageStatsManager.INTERVAL_BEST
import android.content.Context.USAGE_STATS_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import top.iseason.heping.MainActivity
import java.lang.reflect.Field


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

    fun getPackageManager() = this.packageManager

    fun queryUsageStatistics(
        startTime: Long,
        endTime: Long
    ): HashMap<String, AppInfo> {
        var currentEvent: UsageEvents.Event
        val allEvents: MutableList<UsageEvents.Event> = ArrayList()
        val map: HashMap<String, AppInfo> = HashMap()
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val packageManager = getPackageManager()
        while (usageEvents.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)
            val packageName = currentEvent.packageName
            val eventType = currentEvent.eventType
            if (!(eventType == UsageEvents.Event.ACTIVITY_RESUMED || eventType == UsageEvents.Event.ACTIVITY_PAUSED || eventType == UsageEvents.Event.ACTIVITY_STOPPED))
                continue
            allEvents.add(currentEvent)
            if (map.containsKey(packageName)) continue
            val applicationInfo: ApplicationInfo
            val asImageBitmap: ImageBitmap
            val appName: String
            try {
                applicationInfo = packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                )
                appName = packageManager.getApplicationLabel(applicationInfo).toString()
                //解决 小米 读取问题
                asImageBitmap = applicationInfo.loadIcon(packageManager).toBitmap().asImageBitmap()
            } catch (e: Exception) {
                continue
            }
            if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1 || (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) continue
            map[packageName] = AppInfo(packageName, appName, 0, 0, asImageBitmap)
        }
        val sortedEvents = allEvents.sortedBy { it.packageName }
        for (i in 0 until sortedEvents.size - 1) {
            val event0 = sortedEvents[i]
            val event1 = sortedEvents[i + 1]
            //println("${event0.packageName} ${event0.eventType} -> ${event1.packageName} ${event1.eventType}")
//            if (event0.packageName != event1.packageName && event1.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
//                val appInfo = map[event1.packageName]
//                if (appInfo != null) {
//                    appInfo.launchCount++
//                }
//            }
            if (event0.eventType == UsageEvents.Event.ACTIVITY_RESUMED &&
                (event1.eventType == UsageEvents.Event.ACTIVITY_PAUSED || event1.eventType == UsageEvents.Event.ACTIVITY_STOPPED)
                && event0.packageName == event1.packageName
            ) {
//                println("${event0.packageName} ${event0.timeStamp} ${event1.timeStamp}")
                val diff = event1.timeStamp - event0.timeStamp
                val appUsageInfo = map[event0.packageName]
                if (appUsageInfo != null) {
                    appUsageInfo.useTime += diff
                    appUsageInfo.launchCount++
                }
            }
        }
        return map
    }

    @Deprecated("不太准")
    fun getAppInfoListInTime(
        startTime: Long,
        endTime: Long
    ): List<AppInfo> {
        val list = queryRawUsageStatisticsInTime(startTime, endTime)
        val map = mutableMapOf<String, AppInfo>()
        for (appInfo in list) {
            val packageName = appInfo.packageName
            val appInfo1 = map[packageName]
            if (appInfo1 == null) {
                val applicationInfo: ApplicationInfo
                try {
                    applicationInfo = packageManager.getApplicationInfo(
                        packageName,
                        PackageManager.GET_META_DATA
                    )
                } catch (e: Exception) {
                    continue
                }
                if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1 ||
                    (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1
                ) continue
                val appName: String = packageManager.getApplicationLabel(applicationInfo).toString()
                val asImageBitmap: ImageBitmap =
                    applicationInfo.loadIcon(packageManager).toBitmap().asImageBitmap()
                map[packageName] = appInfo.copy(appName = appName, icon = asImageBitmap)
            } else {
                appInfo1.plus(appInfo)
            }
        }
        return map.values.toList()
    }

    /**
     * 根据时间段获取应用使用数据，只有包名 启动次数 和 使用时间
     */
    @SuppressLint("DiscouragedPrivateApi")
    fun queryRawUsageStatisticsInTime(
        startTime: Long,
        endTime: Long
    ): MutableList<AppInfo> {
        val usageStats = usageStatsManager.queryUsageStats(INTERVAL_BEST, startTime, endTime)
        val mutableListOf = mutableListOf<AppInfo>()
        //对数据整理分析
        for (stats in usageStats) {
            val lastTimeUsed = stats.lastTimeUsed
            var useTime = stats.totalTimeInForeground
            println("${stats.packageName} ${stats.lastTimeUsed} ${stats.firstTimeStamp} ${stats.lastTimeStamp} use ${stats.totalTimeInForeground} ")
            //范围之外的数据
            if (lastTimeUsed + useTime < startTime) continue
//            if (stats.lastTimeUsed == stats.lastTimeStamp) continue
            if (lastTimeUsed < startTime) {
                useTime -= (startTime - lastTimeUsed)
            }
            var field: Field? = null
            try {
                field = stats.javaClass.getDeclaredField("mLaunchCount")
            } catch (e: Exception) {
            }
            val count: Int = if (field == null) 1 else field.get(stats) as Int
            val packageName = stats.packageName
            mutableListOf.add(AppInfo(packageName, launchCount = count, useTime = useTime))
        }
        return mutableListOf
    }
}

data class AppInfo(
    var packageName: String,
    var appName: String = "",
    var launchCount: Int = 0,
    var useTime: Long = 0,
    var icon: ImageBitmap = ImageBitmap(1, 1),
) {
    fun plus(other: AppInfo) {
        launchCount += other.launchCount
        useTime += other.useTime
    }
}

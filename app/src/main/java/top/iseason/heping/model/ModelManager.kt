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

object ModelManager {
    lateinit var usageStatsManager: UsageStatsManager
    private lateinit var activity: MainActivity
    private lateinit var packageManager: PackageManager

    @JvmName("setMainActivity1")
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
            map[packageName] = AppInfo(appName, 0, 0, asImageBitmap)
        }
        for (i in 0 until allEvents.size - 1) {
            val event0 = allEvents[i]
            val event1 = allEvents[i + 1]
            if (event0.packageName != event1.packageName && event1.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                val appInfo = map[event1.packageName]
                if (appInfo != null)
                    appInfo.launchCount++
            }
            if (event0.eventType == UsageEvents.Event.ACTIVITY_RESUMED &&
                (event1.eventType == UsageEvents.Event.ACTIVITY_PAUSED || event1.eventType == UsageEvents.Event.ACTIVITY_STOPPED)
                && event0.packageName == event1.packageName
            ) {
                val diff = event1.timeStamp - event0.timeStamp
                val appUsageInfo = map[event0.packageName]
                if (appUsageInfo != null)
                    appUsageInfo.useTime += diff
            }
        }
        return map
    }
}

data class AppInfo(
    var appName: String,
    var launchCount: Int = 0,
    var useTime: Long = 0,
    var icon: ImageBitmap,
)
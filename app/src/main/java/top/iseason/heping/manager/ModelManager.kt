package top.iseason.heping.manager

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context.*
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.PowerManager
import android.os.Process
import android.os.Vibrator
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import top.iseason.heping.MainActivity
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.utils.Util
import java.util.*


@SuppressLint("StaticFieldLeak")
object ModelManager {
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var activity: MainActivity
    private lateinit var packageManager: PackageManager
    private lateinit var windowManager: WindowManager
    private var vibrator: Vibrator? = null
    private var layoutInflater: LayoutInflater? = null
    private var powerManager: PowerManager? = null
    private var viewModel = AppViewModel()
    private var service: AppService? = null
    private var isBound = false
    val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            isBound = true
            val myBinder: AppService.MyBinder = binder as AppService.MyBinder
            service = myBinder.appService
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    @SuppressLint("StaticFieldLeak")
    private lateinit var navController: NavHostController

    @SuppressLint("ServiceCast")
    fun setMainActivity(activity: MainActivity) {
        ModelManager.activity = activity
        usageStatsManager = activity.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        EventManager.usageStatsManager = usageStatsManager
        powerManager = activity.getSystemService(POWER_SERVICE) as PowerManager
        windowManager = ModelManager.activity.getSystemService(WINDOW_SERVICE) as WindowManager
        layoutInflater =
            activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        packageManager = activity.packageManager
        activity.bindService(Intent(activity, AppService::class.java), conn, BIND_AUTO_CREATE)
        vibrator = activity.getSystemService(VIBRATOR_SERVICE) as Vibrator?
    }

    fun getService() = service
    fun getViewModel() = viewModel
    fun setViewModel(model: AppViewModel) {
        viewModel = model
    }

    fun getLayoutInflater() = layoutInflater
    fun getWindowManager() = windowManager
    fun getPowerManager() = powerManager
    fun getUsageStatsManager() = usageStatsManager
    fun setNavHostController(navController: NavHostController) {
        ModelManager.navController = navController
    }

    fun getMainActivity() = activity
    fun getNavController() = navController
    fun isInteractive(): Boolean {
        val powerManager1 = powerManager ?: return false
        return powerManager1.isInteractive
    }

    fun showToast(text: String) {
        Toast.makeText(getMainActivity(), text, Toast.LENGTH_SHORT).show()
    }

    private fun getPackageManager() = packageManager

    /**
     * 核心算法，按小时统计应用前台使用时间
     * @param day 时间偏差,0为今天
     *
     */
    fun queryUsageStatsForDays(day: Int): List<AppInfo> {
        val calendar = Util.getDate(-day)
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
                appInfo.icon = applicationInfo.loadIcon(packageManager).toBitmap().asImageBitmap()
            } catch (e: Exception) {
                continue
            }
            //过滤系统应用
//            if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1 ||
//                (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1
//            ) continue
            appInfo.appName = packageManager.getApplicationLabel(applicationInfo).toString()

            //从昨天用到现在的补偿
            if (fistEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED || fistEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                var expand = 0
                val fistTime = fistEvent.timeStamp
                //确定跨区
                while (fistTime >= startTime + 3600000L * (expand + 1)) expand++
                timeZone = expand
                startTimeN = startTime + 3600000L * expand
                appInfo.useTime[timeZone] = fistTime - startTimeN
            }
            for (index in 0 until size - 1) {
                val event1 = eventList[index]
                val event2 = eventList[index + 1]
                if (event1.eventType == UsageEvents.Event.ACTIVITY_PAUSED) continue
                if (!(event2.eventType == UsageEvents.Event.ACTIVITY_STOPPED || event2.eventType == UsageEvents.Event.ACTIVITY_PAUSED)) continue
                //开始分配时间区间
                val time1 = event1.timeStamp
                val time2 = event2.timeStamp
                //启动阈值
                if (time2 - time1 > 3000L) launchCount++
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
            //尾切
            val lastEvent = eventList[size - 1]
            if (lastEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                launchCount++
                if (day == 0)
                    appInfo.useTime[timeZone] += (System.currentTimeMillis() - lastEvent.timeStamp)
                else
                    appInfo.useTime[timeZone] += (endTime - lastEvent.timeStamp)
            }
            //时间统计完毕，设置其他属性
            appInfo.launchCount = launchCount
            infoList.add(appInfo)
        }
        return infoList
    }

    fun queryAppUseTime(packageName: String): Pair<String, Long> {
        val calendar = Util.getDate(0)
        val startTime = calendar.timeInMillis
        calendar.add(Calendar.DATE, 1)
        val endTime = calendar.timeInMillis
        val eventList = mutableListOf<UsageEvents.Event>()
        var currentEvent: UsageEvents.Event
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        while (usageEvents.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)
            val eventType = currentEvent.eventType
            if (!(eventType == UsageEvents.Event.ACTIVITY_RESUMED || eventType == UsageEvents.Event.ACTIVITY_PAUSED || eventType == UsageEvents.Event.ACTIVITY_STOPPED))
                continue
            if (packageName != currentEvent.packageName) continue
            eventList.add(currentEvent)
        }
        var total = 0L
        if (eventList.isEmpty()) return Pair(packageName, 0L)
        val fistEvent = eventList[0]
        if (fistEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED || fistEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
            total += (fistEvent.timeStamp - startTime)
        }
        for (index in 0 until eventList.size - 1) {
            val event1 = eventList[index]
            val event2 = eventList[index + 1]
            if (event1.eventType == UsageEvents.Event.ACTIVITY_PAUSED) continue
            if (!(event2.eventType == UsageEvents.Event.ACTIVITY_STOPPED || event2.eventType == UsageEvents.Event.ACTIVITY_PAUSED)) continue
            total += (event2.timeStamp - event1.timeStamp)
        }
        val lastEvent = eventList[eventList.size - 1]
        if (lastEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
            total += (System.currentTimeMillis() - lastEvent.timeStamp)
        }
        val applicationInfo: ApplicationInfo
        try {
            applicationInfo = packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
        } catch (e: Exception) {
            return Pair(packageName, total)
        }
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        return Pair(appName, total)
    }

    fun openSuspendedWindowPermission() {
        if (!hasPermission(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW)) {
            Toast.makeText(activity, "在设置里找到 和屏 然后开启权限!", Toast.LENGTH_LONG).show()
            getMainActivity().startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }

    fun lockScreen() {
        val mainActivity = getMainActivity()
        val adminComponent = ComponentName(mainActivity, Admin::class.java)
        val devicePolicyManager =
            mainActivity.getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏,非常好用")
            mainActivity.startActivityForResult(intent, 1)
        } else {
            devicePolicyManager.lockNow()

        }
    }

    fun tip() {
        if (ConfigManager.getBoolean("Focus-Setting-TipSound")) {
            tipSound()
        }
        vibrator()
    }

    private fun tipSound() {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(getMainActivity(), notification)
        r.play()
    }

    private fun vibrator() {
        vibrator?.vibrate(500L)
    }

    fun setHideFromRecent(enabled: Boolean) {
        (activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager).let {
            val tasks = it.appTasks
            if (!tasks.isNullOrEmpty()) {
                tasks[0].setExcludeFromRecents(enabled)
            }
        }
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

fun hasPermission(permission: String): Boolean {
    val mode = (ModelManager.getMainActivity()
        .getSystemService(APP_OPS_SERVICE) as AppOpsManager)
        .checkOpNoThrow(
            permission,
            Process.myUid(), ModelManager.getMainActivity().packageName
        )
    return mode == AppOpsManager.MODE_ALLOWED
}
package top.iseason.heping.model

import android.app.usage.UsageStatsManager
import android.content.Context.USAGE_STATS_SERVICE
import android.content.pm.PackageManager
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

}
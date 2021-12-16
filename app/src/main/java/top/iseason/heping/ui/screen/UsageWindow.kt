package top.iseason.heping.ui.screen

import android.app.usage.UsageStatsManager.INTERVAL_DAILY
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import top.iseason.heping.model.ModelManager
import java.util.*

@Composable
fun UsageWindow() {
    var usageList by remember { mutableStateOf(listOf<AppInfo>()) }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val queryUsageStats = ModelManager.usageStatsManager.queryUsageStats(
            INTERVAL_DAILY,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )
        if (queryUsageStats.isEmpty()) {
            Toast.makeText(ModelManager.getMainActivity(), "你没有应用读取权限，请先允许!", Toast.LENGTH_LONG)
                .show()
            ModelManager.getMainActivity()
                .startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        } else {
            val mutableListOf = mutableListOf<AppInfo>()
            val packageManager = ModelManager.getPackageManager()
            queryUsageStats.forEach {
                val packageName = it.packageName
                if (packageName.startsWith("com.android")) return@forEach
                mutableListOf.add(
                    AppInfo(
                        it.totalTimeInForeground,
                        packageManager.getApplicationLabel(
                            packageManager.getApplicationInfo(
                                packageName,
                                PackageManager.GET_META_DATA
                            )
                        ).toString(),
                        packageManager.getApplicationIcon(packageName).toBitmap().asImageBitmap()
                    )
                )
            }
            usageList = mutableListOf
        }
    }
    Items(usageList)
}

@Composable
fun Item(appInfo: AppInfo) {
    Row {


        Column() {
            Box(
                modifier = Modifier
                    .size(17.dp, 70.dp)
                    .background(color = MaterialTheme.colors.primary)
            ) {

            }
            Image(
                bitmap = appInfo.icon,
                contentDescription = appInfo.appName,
                modifier = Modifier.size(17.dp, 17.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
    }
}

@Composable
fun Items(appInfoList: List<AppInfo>) {
    if (appInfoList.isNotEmpty()) {
        val sorted = appInfoList.sortedByDescending { it.useTime }
        LazyRow {
            items(sorted) { appInfo ->
                Item(appInfo)
            }
        }
    }
}

data class AppInfo(
    val useTime: Long,
    val appName: String,
    val icon: ImageBitmap
)
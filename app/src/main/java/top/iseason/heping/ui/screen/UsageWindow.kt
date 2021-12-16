package top.iseason.heping.ui.screen

import android.graphics.drawable.Drawable
import androidx.compose.runtime.*
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
        //todo:解析应用数据
//        ModelManager.usageStatsManager.queryUsageStats(
//            INTERVAL_DAILY,
//            calendar.timeInMillis,
//            System.currentTimeMillis()
//        ).stream().filter {
//            !(it.packageName.startsWith("com.android") ||
//                    it.packageName.startsWith("com.google.android"))
//        }.toList()
//        if (usageList.isEmpty()) {
//            ModelManager.getMainActivity()
//                .startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
//        }
    }
    Items(usageList)
}

@Composable
fun Item(itemState: AppInfo) {
//    val useTime = itemState.totalTimeInForeground
//    println(itemState.packageName)
//    println(itemState.totalTimeInForeground)
//    println("${itemState.totalTimeInForeground / 1000}s")
//    println("${itemState.totalTimeInForeground / 60000}m")
    ModelManager.getPackageManager().getApplicationIcon(itemState.packageName)
}

@Composable
fun Items(itemStates: List<AppInfo>) {
    if (itemStates.isNotEmpty()) {
        Item(itemStates[0])
    }

}

data class AppInfo(
    val useTime: Long,
    val packageName: String,
    val icon: Drawable
)
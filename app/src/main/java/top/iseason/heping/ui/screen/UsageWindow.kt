package top.iseason.heping.ui.screen

import android.app.usage.UsageStatsManager.INTERVAL_DAILY
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.model.AppInfo
import top.iseason.heping.model.ModelManager
import top.iseason.heping.utils.Util
import java.util.*

@Composable
fun UsageWindow(modifier: Modifier = Modifier) {
    var usageList by remember { mutableStateOf(listOf<AppInfo>()) }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
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
            usageList = ModelManager.queryUsageStatistics(
                calendar.timeInMillis,
                System.currentTimeMillis()
            ).values.toList().sortedByDescending { it.useTime }
        }
    }
    if (usageList.isNotEmpty()) {
        var totalTime = 0L
        usageList.forEach { totalTime += it.useTime }
        val maxUseTime = usageList[0].useTime
        Surface(
            modifier = modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(5.dp))

        ) {
            Canvas(modifier = Modifier) {
                for (i in 0..6) {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(100f, 832f - 90 * i),
                        end = Offset(900f, 832f - 90 * i),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "今日使用屏幕共计",
                    modifier = Modifier.size(144.dp, 26.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = Util.longTimeFormat(totalTime),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Items(usageList, maxUseTime)
            }
        }
    }

}

@Composable
fun Item(appInfo: AppInfo, maxTime: Long) {
    println("${appInfo.appName} ${appInfo.useTime}")
    val percentage = (appInfo.useTime.toFloat() / maxTime.toFloat())
    Row() {
        Column() {
            Box(
                modifier = Modifier
                    .size(17.dp, (200.0 * percentage).toInt().dp)
                    .clip(RoundedCornerShape(20))
                    .background(color = MaterialTheme.colors.primaryVariant)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                bitmap = appInfo.icon,
                contentDescription = appInfo.appName,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .size(17.dp, 17.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
    }
}

@Composable
fun Items(appInfoList: List<AppInfo>, maxUseTime: Long) {
    LazyRow(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(230.dp)
    ) {
        items(appInfoList) { appInfo ->
            Item(appInfo, maxUseTime)
        }
    }
}


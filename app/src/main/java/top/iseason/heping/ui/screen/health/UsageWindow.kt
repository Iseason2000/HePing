package top.iseason.heping.ui.screen.health

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.model.ModelManager
import top.iseason.heping.utils.Util

@Composable
fun UsageWindow(modifier: Modifier = Modifier, viewModel: AppViewModel) {

    val viewState by viewModel.viewState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.updateAppInfo()
    }
    Surface(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 305.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(5.dp))
    ) {
        val appInfo = viewState.appInfo
        if (appInfo.isNotEmpty()) {
            var totalTime = 0L
            appInfo.forEach { totalTime += it.useTime }
            val maxUseTime = appInfo[0].useTime
            Canvas(modifier = Modifier.requiredSize(305.dp)) {
                val fl = size.height / 305.0F
                for (i in 0..6) {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(60f, size.height - (37 + 29F * i) * fl),
                        end = Offset(size.width - 60f, size.height - (37 + 29F * i) * fl),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            ) {
                Text(
                    text = "今日使用屏幕共计",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = Util.longTimeFormat(totalTime),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                )
                Items(appInfo, maxUseTime)
            }
        } else {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "你还没有授予权限，将无法显示应用统计!")
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        Toast.makeText(
                            ModelManager.getMainActivity(),
                            "在设置里找到 和屏 然后开启权限!",
                            Toast.LENGTH_LONG
                        ).show()
                        ModelManager.getMainActivity()
                            .startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }) {
                        Text(text = "去授予")
                    }
                }
            }
        }
    }
}

@Composable
fun Item(appInfo: AppInfo, maxTime: Long) {
    println(appInfo.useTime)
    val percentage = (appInfo.useTime.toFloat() / maxTime.toFloat())
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
            },
            title = {
                Text(
                    text = appInfo.appName,
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.h6
                )
            },
            text = {
                Column {
                    Text(
                        text = "使用时间: ${Util.longTimeFormat(appInfo.useTime)}",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "启动次数: ${appInfo.launchCount}",
                        fontSize = 16.sp
                    )
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { openDialog = false }
                    ) {
                        Text("关闭")
                    }
                }
            }
        )
    }
    Row {
        Column(modifier = Modifier
            .animateContentSize()
            .clickable { openDialog = true }) {
            Box(
                modifier = Modifier
                    .size(17.dp, (175.0 * percentage).toInt().dp)
                    .clip(RoundedCornerShape(20))
                    .background(color = MaterialTheme.colors.primaryVariant)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                bitmap = appInfo.icon,
                contentDescription = appInfo.appName,
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .requiredSize(17.dp, 17.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
    }
}

@Composable
fun Items(appInfoList: List<AppInfo>, maxUseTime: Long) {
    LazyRow(
        verticalAlignment = Alignment.Bottom, modifier = Modifier.defaultMinSize(minHeight = 196.dp)
    ) {
        items(appInfoList) { appInfo ->
            Item(appInfo, maxUseTime)
        }
    }
}




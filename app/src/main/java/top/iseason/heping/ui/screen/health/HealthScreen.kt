package top.iseason.heping.ui.screen.health

import android.app.AppOpsManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import top.iseason.heping.R
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.EventManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.manager.hasPermission
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.utils.Util

@Composable
fun HealthScreen(viewModel: AppViewModel) {
    val mainColor = MaterialTheme.colors.primaryVariant
    val subColor = MaterialTheme.colors.background
    if (MaterialTheme.colors.isLight)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(mainColor, subColor),
                    start = Offset(size.width / 2, 0F),
                    end = Offset(size.width / 2, size.height)
                ),
            )
        }
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.refresh() },
    ) {
        LazyColumn(modifier = Modifier.padding(all = 16.dp)) {
            item {
                HealthUsageWindow(viewModel = viewModel)
            }
            item {
                MoreAppInfo()
            }
            item {
                var isOpen by remember { mutableStateOf(false) }
                isOpen = ConfigManager.getBoolean("Setting-SleepPlain")
                val time = ConfigManager.getLong("Setting-SleepTime-Yesterday")
                var text = "未设定睡眠计划"
                val timeSet = ConfigManager.getString("Setting-SleepPlain-TimeSet")
                if (timeSet != null) {
                    val split = timeSet.split(',')
                    val fistHour = split[0].toInt()
                    val fistMinute = split[1].toInt()
                    val lastHour = split[2].toInt()
                    val lastMinute = split[3].toInt()
                    text = "${
                        Util.formatTime2(fistHour)
                    }:${
                        Util.formatTime2(fistMinute)
                    }~${
                        Util.formatTime2(lastHour)
                    }:${Util.formatTime2(lastMinute)}"
                }
                text = if (isOpen) text else "未设定睡眠计划"
                MessageCard(
                    title = "睡眠",
                    subTitle = "昨晚睡眠",
                    time = Util.longTimeFormat(time),
                    message = text,
                    drawable = R.drawable.moon,
                    modifier = Modifier
                        .padding(top = 15.dp, end = 23.dp)
                ) {
                    if (hasPermission(AppOpsManager.OPSTR_GET_USAGE_STATS))
                        ModelManager
                            .getNavController()
                            .navigate("HealthSleep") else
                        ModelManager.showToast("你还没有开启权限,无法查看!")
                }
            }
            item {
                val int = ConfigManager.getInt("Health-TiredRecord-Tip")
                val text = if (int == 0) "未启用连续使用提醒" else "${int}分钟提醒一次"
                MessageCard(
                    title = "疲劳记录",
                    subTitle = "目前已连续使用屏幕",
                    time = Util.longTimeFormat(System.currentTimeMillis() - EventManager.tempUnix),
                    message = text,
                    drawable = R.drawable.eyes,
                    modifier = Modifier
                        .padding(top = 7.dp, end = 6.dp)
                ) {
                    ModelManager.getNavController().navigate("healthTired")
                }

            }
//            item {
//                MessageCard(
//                    title = "久坐检测",
//                    subTitle = "今天检测到久坐",
//                    time = "3次",
//                    message = "未启用久坐提醒",
//                    drawable = R.drawable.chair,
//                    modifier = Modifier
//                        .padding(top = 5.dp, end = 25.dp)
//                ) {
//                    ModelManager.showToast("功能未实现")
//                }
//            }
            item { Spacer(modifier = Modifier.padding(top = 64.dp)) }
        }
    }
}

@Composable
fun MoreAppInfo() {
    Surface(
        modifier = Modifier
            .padding(top = 16.dp)
            .height(height = 48.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable {
                if (hasPermission(AppOpsManager.OPSTR_GET_USAGE_STATS))
                    ModelManager
                        .getNavController()
                        .navigate("healthTotal") else
                    ModelManager.showToast("你还没有开启权限,无法查看!")
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = "更多详细数据", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Icon(Icons.Outlined.ArrowForwardIos, null, tint = MaterialTheme.colors.secondary)
        }
    }
}

@Composable
fun MessageCard(
    title: String,
    subTitle: String,
    time: String,
    message: String,
    drawable: Int,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(top = 16.dp)
            .defaultMinSize(minHeight = 156.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = subTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.secondary
                )
                Text(
                    text = time,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.secondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colors.secondaryVariant
                ) {
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 6.dp,
                            bottom = 7.dp
                        )
                    )
                }
            }

            Image(
                painterResource(drawable),
                null,
                modifier = modifier
            )

        }
    }
}


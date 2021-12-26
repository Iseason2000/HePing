package top.iseason.heping.ui.screen.health

import android.app.AppOpsManager
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import top.iseason.heping.R
import top.iseason.heping.manager.AppInfo
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.manager.hasPermission
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.model.getTotalTime
import top.iseason.heping.utils.Util


@Composable
fun UsageWindow(viewModel: AppViewModel) {

    val viewState by viewModel.healthViewState.collectAsState()
    var isOpenSetting by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .animateContentSize()
    ) {
        val appInfo = viewState.appInfo
        if (appInfo.isNotEmpty()) {
            val totalTime = appInfo.getTotalTime()
            val maxUseTime = appInfo[0].getTotalTime()
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            ) {
                Text(
                    text = "今日使用屏幕",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = Util.longTimeFormat(totalTime),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
                val yTotalTime = ConfigManager.getLong("YesterdayUseTime")
                val t = totalTime - yTotalTime

                if (t > 0)
                    Text(
                        text = "较昨日增加${Util.longTimeFormat(t)} ▲",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFFA421C)
                    )
                else
                    Text(
                        text = "较昨日减少${Util.longTimeFormat(-t)} ▼",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF07C192)
                    )
                Items(appInfo, maxUseTime, viewModel)
            }
        } else {
            if (hasPermission(AppOpsManager.OPSTR_GET_USAGE_STATS)) {
                isOpenSetting = false
            }
            if (!isOpenSetting)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 80.dp)
                ) {
                    if (!hasPermission(AppOpsManager.OPSTR_GET_USAGE_STATS))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "你还没有授予权限，将无法显示应用统计!")
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = {
                                isOpenSetting = true
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
                    else Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(50.dp)
                    ) {
                        LaunchedEffect(Unit) {
                            viewModel.updateAppInfo()
                            viewModel.loadPastUsage()
                        }
                        Loading(Modifier.scale(2F), 3F)
                    }
                }
            else Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 80.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "已经打开权限? 点击刷新!")
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        isOpenSetting = false
                        viewModel.updateAppInfo()
                    }) {
                        Text(text = "刷新")
                    }
                }
            }
        }
    }
    if (viewState.selectApp >= 0) {
        val appInfo = viewState.appInfo[viewState.selectApp]
        AlertDialog(
            onDismissRequest = {
                viewModel.setSelectedApp(-1)
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
                        text = "使用时间: ${Util.longTimeFormat(appInfo.getTotalTime())}",
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
                        onClick = { viewModel.setSelectedApp(-1) }
                    ) {
                        Text("关闭")
                    }
                }
            }
        )
    }
}

@Composable
fun Items(appInfoList: List<AppInfo>, maxUseTime: Long, viewModel: AppViewModel) {
    val grayColor = MaterialTheme.colors.onError
    var heightPre by remember { mutableStateOf(0.0F) }
    LaunchedEffect(Unit) { heightPre = 1.0F }
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = Util.longTimeFormat(appInfoList[0].getTotalTime()),
            color = grayColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Normal
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            drawLine(
                color = grayColor,
                start = Offset(0F, 0F),
                end = Offset(size.width, 0F),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5F, 5F), 0F)
            )
        }
    }
    LazyRow(
        verticalAlignment = Alignment.Bottom, modifier = Modifier.defaultMinSize(minHeight = 175.dp)
    ) {
        itemsIndexed(appInfoList) { count, appInfo ->
            run {
                val percentage = (appInfo.getTotalTime().toFloat() / maxUseTime.toFloat())
                Row(verticalAlignment = Alignment.Bottom) {
                    Column(modifier = Modifier
                        .animateContentSize()
                        .clickable {
                            viewModel.setSelectedApp(count)
                        }) {
                        val barColor =
                            if (MaterialTheme.colors.isLight)
                                MaterialTheme.colors.primaryVariant else
                                MaterialTheme.colors.primary
                        if (percentage > 0.01) {
                            Box(
                                modifier = Modifier
                                    .size(17.dp, (150.0 * percentage * heightPre).toInt().dp)
                                    .clip(RoundedCornerShape(20))
                                    .background(color = barColor)
                                    .animateContentSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            bitmap = appInfo.icon,
                            contentDescription = appInfo.appName,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20))
                                .size(17.dp, 17.dp)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .width(15.dp)
                            .padding(bottom = 21.dp)
                    )
                }
            }
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-25).dp)
    ) {
        drawLine(
            color = grayColor,
            start = Offset(0F, 0F),
            end = Offset(size.width, 0F),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5F, 5F), 0F)
        )
    }
}

@Composable
fun Loading(modifier: Modifier, speed: Float = 1F) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        speed = speed,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier
    )
}


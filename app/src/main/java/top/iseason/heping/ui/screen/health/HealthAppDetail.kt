package top.iseason.heping.ui.screen.health

import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.AppInfo
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.controller.AppLimiter
import top.iseason.heping.utils.Util
import java.util.*

@Composable
fun HealthAppDetail(packageName: String) {
    Box(modifier = Modifier.fillMaxSize())
    val appInfoForAllDays = ModelManager.getViewModel().getAppInfoForAllDays(packageName)
    val appName = appInfoForAllDays[0].appName
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colors.primaryVariant)
                    .padding(start = 22.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        ModelManager
                            .getNavController()
                            .popBackStack()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ArrowBackIos, null,
                    tint = Color.White
                )
                Text(
                    text = appName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColorFor(MaterialTheme.colors.primaryVariant)
                )
            }
        },
        backgroundColor = Color(0xFFF3F6F5),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { focusManager.clearFocus() }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
        ) {
            run {
                item { TotalDays(appInfoForAllDays) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    AppLimiter(
                        key = "TimeLimit-$packageName",
                        mainTitle = "应用限额设置",
                        subTitle = "单日使用此应用达到指定时长（分钟）时将提醒您",
                        value1 = 30,
                        value2 = 60,
                        value3 = 90
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun TotalDays(dayList: List<AppInfo>) {
    var selectedDay by remember { mutableStateOf(0) }
    val maxTimeList = mutableListOf<Long>()
    dayList.forEach {
        maxTimeList.add(it.getTotalTime())
    }
    val totalMaxTime = maxTimeList.take(maxTimeList.size - 1).maxOf { it }
    val maxHour = (totalMaxTime / 3600000L).toInt() + 1
    val date = Util.getDate(selectedDay)
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = "${date.get(Calendar.MONTH)}.${date.get(Calendar.DAY_OF_MONTH)}使用了",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                val time1 = maxTimeList[selectedDay]
                Text(
                    text = Util.longTimeFormat(time1),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
                var change = ""
                var isRed = false
                if (selectedDay + 2 <= maxTimeList.size) {
                    val time2 = maxTimeList[selectedDay + 1]
                    val t = time1 - time2
                    change = if (t > 0) "较昨日增加${Util.longTimeFormat(t)} ▲".apply { isRed = true }
                    else "较昨日减少${Util.longTimeFormat(-t)} ▼"
                }
                Text(
                    text = change,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isRed) Color(0xFFFA421C) else Color(0xFF07C192)
                )
                val grayColor = MaterialTheme.colors.onError
                val primaryColor = MaterialTheme.colors.primary
                val secondaryVariant = MaterialTheme.colors.secondaryVariant
                var start by remember { mutableStateOf(false) }
                val heightPre by animateFloatAsState(
                    targetValue = if (start) 1f else 0f,
                    animationSpec = FloatTweenSpec(duration = 1000)
                )
                LaunchedEffect(Unit) {
                    start = true
                }
                Spacer(modifier = Modifier.height(19.dp))
                Canvas(
                    modifier = Modifier
                        .height(99.dp)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    val pointAt =
                                        (it.x / (size.width - 100 * size.height / 150) * 6).toInt()
                                    if (pointAt > 6) return@detectTapGestures
                                    selectedDay = 6 - pointAt
                                }
                            )
                        }
                ) {
                    val height = size.height
                    val rate = height / 150
                    val actWidth = size.width - 80 * rate
                    val width = size.width
                    val pe = PathEffect.dashPathEffect(floatArrayOf(5F, 5F))
                    val textPaint = Paint().asFrameworkPaint().apply {
                        color = grayColor.toArgb()
                        textSize = 11 * rate
                    }
                    drawLine(
                        color = grayColor,
                        start = Offset(0F, height),
                        end = Offset(width, height),
                        pathEffect = pe
                    )
                    drawLine(
                        color = grayColor,
                        start = Offset(0F, height / 2),
                        end = Offset(width, height / 2),
                        pathEffect = pe
                    )
                    drawLine(
                        color = grayColor,
                        start = Offset(0F, 0F),
                        end = Offset(width, 0F),
                        pathEffect = pe
                    )
                    drawIntoCanvas {
                        var max = "${maxHour}小时"
                        var mid = "${(maxHour.toFloat() / 2)}小时"
                        if (maxHour == 1) {
                            max = "1小时"
                            mid = "30分钟"
                        }
                        it.nativeCanvas.drawText(
                            "0分钟",
                            width - 30 * rate,
                            height - 5 * rate,
                            textPaint
                        )
                        it.nativeCanvas.drawText(
                            mid,
                            width - 30 * rate,
                            (height / 2) - 5 * rate,
                            textPaint
                        )
                        it.nativeCanvas.drawText(
                            max,
                            width - 30 * rate,
                            0 - 5 * rate,
                            textPaint
                        )
                        for (index in 0 until 7) {
                            val date1 = Util.getDate(-index)
                            if (index == 0) {
                                it.nativeCanvas.drawText(
                                    "今天",
                                    actWidth - (actWidth / 6 * index),
                                    height + 15 * rate,
                                    textPaint
                                )
                            } else
                                it.nativeCanvas.drawText(
                                    "${date1.get(Calendar.MONTH)}.${date1.get(Calendar.DAY_OF_MONTH)}",
                                    actWidth - (actWidth / 6 * index),
                                    height + 15 * rate,
                                    textPaint
                                )
                        }
                    }
                    for ((index, datum) in maxTimeList.withIndex()) {
                        val h =
                            datum.toFloat() / (maxHour * 3600000L).toFloat() * height * heightPre
                        val w = actWidth / 12
                        drawRoundRect(
                            color = if (selectedDay == index) primaryColor else secondaryVariant,
                            topLeft = Offset(actWidth - w * index * 2, height - h),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(8F)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TotalBar(dayList[selectedDay].useTime)
    }
}
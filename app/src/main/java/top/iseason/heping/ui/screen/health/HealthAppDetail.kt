package top.iseason.heping.ui.screen.health

import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.controller.AppLimiter
import top.iseason.heping.utils.Util
import java.util.*

@Composable
fun HealthAppDetail(packageName: String) {
    val appInfoForAllDays = ModelManager.getViewModel().getAppInfoForAllDays(packageName)
    val appName = appInfoForAllDays[0].appName
    val focusManager = LocalFocusManager.current
    NavBar(appName, modifier = Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        focusManager.clearFocus()
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
        ) {
            item { TotalDays(appInfoForAllDays) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                AppLimiter(
                    key = "TimeLimit-$packageName",
                    mainTitle = "??????????????????",
                    subTitle = "??????????????????????????????????????????????????????????????????",
                    value1 = 30,
                    value2 = 60,
                    value3 = 90
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }

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
    val totalMaxTime = maxTimeList.maxOf { it }
    val maxHour = (totalMaxTime / 3600000L).toInt() + 1
    val date = Util.getDate(-selectedDay)
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
        ) {
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = "${Util.formatTime2(date.get(Calendar.MONTH) + 1)}.${
                        Util.formatTime2(
                            date.get(Calendar.DAY_OF_MONTH)
                        )
                    }?????????",
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
                    change = if (t > 0) "???????????????${Util.longTimeFormat(t)} ???".apply { isRed = true }
                    else "???????????????${Util.longTimeFormat(-t)} ???"
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
                        var max = "${maxHour}??????"
                        var mid = "${(maxHour.toFloat() / 2)}??????"
                        if (maxHour == 1) {
                            max = "1??????"
                            mid = "30??????"
                        }
                        it.nativeCanvas.drawText(
                            "0??????",
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
                                    "??????",
                                    actWidth,
                                    height + 15 * rate,
                                    textPaint
                                )
                            } else
                                it.nativeCanvas.drawText(
                                    "${date1.get(Calendar.MONTH) + 1}.${date1.get(Calendar.DAY_OF_MONTH)}",
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
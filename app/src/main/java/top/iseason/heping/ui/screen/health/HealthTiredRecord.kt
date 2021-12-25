package top.iseason.heping.ui.screen.health

import android.annotation.SuppressLint
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.EventManager
import top.iseason.heping.manager.calculateTime
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.controller.AppLimiter
import top.iseason.heping.utils.Util
import java.util.*

@Composable
fun HealthTiredRecord() {
    NavBar("疲劳记录") {
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            item {
                TiredRecord()
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                AppLimiter(
                    key = "Health-TiredRecord-Tip",
                    mainTitle = "连续使用提醒",
                    subTitle = "连续使用屏幕达到指定时长（分钟）时将提醒您放松眼睛",
                    value1 = 30,
                    value2 = 45,
                    value3 = 60,
                    maxValue = 120
                )
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TiredRecord() {
    var useTime by remember { mutableStateOf(Array<Long>(12) { 0 }) }
    var selected by remember { mutableStateOf(0) }
    var eventList by remember { mutableStateOf(mutableListOf<Pair<Long, Long>>()) }
    LaunchedEffect(Unit) {
        eventList = EventManager.eventList
        val array = Array<Long>(12) { 0 }
        for (i in 0 until eventList.size) {
            array[i] = eventList[i].calculateTime()
        }
        useTime = array
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        if (eventList.isNotEmpty())
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = "当前已连续使用",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = Util.longTimeFormat(System.currentTimeMillis() - EventManager.tempUnix),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
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
                                        (it.x / (size.width - 100 * size.height / 150) * 12).toInt()
                                    if (pointAt > 12) return@detectTapGestures
                                    selected = pointAt
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
                        start = Offset(0F, 0F),
                        end = Offset(width, 0F),
                        pathEffect = pe
                    )
                    drawIntoCanvas {
                        it.nativeCanvas.drawText(
                            "0分钟",
                            width - 30 * rate,
                            height - 5 * rate,
                            textPaint
                        )
                        val instance = Calendar.getInstance()
                        instance.timeInMillis = eventList[0].first
                        val first = "${Util.formatTime2(instance.get(Calendar.HOUR_OF_DAY))}:${
                            Util.formatTime2(instance.get(Calendar.MINUTE))
                        }"
                        instance.timeInMillis = eventList[0].second
                        val second = "${Util.formatTime2(instance.get(Calendar.HOUR_OF_DAY))}:${
                            Util.formatTime2(instance.get(Calendar.MINUTE))
                        }"
                        it.nativeCanvas.drawText(
                            "${first}~${second} ${Util.longTimeFormat(useTime[0])}",
                            width - 90 * rate,
                            0 - 5 * rate,
                            textPaint
                        )
                    }
                    val max = useTime[0]
                    for ((index, datum) in useTime.withIndex()) {
                        val h =
                            datum.toFloat() / max.toFloat() * height * heightPre
                        val w = actWidth / 24
                        drawRoundRect(
                            color = if (selected == index) primaryColor else secondaryVariant,
                            topLeft = Offset(w * index * 2, height - h),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(8F)
                        )
                    }
                }
            }
        else Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(200.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "目前没有记录数据，请使用一段时间再看看",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Loading(
                    Modifier
                        .scale(2F)
                        .size(50.dp), 1F
                )
            }
        }
    }
}
package top.iseason.heping.ui.screen.health

import android.annotation.SuppressLint
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collect
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.controller.TimePicker
import top.iseason.heping.utils.Util
import java.util.*
import kotlin.math.abs
import kotlin.math.floor

@Composable
fun HealthSleep() {
    NavBar("睡眠") {
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            item {
                SleepTime()
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item { TimePicker() }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition", "MutableCollectionMutableState")
@Composable
fun SleepTime() {
    var sleepTimeForDays by remember { mutableStateOf(mutableListOf<SleepTime>()) }
    val pastUsage = ModelManager.getViewModel().getPastUsage().collectAsState()
    var totalStartHour by remember { mutableStateOf(0) }
    var totalEndHour by remember { mutableStateOf(0) }
    var sizeT by remember { mutableStateOf(0) }
    val topHour = floor(totalStartHour.toFloat() / sizeT).toInt() + 1
    val endHour = floor(totalEndHour.toFloat() / sizeT).toInt()
    var selectedDay by remember { mutableStateOf(0) }
    var isWakeUp by remember { mutableStateOf(false) }
    val ofDay = if (isWakeUp) 0 else -1
    val date = Util.getDate(-selectedDay + ofDay)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        DisposableEffect(Unit) {
            onDispose {
                ConfigManager.setLong(
                    "Setting-SleepTime-Yesterday",
                    sleepTimeForDays[0].getUsedTime()
                )
            }
        }
        if (sleepTimeForDays.isNotEmpty())
            Column(modifier = Modifier.padding(all = 16.dp)) {
                val selectedDays = sleepTimeForDays[selectedDay]

                val title =
                    "${Util.formatTime2(date.get(Calendar.MONTH) + 1)}.${
                        Util.formatTime2(
                            date.get(
                                Calendar.DAY_OF_MONTH
                            )
                        )
                    }睡眠 " +
                            "${Util.formatTime2(selectedDays.first.hour)}:${
                                Util.formatTime2(
                                    selectedDays.first.minutes
                                )
                            }~" +
                            "${Util.formatTime2(selectedDays.second.hour)}:${
                                Util.formatTime2(
                                    selectedDays.second.minutes
                                )
                            }"
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                val time1 = selectedDays.getUsedTime()
                Text(
                    text = Util.longTimeFormat(time1),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
                var change = ""
                var isRed = false
                if (selectedDay + 2 <= sleepTimeForDays.size) {
                    val time2 = sleepTimeForDays[selectedDay + 1].getUsedTime()
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
                Spacer(modifier = Modifier.height(33.dp))
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
                        start = Offset(0F, 0F),
                        end = Offset(width, 0F),
                        pathEffect = pe
                    )
                    drawIntoCanvas {
                        val top = "${Util.toHour(topHour)}:00"
                        val end = "${Util.toHour(endHour)}:00"
                        it.nativeCanvas.drawText(
                            top,
                            width - 30 * rate,
                            height - 5 * rate,
                            textPaint
                        )
                        it.nativeCanvas.drawText(
                            end,
                            width - 30 * rate,
                            0 - 5 * rate,
                            textPaint
                        )
                        for (index in 0 until 7) {
                            val date1 = Util.getDate(-index + ofDay)
                            if (index == 0) {
                                it.nativeCanvas.drawText(
                                    "昨晚",
                                    actWidth - (actWidth / 6 * index),
                                    height + 40 * rate,
                                    textPaint
                                )
                            } else
                                it.nativeCanvas.drawText(
                                    "${Util.formatTime2(date1.get(Calendar.MONTH) + 1)}.${
                                        Util.formatTime2(
                                            date1.get(Calendar.DAY_OF_MONTH)
                                        )
                                    }",
                                    actWidth - (actWidth / 6 * index),
                                    height + 40 * rate,
                                    textPaint
                                )
                        }
                    }
                    for ((index, dayS) in sleepTimeForDays.withIndex()) {
                        val timeLength = (abs(endHour - topHour) * 3600000L).toFloat()
                        val h = dayS.getUsedTime().toFloat() / timeLength * height * heightPre
                        val w = actWidth / 12
                        var usedTime: Long
                        val copy = dayS.first.copy(hour = topHour, minutes = 0)
                        usedTime = if (dayS.first.isBefore(copy)) {
                            -Pair(dayS.first, copy).getUsedTime()
                        } else {
                            Pair(copy, dayS.first).getUsedTime()
                        }
                        val offset = usedTime / timeLength * height
                        drawRoundRect(
                            color = if (selectedDay == index) primaryColor else secondaryVariant,
                            topLeft = Offset(actWidth - w * index * 2, height - h - offset),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(8F)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        else
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(200.dp)
            ) {
                LaunchedEffect(pastUsage) {
                    snapshotFlow { pastUsage.value }.collect {
                        if (!ModelManager.getViewModel().isInit) return@collect
                        val now = Calendar.getInstance()
                        val sleepTime = arrayOf(0, 0, 7, 0) //睡觉小时,睡觉分钟,起床小时,起床分钟
                        val timeSet = ConfigManager.getString("Setting-SleepPlain-TimeSet")
                        if (timeSet != null) {
                            for ((i, s) in timeSet.split(',').withIndex()) {
                                sleepTime[i] = s.toInt()
                            }
                        }
                        if (now.get(Calendar.HOUR_OF_DAY) >= sleepTime[2] && now.get(Calendar.MINUTE) >= sleepTime[3]) {
                            isWakeUp = true
                        }
                        val pastUsage2 = pastUsage.value.toMutableList()
                        if (isWakeUp) {
                            val appInfo = ModelManager.getViewModel().healthViewState.value.appInfo
                            pastUsage2.add(0, appInfo)
                        }
                        //多天内的使用时间分布情况
                        val useTimeList =
                            mutableListOf<Pair<Array<Int>, Long>>() //Array 天+小时 ，Long 使用时间
                        for (index in pastUsage2.size - 1 downTo 0) {
                            val totalD = Array(24) { 0L }.clone()
                            pastUsage2[index].forEach {
                                for (l in totalD.indices) {
                                    totalD[l] += it.useTime[l]
                                }
                            }
                            totalD.forEachIndexed { i, l ->
                                useTimeList.add(Pair(arrayOf(index, i), l))
                            }
                        }
                        val sleepEventList = mutableListOf<SleepEvent>()
                        //时间由远到近，小时从0到23
                        for ((index, pair) in useTimeList.withIndex()) {
                            if (index + 1 == useTimeList.size) break
                            val day = pair.first[0]
                            val hour = pair.first[1]
                            val time = pair.second
                            //一分钟阈值
                            val threshold = 120000L
                            if (time > threshold && useTimeList[index + 1].second <= threshold) {
                                //开始休息
                                sleepEventList.add(
                                    SleepEvent(
                                        day,
                                        hour,
                                        (time.toInt() / 60000),
                                        true
                                    )
                                )
                                continue
                            }
                            val next = useTimeList[index + 1]
                            val timeNext = next.second
                            if (threshold in time..timeNext) {
                                //开始使用
                                val i = timeNext.toInt() / 60000
                                sleepEventList.add(
                                    SleepEvent(
                                        next.first[0],
                                        next.first[1],
                                        if (i > 30) 60 - i else i,
                                        false
                                    )
                                )
                            }
                        }
                        val sleepTimeForDay = mutableListOf<SleepTime>()
                        for (day in 0 until pastUsage.value.size) {
                            val today = sleepEventList.filter { it.day in day..day + 1 }
                            val temp = mutableListOf<Pair<SleepEvent, SleepEvent>>()
                            for ((index, event) in today.withIndex()) {
                                if (index == 0) continue
                                if (event.isSleep || event.day != day) continue
                                //今日的起床事件
                                temp.add(Pair(today[index - 1], event))
                            }
                            //计算休息时间最长的时间
                            var todayWakeUp: Pair<SleepEvent, SleepEvent> = temp[0]
                            for (sleepEvent in temp) {
                                //3点前起床的算
                                if (sleepEvent.second.hour < 15)
                                    if (sleepEvent.getUsedTime() > todayWakeUp.getUsedTime())
                                        todayWakeUp = sleepEvent
                            }
                            sleepTimeForDay.add(todayWakeUp)
                        }
                        sleepTimeForDays = sleepTimeForDay
                        for (sleepTime2 in sleepTimeForDays) {
                            totalStartHour += if (sleepTime2.first.hour >= 12) {
                                (24 - sleepTime2.first.hour)
                            } else
                                sleepTime2.first.hour
                            totalEndHour += sleepTime2.second.hour
                        }
                        sizeT = sleepTimeForDays.size
                    }
                }
                Loading(Modifier.scale(0.5F), 3F)
            }
    }
}
typealias SleepTime = Pair<SleepEvent, SleepEvent>

fun SleepTime.getUsedTime(): Long {
    val sleep = this.first
    val wakeUp = this.second
    var time = 0L
    time += if (sleep.day == wakeUp.day) {
        (wakeUp.hour - sleep.hour) * 3600000L
    } else {
        (wakeUp.hour + 24 - sleep.hour) * 3600000L
    }
    if (wakeUp.minutes >= sleep.minutes) {
        time += (wakeUp.minutes - sleep.minutes) * 60000L
    } else {
        time += (wakeUp.minutes + 60 - sleep.minutes) * 60000L
        time -= 3600000L
    }
    return time
}

data class SleepEvent(
    val day: Int,
    val hour: Int,
    val minutes: Int,
    val isSleep: Boolean
)

fun SleepEvent.isBefore(other: SleepEvent): Boolean {
    if (this.day > other.day) {
        return true
    }
    if (this.day < other.day) return false
    if (this.hour < other.hour) return true
    if (this.minutes < other.minutes) return true
    return false
}
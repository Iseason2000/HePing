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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.controller.TimePicker
import top.iseason.heping.utils.Util
import java.util.*
import kotlin.math.abs

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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SleepTime() {
    val now = Calendar.getInstance()
    val sleepTime = arrayOf(0, 0, 7, 0) //睡觉小时,睡觉分钟,起床小时,起床分钟
    val timeSet = ConfigManager.getString("Setting-SleepPlain-TimeSet")
    if (timeSet != null) {
        for ((i, s) in timeSet.split(',').withIndex()) {
            sleepTime[i] = s.toInt()
        }
    }
    var isWakeUp = false
    if (now.get(Calendar.HOUR_OF_DAY) > sleepTime[2] && now.get(Calendar.MINUTE) > sleepTime[3]) {
        isWakeUp = true
    }
    val pastUsage = ModelManager.getViewModel().getPastUsage().toMutableList()
    if (isWakeUp) {
        val appInfo = ModelManager.getViewModel().viewState.value.appInfo
        pastUsage.add(0, appInfo)
    }
    //多天内的使用时间分布情况
    val useTimeList = mutableListOf<Pair<Array<Int>, Long>>() //Array 天+小时 ，Long 使用时间
    for (index in pastUsage.size - 1 downTo 0) {
        val totalD = Array(24) { 0L }.clone()
        pastUsage[index].forEach {
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
        if (time > 0 && useTimeList[index + 1].second == 0L) {
            //开始休息
            sleepEventList.add(SleepEvent(day, hour, ((3600000 - time.toInt()) / 60000), true))
            continue
        }
        val next = useTimeList[index + 1]
        val timeNext = next.second
        if (time == 0L && timeNext > 0) {
            //开始使用
            sleepEventList.add(
                SleepEvent(
                    next.first[0],
                    next.first[1],
                    (timeNext.toInt() / 60000),
                    false
                )
            )
        }
    }
    val sleepTimeForDays: MutableList<SleepTime> = mutableListOf()
    for (day in 0 until pastUsage.size) {
        val today = sleepEventList.filter { it.day in day..day + 1 }
        val temp = mutableListOf<SleepEvent>()
        for (event in today) {
            if (event.isSleep || event.day != day) continue
            //最近起床事件
            temp.add(event)
        }
        //计算今天离凌晨最近的起床事件
        var todayWakeUp: SleepEvent = temp[0]
        for (sleepEvent in temp)
            if (sleepEvent.hour < todayWakeUp.hour) todayWakeUp = sleepEvent
        val todaySleep = today[today.indexOf(todayWakeUp) - 1]
        sleepTimeForDays.add(Pair(todaySleep, todayWakeUp))
    }
    var totalStartHour = 0
    var totalEndHour = 0
    for (sleepTimeForDay in sleepTimeForDays) {
        totalStartHour += if (sleepTimeForDay.first.hour >= 12) {
            (24 - sleepTimeForDay.first.hour)
        } else
            sleepTimeForDay.first.hour
        totalEndHour += sleepTimeForDay.second.hour
    }
    val sizeT = sleepTimeForDays.size
    val topHour = (totalStartHour.toFloat() / sizeT).toInt()
    val endHour = (totalEndHour.toFloat() / sizeT).toInt()
    var selectedDay by remember { mutableStateOf(0) }
    val date = Util.getDate(selectedDay)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        DisposableEffect(Unit) {
            onDispose {
                ConfigManager.setLong(
                    "Setting-SleepTime-Yesterday",
                    sleepTimeForDays[0].getUsedTime()
                )
            }
        }
        Column(modifier = Modifier.padding(all = 16.dp)) {
            val selectedDays = sleepTimeForDays[selectedDay]
            val title = "${date.get(Calendar.MONTH)}.${date.get(Calendar.DAY_OF_MONTH)}睡眠 " +
                    "${Util.formatTime2(selectedDays.first.hour)}:${Util.formatTime2(selectedDays.first.minutes)}~" +
                    "${Util.formatTime2(selectedDays.second.hour)}:${Util.formatTime2(selectedDays.second.minutes)}"
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
                        val date1 = Util.getDate(-index)
                        if (index == 0) {
                            it.nativeCanvas.drawText(
                                "昨晚",
                                actWidth - (actWidth / 6 * index),
                                height + 40 * rate,
                                textPaint
                            )
                        } else
                            it.nativeCanvas.drawText(
                                "${date1.get(Calendar.MONTH)}.${date1.get(Calendar.DAY_OF_MONTH)}",
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
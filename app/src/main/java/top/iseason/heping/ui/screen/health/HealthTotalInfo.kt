package top.iseason.heping.ui.screen.health

import android.widget.Toast
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.AppInfo
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.utils.Util

@Composable
fun HealthTotalInfo() {
    val viewModel = ModelManager.getViewModel()
    val viewState by viewModel.viewState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.updateAppInfo()
    }
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
                    text = "详细使用情况",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColorFor(MaterialTheme.colors.primaryVariant)
                )
            }
        },
        backgroundColor = Color(0xFFF3F6F5)
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            val array = Array(24) { 0L }
            for (appInfo in viewState.appInfo) {
                for ((i, l) in appInfo.useTime.withIndex()) {
                    array[i] += l
                }
            }
            TotalBar(array)
            TotalInfo(viewState.appInfo)
        }
    }
}

@Composable
fun TotalBar(data: Array<Long> = Array(24) { 0L }) {
    val data12 = Array(12) { 0L }
    for ((i, l) in data.withIndex()) {
        data12[i / 2] += l
    }
    val maxTime = data12.maxOf { it }
    if (maxTime == 0L) {
        return
    }
    val maxHour = (maxTime / 3600000L).toInt() + 1
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(text = "使用时段", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            val grayColor = MaterialTheme.colors.onError
            val primaryColor = MaterialTheme.colors.primary
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
                                val pointAt = (it.x / (size.width - 100) * 12).toInt()
                                val l = data12[pointAt]
                                if (l == 0L) return@detectTapGestures
                                Toast
                                    .makeText(
                                        ModelManager.getMainActivity(),
                                        "${pointAt * 2}点 - ${pointAt * 2 + 2}点 \n${
                                            Util.longTimeFormatDetail(l)
                                        }",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        )
                    }
            ) {
                val height = size.height
                val rate = height / 150
                val actWidth = size.width - 30 * rate
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
                    var mid = "${maxHour / 2}小时"
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
                    for ((i, time) in arrayOf("00:00", "06:00", "12:00", "18:00").withIndex()) {
                        it.nativeCanvas.drawText(
                            time,
                            actWidth / 4 * i - 5 * rate,
                            height + 15 * rate,
                            textPaint
                        )
                        it.nativeCanvas.drawLine(
                            actWidth / 4 * i,
                            height,
                            actWidth / 4 * i,
                            height - 5 * rate,
                            textPaint
                        )
                    }
                }
                for ((index, datum) in data12.withIndex()) {
                    val h = datum.toFloat() / (maxHour * 3600000L).toFloat() * height * heightPre
                    val w = actWidth / 24
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(0 + w * index * 2, height - h),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(8F)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun TotalInfo(appInfo: List<AppInfo>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(8.dp))

    ) {
        Column {
            Text(
                text = "应用使用情况",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(all = 16.dp)
            )
            var start by remember { mutableStateOf(false) }
            val heightPre by animateFloatAsState(
                targetValue = if (start) 1f else 0f,
                animationSpec = FloatTweenSpec(duration = 1000)
            )
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                if (appInfo.isEmpty()) return@LazyColumn
                val maxTime = appInfo[0].getTotalTime()
                items(appInfo) { item ->
                    run {
                        LaunchedEffect(Unit) {
                            start = true
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .clickable {
                                    ModelManager
                                        .getNavController()
                                        .navigate("healthAppDetail/${item.packageName}")
                                }
                        ) {
                            Image(
                                bitmap = item.icon,
                                contentDescription = item.packageName,
                                modifier = Modifier.size(36.dp)
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = item.appName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Text(
                                        text = Util.longTimeFormat(item.getTotalTime()),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colors.onError
                                    )
                                }
                                val colorA = MaterialTheme.colors.primary
                                val colorB = MaterialTheme.colors.secondaryVariant
                                val cornerRadius = CornerRadius(8F)

                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                ) {
                                    drawRoundRect(colorB, size = size, cornerRadius = cornerRadius)
                                    drawRoundRect(
                                        colorA,
                                        size = Size(
                                            size.width * (item.getTotalTime()
                                                .toFloat() * heightPre / maxTime),
                                            size.height
                                        ),
                                        cornerRadius = cornerRadius
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
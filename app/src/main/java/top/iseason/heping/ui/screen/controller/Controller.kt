package top.iseason.heping.ui.screen.controller

import android.app.AppOpsManager
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.manager.hasPermission
import top.iseason.heping.ui.theme.GreenSurface
import top.iseason.heping.utils.Util
import kotlin.math.roundToInt


@Composable
fun AppLimiter(
    key: String,
    mainTitle: String,
    subTitle: String,
    value1: Int,
    value2: Int,
    value3: Int,
    maxValue: Int = 150
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        var limitTime by remember { mutableStateOf(0) }
        var offsetX by remember { mutableStateOf(0F) }
        LaunchedEffect(Unit) {
            if (ConfigManager.hasKey(key)) {
                limitTime = ConfigManager.getInt(key)
                offsetX = limitTime.toFloat() / maxValue * 220
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                ConfigManager.setInt(key, limitTime)
            }
        }
        if (hasPermission(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW))
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Text(
                    text = mainTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subTitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    limitTime = (offsetX / 220 * maxValue).toInt()
                    CountButton("关闭", limitTime, 0) {
                        limitTime = 0
                        offsetX = 0F
                    }
                    CountButton(value1.toString(), limitTime, value1) {
                        limitTime = value1
                        offsetX = limitTime.toFloat() / maxValue * 220
                    }
                    CountButton(value2.toString(), limitTime, value2) {
                        limitTime = value2
                        offsetX = limitTime.toFloat() / maxValue * 220
                    }
                    CountButton(value3.toString(), limitTime, value3) {
                        limitTime = value3
                        offsetX = limitTime.toFloat() / maxValue * 220
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(220.dp, 48.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colors.secondaryVariant)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { offset ->
                                            val fl = (offset.x / size.width) * 220
                                            offsetX = fl
                                        }
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures(
                                        onDragStart = { offset ->
                                            val fl = (offset.x / size.width) * 220
                                            offsetX = fl
                                        },
                                        onHorizontalDrag = { _: PointerInputChange, dragAmount: Float ->
                                            val fl = (dragAmount / size.width) * 220
                                            if (offsetX + fl < 0 || offsetX + fl > 220) return@detectHorizontalDragGestures
                                            offsetX += fl
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (limitTime == 0) {
                                Text(
                                    text = "拖动以设置时长",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colors.secondary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colors.primary)
                                .animateContentSize()
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .size(
                                        if (offsetX > 220) 220.dp else offsetX.roundToInt().dp,
                                        48.dp
                                    )
                            )
                        }
                    }
                    var text by remember { mutableStateOf("") }
                    var isEdit by remember { mutableStateOf(false) }
                    val focusManager = LocalFocusManager.current
                    if (!isEdit) {
                        text = if (limitTime == 0) "∞" else limitTime.toString()
                    }
                    BasicTextField(
                        value = text,
                        onValueChange = {
                            if (it.length > text.length) {
                                val addedChar = it.replaceFirst(text, "").toIntOrNull()
                                if (addedChar != null) text = it
                            } else
                                text = it
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = {
                            limitTime = text.toIntOrNull() ?: 0
                            focusManager.clearFocus()
                        }),
                        cursorBrush = SolidColor(MaterialTheme.colors.primary),
                        textStyle = TextStyle(
                            color = MaterialTheme.colors.secondary,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        ),
                        decorationBox = @Composable { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .size(68.dp, 48.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colors.onSecondary)
                            .onFocusChanged {
                                if (it.isFocused) text = "" else
                                    limitTime = text.toIntOrNull() ?: 0
                                isEdit = it.isFocused
                                offsetX = limitTime.toFloat() / maxValue * 220
                            }
                    )
                }
            }
        else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(30.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "你还没有授予悬浮窗权限，将无法启用限额功能!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        ModelManager.showToast("在设置里找到 和屏 然后开启权限!")
                        ModelManager.getMainActivity()
                            .startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                    }) {
                        Text(text = "去授予")
                    }
                }
            }
        }
    }
}

@Composable
fun CountButton(
    text: String,
    limitTime: Int,
    num: Int,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = if (limitTime == num) MaterialTheme.colors.primary
            else GreenSurface
        ),
        modifier = Modifier.size(68.dp, 48.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = if (limitTime == num) Color.White
            else MaterialTheme.colors.primary
        )
    }
}

@Composable
fun TimePicker() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        var isOpen by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isOpen = ConfigManager.getBoolean("Setting-SleepPlain")
        }
        DisposableEffect(Unit) {
            onDispose { ConfigManager.setBoolean("Setting-SleepPlain", isOpen) }
        }
        if (hasPermission(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW))
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "睡眠计划",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "在计划的睡眠时段内使用屏幕将提醒您",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onError
                        )
                    }
                    Switch(
                        checked = isOpen,
                        onCheckedChange = {
                            isOpen = it
                        },
                        modifier = Modifier.size(46.dp, 24.dp),
                        enabled = true,
                        colors = SwitchDefaults.colors(
                            uncheckedThumbColor = MaterialTheme.colors.primary,
                            uncheckedTrackColor = MaterialTheme.colors.secondaryVariant,
                            checkedThumbColor = MaterialTheme.colors.secondaryVariant,
                            checkedTrackColor = MaterialTheme.colors.primary,
                            checkedTrackAlpha = 1F,
                            uncheckedTrackAlpha = 1F
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawLine(
                                    Color(0xFFF3F6F5),
                                    start = Offset(0F, size.height / 3),
                                    end = Offset(size.width, size.height / 3)
                                )
                                drawLine(
                                    Color(0xFFF3F6F5),
                                    start = Offset(0F, size.height / 4 * 3),
                                    end = Offset(size.width, size.height / 4 * 3)
                                )
                            }
                    ) {
                        var fistHour by remember { mutableStateOf(0) }
                        var fistMinute by remember { mutableStateOf(0) }
                        var lastHour by remember { mutableStateOf(7) }
                        var lastMinute by remember { mutableStateOf(0) }
                        var isInit by remember { mutableStateOf(false) }
                        val timeSet = ConfigManager.getString("Setting-SleepPlain-TimeSet")
                        if (!isInit && timeSet != null) {
                            val split = timeSet.split(',')
                            fistHour = split[0].toInt()
                            fistMinute = split[1].toInt()
                            lastHour = split[2].toInt()
                            lastMinute = split[3].toInt()
                            isInit = true
                        }
                        DisposableEffect(Unit) {
                            onDispose {
                                ConfigManager.setString(
                                    "Setting-SleepPlain-TimeSet",
                                    "$fistHour,$fistMinute,$lastHour,$lastMinute"
                                )
                            }
                        }
                        TimeScrollerPart(
                            defaultValue = fistHour,
                            maxValue = 24,
                            isOpen = isOpen,
                            onValueChange = { fistHour = it },
                            timeFormatter = Util::toHour
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ":",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOpen) MaterialTheme.colors.primary else Color(0XFFD9D9D9),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TimeScrollerPart(
                            defaultValue = fistMinute,
                            maxValue = 60,
                            isOpen = isOpen,
                            onValueChange = { fistMinute = it },
                            timeFormatter = Util::toMinute
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        Text(
                            text = "至",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOpen) MaterialTheme.colors.primary else Color(0XFFD9D9D9),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(y = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        TimeScrollerPart(
                            defaultValue = lastHour,
                            maxValue = 24,
                            isOpen = isOpen,
                            onValueChange = { lastHour = it },
                            timeFormatter = Util::toHour
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ":",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOpen) MaterialTheme.colors.primary else Color(0XFFD9D9D9),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TimeScrollerPart(
                            defaultValue = lastMinute,
                            maxValue = 60,
                            isOpen = isOpen,
                            onValueChange = { lastMinute = it },
                            timeFormatter = Util::toMinute
                        )
                    }
                }
            }
        else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(30.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "你还没有授予悬浮窗权限，将无法启用限额功能!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        ModelManager.openSuspendedWindowPermission()
                    }) {
                        Text(text = "去授予")
                    }
                }
            }
        }
    }
}

@Composable
fun TimeScrollerPart(
    defaultValue: Int = 0,
    maxValue: Int,
    isOpen: Boolean,
    onValueChange: (Int) -> Unit,
    timeFormatter: (Int) -> Int
) {
    val coroutineScope = rememberCoroutineScope()
    var target by remember { mutableStateOf(defaultValue) }
    onValueChange(target)
    val offset: Int = if (target > maxValue / 2) {
        target - 1
    } else {
        maxValue - 1 + target
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = offset)
    var isDragging = false
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect {
                if (isDragging) return@collect
                isDragging = true
                coroutineScope.launch {
                    delay(300L)
                    isDragging = false
                    listState.animateScrollToItem(
                        listState.firstVisibleItemIndex,
                        0
                    )
                }
            }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect {
            val i = maxValue - 1 - it
            target = if (i > 0) maxValue - i else -i
        }
    }
    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(30.dp, 81.dp)
            .disabledVerticalPointerInputScroll(!isOpen)
    ) {
        item { TimeTextScroller(0, target, isOpen, timeFormatter) }
        items(maxValue * 2 - 1) { index ->
            val i = maxValue - 1 - index
            TimeTextScroller(if (i > 0) maxValue - i else -i, target, isOpen, timeFormatter)
        }
        item { TimeTextScroller(0, target, isOpen, timeFormatter) }
    }
}

@Composable
fun TimeTextScroller(
    time: Int,
    currentTime: Int,
    isOn: Boolean,
    timeFormatter: (Int) -> Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(30.dp, 29.dp), contentAlignment = Alignment.Center
    ) {
        if (time == currentTime)
            TimeText(time, isOn, timeFormatter)
        else LightText(time, isOn, timeFormatter)
    }
}

@Composable
fun LightText(time: Int, isOn: Boolean, timeFormatter: (Int) -> Int) {
    Text(
        text = Util.formatTime2(timeFormatter(time)),
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = if (isOn) MaterialTheme.colors.secondaryVariant else Color(0XFFF3F6F5)
    )
}

@Composable
fun TimeText(time: Int, isOn: Boolean, timeFormatter: (Int) -> Int) {
    Text(
        text = Util.formatTime2(timeFormatter(time)),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = if (isOn) MaterialTheme.colors.primary else Color(0XFFD9D9D9)
    )
}

@Composable
fun ScrollerPicker(max: Int, default: Int, offset: Int, onValueChange: ((Int) -> Unit)) {
    var count by remember { mutableStateOf(default) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = default - 1,
        initialFirstVisibleItemScrollOffset = offset
    )
    var isDragging = false
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect {
                if (isDragging) return@collect
                isDragging = true
                coroutineScope.launch {
                    delay(300L)
                    isDragging = false
                    listState.animateScrollToItem(
                        listState.firstVisibleItemIndex,
                        offset
                    )
                }
            }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect {
            count = it + 1
            onValueChange(count)
        }
    }
    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.height(80.dp)
    ) {
        item { Spacer(modifier = Modifier.height(28.dp)) }
        items(max) { index ->
            Box(
                modifier = Modifier.size(30.dp, 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (index + 1).toString(),
                    fontSize = if (count == index + 1) 20.sp else 14.sp,
                    fontWeight = if (count == index + 1) FontWeight.Bold else FontWeight.Normal,
                    color = if (count == index + 1) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
    }
}

@Composable
fun ExitDialog(
    title: String,
    tip: String,
    isVisual: (Unit) -> Boolean,
    onConfirm: (Boolean) -> Unit
) {
    var state by remember { mutableStateOf(false) }
    state = isVisual(Unit)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = state, enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 300)
            ), exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = tip,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(122.dp, 48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(color = MaterialTheme.colors.secondaryVariant)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onConfirm(true)
                                }
                        ) {
                            Text(
                                text = "确认退出", fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.secondary
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(122.dp, 48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(color = MaterialTheme.colors.secondary)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onConfirm(false)
                                }
                        ) {
                            Text(
                                text = "继续坚持", fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

            }
        }
    }
}

private val VerticalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(x = 0f)
    override suspend fun onPreFling(available: Velocity) = available.copy(x = 0f)
}

private val HorizontalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(y = 0f)
    override suspend fun onPreFling(available: Velocity) = available.copy(y = 0f)
}

fun Modifier.disabledVerticalPointerInputScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(VerticalScrollConsumer) else this

fun Modifier.disabledHorizontalPointerInputScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(HorizontalScrollConsumer) else this
package top.iseason.heping.ui.screen.controller

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.ConfigManager
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
            .clip(RoundedCornerShape(8.dp))
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
                println(limitTime)
                ConfigManager.setInt(key, limitTime)
            }
        }
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
                    offsetX = limitTime.toFloat() / maxValue * 220
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
                            .clip(RoundedCornerShape(8.dp))
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colors.primary)
                            .animateContentSize()
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(if (offsetX > 220) 220.dp else offsetX.roundToInt().dp, 48.dp)
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
                        .clip(RoundedCornerShape(8.dp))
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
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = if (limitTime == num) MaterialTheme.colors.primary
            else Color(0xFFF3F6F5)
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
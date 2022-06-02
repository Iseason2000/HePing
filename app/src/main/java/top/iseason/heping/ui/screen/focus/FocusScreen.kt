package top.iseason.heping.ui.screen.focus

import android.app.AppOpsManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.R
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.manager.hasPermission
import top.iseason.heping.ui.screen.controller.ScrollerPicker

@Composable
fun FocusScreen() {
    val focusManager = LocalFocusManager.current
    val mainColor = MaterialTheme.colors.primaryVariant
    val subColor = MaterialTheme.colors.background
    if (MaterialTheme.colors.isLight)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(mainColor, subColor),
                    start = Offset(size.width / 2, 0F),
                    end = Offset(size.width / 2, size.height)
                )
            )
        }
    LazyColumn(modifier = Modifier
        .padding(all = 16.dp)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            focusManager.clearFocus()
        }) {
        item { QuickFocus() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TomatoCard(Modifier.weight(0.45F, true))
                Spacer(modifier = Modifier.width(16.dp))
                FocusSetting(Modifier.weight(0.45F, true))
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            AboutTomato()
        }
    }
}

@Composable
fun AboutTomato() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable {
                ModelManager
                    .getMainActivity()
                    .startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data =
                            Uri.parse("https://baike.baidu.com/item/%E7%95%AA%E8%8C%84%E5%B7%A5%E4%BD%9C%E6%B3%95/6353502")
                    })
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {

            Column {
                Text(
                    text = "了解“番茄工作法”",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "一种简单易行的时间管理方法",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onError
                )
            }
            Image(
                painter = painterResource(id = R.drawable.tomato_circle),
                contentDescription = null,
                modifier = Modifier
                    .size(54.dp, 35.dp)
            )
        }
    }
}

@Composable
fun FocusSetting(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .defaultMinSize(minWidth = 156.dp)
            .height(88.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable {
                ModelManager
                    .getNavController()
                    .navigate("focusSetting")
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {

            Column {
                Text(
                    text = "专注设置",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "自定义专注功能",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onError
                )
            }
            Image(
                painter = painterResource(id = R.drawable.hourglass_bottom_black),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp, 32.dp)
            )
        }
    }
}


@Composable
fun TomatoCard(modifier: Modifier = Modifier) {
    var isEditing by remember { mutableStateOf(false) }
    var count by remember { mutableStateOf(1) }
    Surface(
        modifier = modifier
            .defaultMinSize(minWidth = 156.dp)
            .height(88.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable {
                isEditing = !isEditing
            }
    ) {
        LaunchedEffect(Unit) {
            val int = ConfigManager.getInt("Tired-Tomato-count")
            if (int != 0) count = int
        }
        LaunchedEffect(count) {
            ConfigManager.setInt("Tired-Tomato-count", count)
        }
        if (!isEditing)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)

            ) {
                Column {
                    Text(
                        text = "番茄循环",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "选择循环次数",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colors.onError
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.tomato),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp, 35.dp)
                )
            }
        else
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {

                Text(
                    text = "循环",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                ScrollerPicker(10, count, 20, onValueChange = { count = it })
                Text(
                    text = "次",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    onClick = {
                        val tomatoCircle =
                            ModelManager.getService()?.tomatoCircle ?: return@TextButton
                        val int = ConfigManager.getInt("Focus-Setting-Tomato-FocusTime")
                        if (int == 0) return@TextButton
                        val int1 = ConfigManager.getInt("Focus-Setting-Tomato-ReleaseTime")
                        if (int1 == 0) return@TextButton
                        if (hasPermission(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW)) {
                            if (!tomatoCircle.start(count, int * 60, int1 * 60)) {
                                ModelManager.showToast("当前有其他任务正在运行!")
                            } else {
                                if (hasPermission(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW))
                                    ModelManager.getNavController().navigate("focusTomato")
                            }
                        } else {
                            ModelManager.openSuspendedWindowPermission()
                            ModelManager.showToast("想要使用番茄循环，必须开启悬浮窗权限!")
                        }
                    },
                    modifier = Modifier
                        .width(40.dp)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0XFFFF6767))
                ) {
                    Column {
                        Text(
                            text = "开",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "始",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

            }
    }
}

@Composable
fun QuickFocus() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "快速专注",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "选择单次专注时长",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EditButton(0, 20)
                    Spacer(modifier = Modifier.width(8.dp))
                    EditButton(1, 30)
                    Spacer(modifier = Modifier.width(8.dp))
                    EditButton(2, 45)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EditButton(3, 60)
                    Spacer(modifier = Modifier.width(8.dp))
                    EditButton(4, 90)
                    Spacer(modifier = Modifier.width(8.dp))
                    EditButton(5, 0)
                }
            }
            Image(
                painter = painterResource(id = R.drawable.lightning),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 45.dp, end = 19.dp)
                    .size(65.dp, 99.dp)
            )
        }
    }
}

@Composable
fun EditButton(id: Int, defaultValue: Int = 0) {
    var minutes by remember { mutableStateOf(defaultValue) }
    var isEditing by remember { mutableStateOf(false) }
    var isInit by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(isEditing) {
        if (isEditing) {
            text = ""
            focusRequester.requestFocus()
        }
        isInit = isEditing
    }
    LaunchedEffect(Unit) {
        val int = ConfigManager.getInt("Tired-Focus-Button-$id")
        if (int != 0) minutes = int
    }
    if (!isEditing)
        TextButton(
            onClick = {
                if (!isEditing && minutes > 0) {
                    if (hasPermission(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW)) {
                        if (ModelManager.getService()?.focusTime?.start(minutes * 60) == false) {
                            Toast.makeText(
                                ModelManager.getMainActivity(),
                                "当前有其他任务正在运行!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }
                        ModelManager.getNavController().navigate("focusing")
                    } else {
                        ModelManager.openSuspendedWindowPermission()
                        ModelManager.showToast("想要使用快速专注，必须开启悬浮窗权限!")
                    }
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.textButtonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
            modifier = Modifier
                .height(30.dp)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(onDragStart = {
                        isEditing = true
                    }) { _, _ ->
                    }
                }
        )
        {
            if (minutes == 0)
                Text(
                    text = "长按编辑",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.secondary
                )
            else
                Text(
                    text = "${minutes}min",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.secondary
                )
        }
    else {
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
                minutes = text.toIntOrNull() ?: defaultValue
                isEditing = false
                focusManager.clearFocus()
                ConfigManager.setInt("Tired-Focus-Button-$id", minutes)
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
                .size(60.dp, 30.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.secondaryVariant)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (!isInit) return@onFocusChanged
                    if (!it.isFocused) {
                        minutes = text.toIntOrNull() ?: defaultValue
                        isEditing = false
                        ConfigManager.setInt("Tired-Focus-Button-$id", minutes)
                    }
                }
        )
    }
}
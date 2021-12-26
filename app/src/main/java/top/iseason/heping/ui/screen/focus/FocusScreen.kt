package top.iseason.heping.ui.screen.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import top.iseason.heping.R
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.theme.GreenSurface

@Composable
fun FocusScreen(viewModel: AppViewModel) {
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
        item { QuickFocus(viewModel) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TomatoCard()
                TomatoCard()
            }
        }
    }
}

@Composable
fun TomatoCard() {
    var isEditing by remember { mutableStateOf(false) }
    var count by remember { mutableStateOf(1) }
    Surface(
        modifier = Modifier
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
                        fontWeight = FontWeight.Bold,
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
                val coroutineScope = rememberCoroutineScope()

                val listState = rememberLazyListState(
                    initialFirstVisibleItemIndex = count - 1,
                    initialFirstVisibleItemScrollOffset = 20
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
                                    20
                                )
                            }
                        }
                }
                LaunchedEffect(listState) {
                    snapshotFlow { listState.firstVisibleItemIndex }.collect {
                        count = it + 1
                    }
                }
                LazyColumn(
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                ) {
                    item { Spacer(modifier = Modifier.height(28.dp)) }
                    items(10) { index ->
                        Box(
                            modifier = Modifier.size(30.dp, 28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                fontSize = if (count == index + 1) 20.sp else 16.sp,
                                fontWeight = if (count == index + 1) FontWeight.Medium else FontWeight.Normal,
                                color = if (count == index + 1) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(28.dp)) }

                }
                Text(
                    text = "次",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    onClick = { /*TODO*/ },
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
fun QuickFocus(viewModel: AppViewModel) {
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
                if (!isEditing)
                    println(minutes)
            },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.textButtonColors(backgroundColor = GreenSurface),
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
                .clip(MaterialTheme.shapes.large)
                .background(GreenSurface)
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
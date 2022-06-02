package top.iseason.heping.ui.screen.me

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import top.iseason.heping.R
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.focus.SettingLine
import top.iseason.heping.ui.screen.focus.SettingTitle
import java.util.*

@Composable
fun MainSetting() {
    NavBar("高级设置") {
        var isShowColorPicker by remember { mutableStateOf(false) }
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            item {
                SettingTitle("权限和运行")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("自动从最近任务列表隐藏", "Main-Setting-AutoHideFromRecent")
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SettingTitle("提醒字幕")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable {
                            isShowColorPicker = true
                        }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(text = "字幕样式", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }

                }
            }
        }
        if (isShowColorPicker) {
            Dialog(
                onDismissRequest = { isShowColorPicker = false },
            ) {
                var colorl by remember { mutableStateOf(HsvColor.DEFAULT) }
                var color by remember { mutableStateOf(Color.White) }
                color = colorl.toColor()
                var hexText by remember { mutableStateOf("") }
                var alphaText by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    val windowManager =
                        ModelManager.getService()?.getWindowManager() ?: return@LaunchedEffect
                    val alpha = windowManager.getAlpha()
                    val color1 = windowManager.getColor()?.uppercase(Locale.ROOT)
                    if (color1 != null) {
                        val hexToRgb = hexToRgb(color1)?.copy(alpha = alpha)
                        if (hexToRgb != null) {
                            colorl = HsvColor.from(hexToRgb)
                            color = hexToRgb
                            hexText = hexToRgb.toHex()
                            alphaText = alpha.toString()
                        }
                    }
                }
                val focusManager = LocalFocusManager.current
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colors.background)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }
                        .padding(16.dp),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(296.dp, 180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(R.drawable.yulang),
                            null,
//                                modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "测试字幕",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Medium,
                            color = color
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = "HEX:")
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = hexText,
                            onValueChange = {
                                val hexToRgb = hexToRgb(it)
                                if (hexToRgb != null) {
                                    color = hexToRgb
                                }
                                hexText = it
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(onDone = {
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
                                .size(100.dp, 48.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colors.onSecondary)

                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Alpha:")
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = if (alphaText.length > 5)
                                alphaText.substring(0, 5) else alphaText,
                            onValueChange = {
                                val toFloatOrNull = it.toFloatOrNull()
                                if (toFloatOrNull != null && toFloatOrNull in 0F..1.0F) {
                                    color = color.copy(alpha = toFloatOrNull)
                                }
                                alphaText = it
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(onDone = {
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
                                .size(60.dp, 48.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colors.onSecondary)

                        )
                    }
                    ClassicColorPicker(
                        color = color,
                        onColorChanged = { color: HsvColor ->
                            colorl = color
                            hexText = color.toColor().toHex()
                            alphaText = color.alpha.toString()
                        },
                        modifier = Modifier.size(250.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                    isShowColorPicker = false
                                }
                        ) {
                            Text(
                                text = "取消", fontSize = 14.sp,
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
                                    isShowColorPicker = false
                                    val toHex = color.toHex()
                                    val alpha = color.alpha
                                    ConfigManager.setString(
                                        "Main-Setting-Caption-Color",
                                        toHex
                                    )
                                    ConfigManager.setFloat(
                                        "Main-Setting-Caption-Alpha",
                                        alpha
                                    )
                                    ModelManager
                                        .getService()
                                        ?.getWindowManager()
                                        ?.setColor(toHex, alpha)
                                }
                        ) {
                            Text(
                                text = "确认", fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.secondaryVariant
                            )
                        }
                    }
                }

            }
        }
        DisposableEffect(Unit) {
            onDispose {
                ModelManager.setHideFromRecent(ConfigManager.getBoolean("Main-Setting-AutoHideFromRecent"))
            }
        }
    }
}

fun Color.toHex(): String {
    (this.red * 255).toInt()
    return String.format(
        "#%02X%02X%02X",
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt()
    )
}

fun hexToRgb(hex: String): Color? {
    if ("" != hex && hex.length == 7) {
        val rgb = IntArray(3)
        rgb[0] = Integer.valueOf(hex.substring(1, 3), 16)
        rgb[1] = Integer.valueOf(hex.substring(3, 5), 16)
        rgb[2] = Integer.valueOf(hex.substring(5, 7), 16)
        return Color(rgb[0], rgb[1], rgb[2])
    }
    return null
}


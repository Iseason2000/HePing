package top.iseason.heping.ui.screen.focus

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import top.iseason.heping.R
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.controller.ExitDialog
import top.iseason.heping.utils.Util


@SuppressLint("InvalidWakeLockTag")
@Composable
fun Focusing() {
    val systemUiController = rememberSystemUiController()
    val isLight = MaterialTheme.colors.isLight
    val color = MaterialTheme.colors.background
    val color2 = MaterialTheme.colors.primaryVariant
    var isFocusing by remember { mutableStateOf(true) }
    var isExit by remember { mutableStateOf(false) }
    var isPreExit by remember { mutableStateOf(false) }
    var maxTime by remember { mutableStateOf(0) }
    var currentTime by remember { mutableStateOf(0) }
    val timer = ModelManager.getService()?.focusTime ?: return
    isFocusing = timer.isFocusing
    maxTime = timer.focusTime
    currentTime = timer.currentTime
    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = color,
            darkIcons = isLight
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            systemUiController.setSystemBarsColor(
                color = color2,
                darkIcons = !isLight
            )
        }
    }
    val animateFloat = remember { Animatable(currentTime.toFloat() / maxTime) }
    LaunchedEffect(Unit) {
        while (isFocusing) {
            delay(1000L)
            if (currentTime > maxTime) {
                isFocusing = false
                animateFloat.snapTo(0F)
                if (ConfigManager.getBoolean("Focus-Setting-EndTip"))
                    ModelManager.tip()
                return@LaunchedEffect
            }
            currentTime++
        }
    }
    LaunchedEffect(isExit) {
        if (isExit) {
            ModelManager.getService()?.focusTime?.stop()
            ModelManager.getNavController().popBackStack()
        }
    }
    LaunchedEffect(animateFloat) {
        animateFloat.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = (maxTime - currentTime) * 1000,
                easing = LinearEasing
            )
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(135.dp))
            Text(
                text = if (isFocusing) "专注中" else "专注已完成",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (MaterialTheme.colors.isLight) Color.Black else Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            val t =
                System.currentTimeMillis() - Util.getDate(0).timeInMillis + (maxTime - currentTime) * 1000L
            val text = if (isFocusing) {
                "预计 ${Util.longTimeFormat2(t)} 结束"
            } else {
                "${Util.longTimeFormat2(t)} - ${Util.longTimeFormat2(t - maxTime * 1000L)}"
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = if (MaterialTheme.colors.isLight) Color.Black else Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))
            val colorN = MaterialTheme.colors.primary
            val colorM = MaterialTheme.colors.secondaryVariant
            if (isFocusing)
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(230.dp)) {
                        val rate = size.height / 230
                        drawCircle(color = colorM, style = Stroke(width = 12 * rate))
                        drawArc(
                            color = colorN,
                            startAngle = -90F,
                            sweepAngle = 360F - 360F * animateFloat.value,
                            useCenter = false,
                            style = Stroke(width = 12 * rate, cap = StrokeCap.Round)
                        )

                    }
                    Text(
                        text = Util.longTimeFormatDetail2((maxTime - currentTime) * 1000L),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (MaterialTheme.colors.isLight) Color.Black else Color.White
                    )
                }
            else
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.finish_focus_task),
                        contentDescription = null, modifier = Modifier.size(230.dp)
                    )
                }

        }
        Row(
            horizontalArrangement = if (isFocusing) Arrangement.SpaceBetween else Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 49.dp)
        ) {
            if (isFocusing)
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        ModelManager.lockScreen()
                    }) {
                    Image(painter = painterResource(R.drawable.lock), contentDescription = null)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "锁屏",
                        color = MaterialTheme.colors.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isPreExit = true
                    if (!isFocusing) {
                        ModelManager.getService()?.focusTime?.stop()
                        ModelManager.getNavController().popBackStack()
                    }
                }) {
                Image(painter = painterResource(R.drawable.exit), contentDescription = null)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "退出",
                    color = MaterialTheme.colors.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

            }

        }
    }
    ExitDialog(
        title = "退出专注",
        "再坚持${Util.longTimeFormatDetail2((maxTime - currentTime) * 1000L)}即可完成本次专注",
        isVisual = { isPreExit },
        onConfirm = {
            isExit = it
            isPreExit = false
        }
    )
}
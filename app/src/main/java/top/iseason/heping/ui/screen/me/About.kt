package top.iseason.heping.ui.screen.me

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.R
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar

@Composable
fun About() {
    NavBar("关于和屏") {
        var isAlert by remember { mutableStateOf(false) }
        var title by remember { mutableStateOf("") }
        var text by remember { mutableStateOf("") }
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    Image(
                        painter = painterResource(id = R.drawable.heping_icon),
                        contentDescription = "logo",
                        modifier = Modifier.size(128.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "和屏",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "您的屏幕健康助手",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            item {
                val mainActivity = ModelManager.getMainActivity()
                MessageBar(
                    "检查更新",
                    mainActivity.packageManager.getPackageInfo(
                        mainActivity.packageName,
                        0
                    ).versionName
                ) {
                    ModelManager
                        .getMainActivity()
                        .startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data =
                                Uri.parse("https://github.com/Iseason2000/HePing/releases")
                        })
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                MessageBar("用户协议") {
                    title = "用户协议"
                    text = "本软件开源(GPL-3.0)且完全免费，请勿用于商业通途!"
                    isAlert = true
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                MessageBar("隐私政策") {
                    title = "隐私政策"
                    text = "软件所需权限仅为了实现功能而申请，不存在功能之外的用途!"
                    isAlert = true
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                MessageBar("反馈问题") {
                    ModelManager.getNavController().navigate("feedBack")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if (isAlert)
            AlertDialog(
                onDismissRequest = {
                    isAlert = false
                },
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = text,
                            fontSize = 16.sp
                        )
                    }
                },
                buttons = {
                }
            )
    }
}

@Composable
fun MessageBar(message: String, tile: String? = null, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(text = message, fontSize = 16.sp, fontWeight = FontWeight.Normal)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (tile != null) {
                    Text(text = tile, fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.width(15.dp))
                }
                Icon(
                    Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            }


        }

    }
}

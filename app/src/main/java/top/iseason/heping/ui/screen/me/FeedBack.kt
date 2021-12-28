package top.iseason.heping.ui.screen.me

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.R
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.focus.SettingTitle

@Composable
fun FeedBack() {
    NavBar("反馈问题") {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            item { SettingTitle(title = "关于软件") }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                MessageBar("反馈问题/Bug") {
                    ModelManager
                        .getMainActivity()
                        .startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data =
                                Uri.parse("https://github.com/Iseason2000/HePing/issues")
                        })
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                MessageBar("开源地址(GPL-3.0 License)") {
                    ModelManager
                        .getMainActivity()
                        .startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data =
                                Uri.parse("https://github.com/Iseason2000/HePing")
                        })
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                SettingTitle(title = "关于作者@Iseason")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MyInfo(R.drawable.github_fill, Color.Black) {
                        ModelManager
                            .getMainActivity()
                            .startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://github.com/Iseason2000")
                            })
                    }
                    MyInfo(R.drawable.bilibili_line, Color(0XFF02b5da)) {
                        ModelManager
                            .getMainActivity()
                            .startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://space.bilibili.com/8689588")
                            })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MyInfo(R.drawable.blog, Color(0XFFFF8800)) {
                        ModelManager
                            .getMainActivity()
                            .startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://www.iseason.top/")
                            })
                    }
                    Surface(
                        modifier = Modifier
                            .size(156.dp, 100.dp)
                            .clip(MaterialTheme.shapes.large)
                    ) {
                        SelectionContainer {
                            Column(modifier = Modifier.padding(all = 16.dp)) {
                                Text(
                                    text = "E-mail: \nIseason2000@qq.com",
                                    fontSize = 12.sp,
                                )
                                Text(
                                    text = "QQ: \n1347811744",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                SettingTitle(title = "请作者喝咖啡")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.wechatpay),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Image(painter = painterResource(id = R.drawable.aipay), contentDescription = null)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MyInfo(icon: Int, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(156.dp, 100.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = null, tint = color)
        }
    }
}
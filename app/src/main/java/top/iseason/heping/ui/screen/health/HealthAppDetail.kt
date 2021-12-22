package top.iseason.heping.ui.screen.health

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.iseason.heping.model.ModelManager

@Composable
fun HealthAppDetail(packageName: String) {
    Box(modifier = Modifier.fillMaxSize())
    val appInfoForAllDays = ModelManager.getViewModel().getAppInfoForAllDays(packageName)

}
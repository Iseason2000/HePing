package top.iseason.heping.ui.screen.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import top.iseason.heping.model.AppViewModel

@Composable
fun HealthScreen(modifier: Modifier = Modifier, viewModel: AppViewModel) {
    val mainColor = MaterialTheme.colors.primaryVariant
    val subColor = MaterialTheme.colors.background
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(mainColor, subColor),
                start = Offset(size.width / 2, 0F),
                end = Offset(size.width / 2, size.height)
            ),
        )
    }
    LazyColumn(modifier = modifier) {
        item {
            UsageWindow(viewModel = viewModel)
        }
    }
}
package top.iseason.heping.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Teal500,
    primaryVariant = Teal700,
    secondary = Purple200,
)

private val LightColorPalette = lightColors(
    primary = GreenPrimary,
    primaryVariant = GreenPrimaryVariant,
    secondary = GreenSecondary,
    secondaryVariant = GreenSecondaryVariant,
    onSecondary = GreenOnSecondaryVariant,
    background = GreenBackground,
    onBackground = GreenOnBackground,
    surface = GreenSurface

    /* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun HePingTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
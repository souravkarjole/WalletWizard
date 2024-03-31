package com.example.walletwizard.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.walletwizard.DarkMode

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val DarkModeDarkSapphire = Color(0xFF2F213C)
val DarkModeLightSapphire = Color(0xFFDDD6E4)
val DarkModeLightestSapphire = Color(0xFFE1DAEB)
val DarkModeLightAliceBlue = Color(0xFFF5EDFC)

val LightModeDarkSapphire = Color(0xFF32367C)
val LightModeLightSapphire = Color(0xFFA0A4C7)
val LightModeLightestSapphire = Color(0xFFE3E8F3)
val LightModeLightAliceBlue = Color(0xFFEEEEF8)

val ExtraDarkTransparent = Color(0x40000000)
val Green = Color(0xFF098D09)

// -----------------------------
val Cerise = 0xFFEB4079
val BrightViolet = 0xFFC429D7
val BitterSweet = 0xFFF55554
val Jade = 0xFF039B6C
val Blue = 0xFF0797BA
val SteelBlue = 0xFF0189D0
val Violet = 0xFF973AEA
val RedViolet = 0xFFEB4899
val RoyalBlue = 0xFF6265F0
val Olive = 0xFF808000
val Ochre = 0xFFD87606
val Arsenic = 0xFF5D5D67
val SlateGray = 0xFF516178
val Peach = 0xFFFFDAB9
val Emerald = 0xFF50C878
val Salmon = 0xFFFA8072
val Teal = 0xFF008080



@Composable
fun observeDarkModeState(darkMode: DarkMode): MutableState<Boolean> {
    val isDarkMode = remember { mutableStateOf(false) }

    LaunchedEffect(darkMode) {
       isDarkMode.value = darkMode.isDarkModeEnabled()
    }

    return isDarkMode
}


@Composable
fun getColorPalette(context: Context):ColorPalette{
    val darkMode:DarkMode = DarkMode.getInstance(context)
    val isDarkMode by observeDarkModeState(darkMode = darkMode)

    if(isDarkMode){
        return ColorPalette(
            Color.White,
            DarkModeDarkSapphire,DarkModeDarkSapphire, DarkModeLightSapphire, DarkModeLightestSapphire,
            DarkModeLightAliceBlue)
    }else{
        return ColorPalette(
            LightModeDarkSapphire,
            Color.White,LightModeDarkSapphire, LightModeLightSapphire, LightModeLightestSapphire,
            LightModeLightAliceBlue)
    }
}

data class ColorPalette(
    val unselectedColor:Color,
    val surfaceColor:Color,
    val darkSapphire:Color,
    val lightSapphire:Color,
    val lightestSapphire:Color,
    val lightAliceSapphire:Color
)
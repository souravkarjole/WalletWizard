package com.example.walletwizard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletwizard.ui.theme.Cerise
import com.example.walletwizard.ui.theme.LightModeDarkSapphire
import com.example.walletwizard.ui.theme.FontName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ListItem(
    useIndication:Boolean = true,
    padding: Dp,
    size: Int = 40,
    text:String,
    textColor:Color,
    fontSize: TextUnit = 16.sp,
    imageVector: ImageVector,
    color: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    val viewConfiguration = LocalViewConfiguration.current


    LaunchedEffect(interactionSource) {
        var isLongClick = false

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongClick = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongClick = true
                    onLongClick()
                }

                is PressInteraction.Release -> {
                    if (isLongClick.not()) {
                        onClick()
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = if(useIndication) rememberRipple() else null)
            { },
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Row(modifier = Modifier
            .padding(padding)
            .background(color = Color.Transparent)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(size.dp)
                .wrapContentSize()
                .background(color, shape = CircleShape)
            ){
                IconButton(
                    modifier = Modifier
                        .wrapContentSize(),
                    onClick = { /*TODO*/ },
                    enabled = false
                ) {
                    Icon(
                        modifier = Modifier.size(size.div(2).plus(2).dp),
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Text(
                modifier = Modifier.padding(start = padding.plus(4.dp), end = padding.plus(4.dp)),
                text = text,
                fontFamily = FontName,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
                color = textColor
            )
        }
    }
}

@Preview
@Composable
fun ssd(){
    ListItem(padding = 10.dp, size = 36, text = "Sourav", fontSize = 16.sp, imageVector = Icons.Default.Home, textColor = LightModeDarkSapphire, color = Color(Cerise), onClick = {}, onLongClick = {})
}
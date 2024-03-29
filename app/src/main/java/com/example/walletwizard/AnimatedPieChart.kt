package com.example.walletwizard


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.walletwizard.Model.PieData
import com.example.walletwizard.ui.theme.BitterSweet
import com.example.walletwizard.ui.theme.Blue
import com.example.walletwizard.ui.theme.Cerise
import com.example.walletwizard.ui.theme.Emerald
import com.example.walletwizard.ui.theme.Jade
import com.example.walletwizard.ui.theme.Peach
import com.example.walletwizard.ui.theme.RoyalBlue
import com.example.walletwizard.ui.theme.Salmon
import com.example.walletwizard.ui.theme.Teal
import kotlinx.coroutines.launch


data class ArcAngle(
    val animation: Animatable<Float, AnimationVector1D>,
    val sweepAngle: Float,
    val color: Color
)

@Composable
fun AnimatedPieChart(
    modifier: Modifier = Modifier,
    pieDataPoints: List<PieData>,
    content: @Composable (modifier: Modifier) -> Unit
){
    val total = pieDataPoints.fold(0f) { acc, pieData ->
        acc+pieData.value
    }.div(360)


    var curr:Long = 0
    val arcs = if(pieDataPoints.isEmpty()) {
        val list = listOf(PieData("",-1,Color(0xFF9E9E9E)))
        list.map {
            ArcAngle(
                sweepAngle = 360f,
                animation = Animatable(0f),
                color = it.color
            )
        }
    }else{
        pieDataPoints.map {
            curr += it.value
            ArcAngle(
                sweepAngle = curr / total,
                animation = Animatable(0f),
                color = it.color
            )
        }
    }


    LaunchedEffect(key1 = arcs){
        arcs.map {
            launch {
                it.animation.animateTo(
                    targetValue = it.sweepAngle,
                    animationSpec = tween(
                        durationMillis = 900,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Canvas(
        modifier = modifier
    ){
        val stroke = Stroke(width = 45f)

        arcs.reversed().map {
            drawArc(
                startAngle = -90f,
                sweepAngle = it.animation.value,
                color = it.color,
                useCenter = false,
                style = stroke
            )
        }
    }

    content(modifier)
}

@Preview
@Composable
fun sd(){
//    val pieData = listOf(
//        PieData("Food & Drinks", 2, Color(BitterSweet)),
//        PieData("Bill", 18, Color(Emerald)),
//        PieData("Health", 1, Color(Jade)),
//        PieData("Car", 6, Color(Teal)),
//        PieData("Entertainment", 11, Color(Cerise)),
//        PieData("Pets", 2, Color(Blue)),
//        PieData("Housing", 66, Color(Salmon)),
//        PieData("Shopping", 13, Color(Peach)),
//        PieData("Clothes", 10, Color(RoyalBlue)),
//    )
    AnimatedPieChart(
        pieDataPoints = listOf(),
        modifier = Modifier
            .padding(30.dp)
            .size(140.dp),
        content = {it ->

        }
    )
}
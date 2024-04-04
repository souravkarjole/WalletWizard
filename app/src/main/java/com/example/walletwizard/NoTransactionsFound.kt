package com.example.walletwizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.walletwizard.ui.theme.FontName


@Composable
fun NoTransactionFound(modifier: Modifier,raw:Int,text:String){

    val lottieComposition = rememberLottieComposition(LottieCompositionSpec.RawRes(raw))

    Column( modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(modifier = Modifier.size(140.dp), composition = lottieComposition.value, speed = 1.4f)
        Text(
            modifier = Modifier,
            text = text,
            fontFamily = FontName,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFFB6B5B5)
        )
    }
}
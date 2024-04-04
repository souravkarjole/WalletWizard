package com.example.walletwizard.Model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoriesData(
    val id:Int,
    var text:String,
    val type:String,
    val imageVector: Int,
    val color: Long
)

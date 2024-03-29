package com.example.walletwizard.Model

import com.example.walletwizard.ui.theme.Arsenic
import com.example.walletwizard.ui.theme.Blue
import com.example.walletwizard.ui.theme.BrightViolet
import com.example.walletwizard.ui.theme.Cerise
import com.example.walletwizard.ui.theme.Jade
import com.example.walletwizard.ui.theme.Ochre
import com.example.walletwizard.ui.theme.Peach
import com.example.walletwizard.ui.theme.Salmon
import com.example.walletwizard.ui.theme.SlateGray
import com.example.walletwizard.ui.theme.SteelBlue
import com.example.walletwizard.ui.theme.Teal

data class ChooseColorData(
    val color:Long
)

fun colorList(): List<ChooseColorData> {
    return listOf(
        ChooseColorData(Cerise),
        ChooseColorData(Cerise.plus(25)),
        ChooseColorData(Cerise.plus(35)),
        ChooseColorData(BrightViolet),
        ChooseColorData(BrightViolet.plus(25)),
        ChooseColorData(BrightViolet.plus(35)),
        ChooseColorData(Salmon),
        ChooseColorData(Salmon.plus(25)),
        ChooseColorData(Salmon.plus(35)),
        ChooseColorData(Peach),
        ChooseColorData(Peach.plus(25)),
        ChooseColorData(Peach.plus(35)),
        ChooseColorData(Teal),
        ChooseColorData(Teal.plus(25)),
        ChooseColorData(Teal.plus(35)),
        ChooseColorData(SteelBlue),
        ChooseColorData(SteelBlue.plus(25)),
        ChooseColorData(SteelBlue.plus(35)),
        ChooseColorData(Blue),
        ChooseColorData(Blue.plus(25)),
        ChooseColorData(Blue.plus(35)),
        ChooseColorData(Jade),
        ChooseColorData(Jade.plus(25)),
        ChooseColorData(Jade.plus(35)),
        ChooseColorData(Ochre),
        ChooseColorData(Ochre.plus(25)),
        ChooseColorData(Ochre.plus(35)),
        ChooseColorData(Arsenic),
        ChooseColorData(Arsenic.plus(25)),
        ChooseColorData(Arsenic.plus(35)),
        ChooseColorData(SlateGray),
        ChooseColorData(SlateGray.plus(25)),
        ChooseColorData(SlateGray.plus(35)),
    )
}
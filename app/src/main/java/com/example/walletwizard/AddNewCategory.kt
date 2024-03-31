package com.example.walletwizard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walletwizard.Database.SqLiteDB
import com.example.walletwizard.Model.colorList
import com.example.walletwizard.Model.iconList
import com.example.walletwizard.ui.theme.Cerise
import com.example.walletwizard.ui.theme.DarkModeDarkSapphire
import com.example.walletwizard.ui.theme.DarkModeLightSapphire
import com.example.walletwizard.ui.theme.LightModeDarkSapphire
import com.example.walletwizard.ui.theme.FontName
import com.example.walletwizard.ui.theme.Green
import com.example.walletwizard.ui.theme.LightModeLightAliceBlue
import com.example.walletwizard.ui.theme.getColorPalette


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCategory(
    navController: NavController,
    id: Int = -1
){
    val context = LocalContext.current
    val db = SqLiteDB(context)
    val categoriesData = db.getParticularCategory(id)

    val text = remember { mutableStateOf(categoriesData.text) }
    val selectedCategory = remember { mutableIntStateOf(if(categoriesData.type.isEmpty() || categoriesData.type == "expense") 0 else 1) }
    val selectedImageDrawable = remember { mutableIntStateOf(categoriesData.imageVector) }
    val selectedColor = remember { mutableLongStateOf(categoriesData.color) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val toolbarTitle = if(id == -1) "New Category" else "Edit Category"
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val colorPalette = getColorPalette(context = LocalContext.current)


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarColors(
                    containerColor = colorPalette.surfaceColor,
                    navigationIconContentColor =  colorPalette.darkSapphire,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = colorPalette.unselectedColor,
                    actionIconContentColor =  colorPalette.darkSapphire
                ),
                title = {
                    Text(
                        text = toolbarTitle,
                        fontSize = 20.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(43.dp)
                            .wrapContentSize()
                            .background(color = colorPalette.lightAliceSapphire, RoundedCornerShape(100.dp))

                    ) {
                        IconButton(
                            onClick = {
                                // TODO
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(22.dp),
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(43.dp)
                            .wrapContentSize()
                            .background(color = colorPalette.lightAliceSapphire, RoundedCornerShape(100.dp))

                    ) {
                        IconButton(
                            onClick = {
                                if(text.value.isNotEmpty()) {
                                    if(id == -1) {
                                        db.insertCategory(
                                            text.value,
                                            if (selectedCategory.intValue == 0) "expense" else "income",
                                            selectedImageDrawable.intValue,
                                            selectedColor.longValue
                                        )
                                    }else{
                                        db.updateParticularCategory(
                                            id,
                                            text.value,
                                            if (selectedCategory.intValue == 0) "expense" else "income",
                                            selectedImageDrawable.intValue,
                                            selectedColor.longValue
                                        )
                                    }
                                    navController.popBackStack()
                                }else{
                                    Toast.makeText(context, "Textfield cannot be empty", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(22.dp),
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                        }
                    }
                }
            )
        }
    ) {paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .background(color = colorPalette.lightestSapphire)
                .fillMaxSize()) {
            Row(modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = if (selectedCategory.intValue == 0) Color.Red else Color.White,
                            RoundedCornerShape(50.dp)
                        )
                        .border(color = Color.Red, width = 1.dp, shape = CircleShape)
                        .clickable {
                            selectedCategory.intValue = 0
                        }
                ){
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        text = "Expense",
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = if(selectedCategory.intValue == 0) Color.White else Color.Red
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (selectedCategory.intValue == 1) Green else Color.White,
                            RoundedCornerShape(50.dp)
                        )
                        .border(color = Green, width = 1.dp, shape = CircleShape)
                        .clickable {
                            selectedCategory.intValue = 1
                        }
                ){
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        text = "Income ",
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = if(selectedCategory.intValue == 1) Color.White else Green
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
                    .border(color = colorPalette.darkSapphire, width = 2.dp, shape = RoundedCornerShape(10.dp))
                    .weight(0.9f)) {


                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                          ,
                        value = text.value,
                        onValueChange = { newText ->
                                text.value = newText
                        },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Enter the name",
                                color= colorPalette.darkSapphire,
                                fontFamily = FontName,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = colorPalette.darkSapphire,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        )
                    )

                }

                Box(modifier = Modifier
                    .size(45.dp)
                    .background(Color(selectedColor.longValue), RoundedCornerShape(100.dp))
                ){
                    IconButton(
                        modifier = Modifier
                            .wrapContentSize(),
                        onClick = {
                            showBottomSheet.value = true
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(23.dp),
                            imageVector = ImageVector.vectorResource(id = selectedImageDrawable.intValue),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

        }
    }

    if(showBottomSheet.value){
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = sheetState
        )
        {
            BottomSheetDialogItems(
                onClickClosed = {
                    showBottomSheet.value = false
                },
                onClickSaved = { color,icon ->
                    showBottomSheet.value = false
                    selectedImageDrawable.intValue = color
                    selectedColor.longValue = icon
                }
            )
        }
    }
}

@Composable
fun BottomSheetDialogItems(
    onClickClosed: () -> Unit,
    onClickSaved: (Int,Long) -> Unit
){
    val colorPalette = getColorPalette(context = LocalContext.current)


    var isSelectedColorList = remember { mutableStateListOf<Boolean>() }
    var isSelectedIconList = remember { mutableStateListOf<Boolean>() }
    var selectedColor = remember { mutableLongStateOf(Cerise) }
    var selectedIcon = remember { mutableIntStateOf(R.drawable.cocktail) }

    var colorList = colorList()
    var iconList = iconList()

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(520.dp)
        .padding(10.dp))
    {


        if (isSelectedColorList.isEmpty()) {
            isSelectedColorList.addAll(List(colorList.size) { false })
        }

        if (isSelectedIconList.isEmpty()) {
            isSelectedIconList.addAll(List(iconList.size) { false })
        }

        Row(
            modifier = Modifier
                .padding(start = 4.dp, bottom = 15.dp)
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .wrapContentSize()
                    .background(Color(selectedColor.longValue), RoundedCornerShape(100.dp))
            ) {
                IconButton(
                    modifier = Modifier
                        .wrapContentSize(),
                    onClick = { /*TODO*/ },
                    enabled = false
                ) {

                    Icon(
                        imageVector = ImageVector.vectorResource(selectedIcon.intValue),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = "Category Icon",
                    fontSize = 18.sp,
                    color = colorPalette.darkSapphire,
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

        }

        LazyRow(
            horizontalArrangement = Arrangement.Center
        ) {
            val rows = colorList.chunked(3) // Split the colorList into chunks of 3 items per row

            items(rows.size) { rowIndex ->

                Column {
                    rows[rowIndex].forEachIndexed { index, item ->
                        val actualIndex = index + rowIndex * 3 // Calculate the actual index

                        SelectableCircle(
                            selected = isSelectedColorList[actualIndex],
                            backgroundColor = Color(item.color)
                        ) {
                            selectedColor.longValue = item.color
                            isSelectedColorList.indices.forEach { i ->
                                isSelectedColorList[i] = i == actualIndex
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.Center
        ) {
            val rows = iconList.chunked(7) // Split the colorList into chunks of 3 items per row

            items(rows.size) { rowIndex ->

                Row {
                    rows[rowIndex].forEachIndexed { index, item ->
                        val actualIndex = index + rowIndex * 7 // Calculate the actual index

                        SelectableIcon(
                            selected = isSelectedIconList[actualIndex],
                            icon = item.icon
                        ) {
                            selectedIcon.intValue = item.icon
                            isSelectedIconList.indices.forEach { i ->
                                isSelectedIconList[i] = i == actualIndex
                            }
                        }
                    }
                }
            }
        }
    }

    HorizontalDivider()
    Row(modifier = Modifier.padding(10.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .clickable {
                    onClickClosed()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = "Cancel",
                color = colorPalette.darkSapphire,
                fontFamily = FontName,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .clickable {
                    onClickSaved(selectedIcon.intValue, selectedColor.longValue)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = "Save",
                color = colorPalette.darkSapphire,
                fontFamily = FontName,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun SelectableCircle(
    modifier: Modifier = Modifier,
    selected: Boolean,
    backgroundColor: Color,
    borderColor: Color = Color.LightGray,
    size: Int = 40,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .padding(4.dp)
        .border(
            width = if (selected) (2.2).dp else (-1).dp,
            color = borderColor,
            shape = CircleShape
        )
    ) {
        Box(
            modifier = modifier
                .size(size.dp)
                .padding(4.dp)
                .clip(CircleShape)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .clickable { onClick() }
        )
    }
}

@Composable
fun SelectableIcon(
    modifier: Modifier = Modifier,
    selected: Boolean,
    icon:Int,
    tint: Color = Color.Black,
    borderColor: Color = Color.LightGray,
    size: Int = 45,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .padding(4.dp)
        .clip(CircleShape)
        .border(
            width = if (selected) (2.5).dp else (-1).dp,
            color = borderColor,
            shape = CircleShape
        )
        .clickable { onClick() }
    ) {
        Icon(
            modifier = modifier
                .size(size.dp)
                .padding(10.dp)
            ,
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null, tint = tint
        )
    }
}

@Preview
@Composable
fun NewCategoryPreview(){
//    var isSelected = remember { mutableStateOf(false) }
//
//    SelectableIcon(
//        selected = isSelected.value,
//        icon = R.drawable.beach,
//        onClick = { isSelected.value = !isSelected.value }
//    )
//    NewCategory(navController = rememberNavController())
}
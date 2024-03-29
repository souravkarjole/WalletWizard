package com.example.walletwizard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.containerColor
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.walletwizard.Database.SqLiteDB
import com.example.walletwizard.Model.CategoriesData
import com.example.walletwizard.ui.theme.Cerise
import com.example.walletwizard.ui.theme.DarkSapphire
import com.example.walletwizard.ui.theme.FontName
import com.example.walletwizard.ui.theme.LightAliceBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditCategories(navController: NavController){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp),
                title = {
                    Text(
                        text = "EditCategories",
                        color = DarkSapphire,
                        fontSize = 20.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .wrapContentSize()
                            .background(color = LightAliceBlue, RoundedCornerShape(100.dp))

                    ) {
                        IconButton(
                            onClick = {
                                navController.navigate("NewCategory/${-1}")
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(22.dp),
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = DarkSapphire
                            )
                        }
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .wrapContentSize()
                            .background(color = LightAliceBlue, RoundedCornerShape(100.dp))

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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = DarkSapphire
                            )
                        }
                    }
                }
            )
        }
    ){paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            val pagerState = rememberPagerState(pageCount = {2})
            val showDialogBox = remember {
                mutableIntStateOf(0)
            }
            val context = LocalContext.current
            val db = SqLiteDB(context)
            val scope = rememberCoroutineScope()

            TabRow(
                modifier = Modifier
                    .padding(top = 20.dp, start = 8.dp, end = 8.dp)
                    .clip(CircleShape),
                divider = {},
                selectedTabIndex = pagerState.currentPage,
                containerColor = LightAliceBlue,
                indicator = {tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .width(0.dp)
                            .height(0.dp)
                            .clip(
                                CircleShape
                            ),
                        color = DarkSapphire
                    )
                }
            ) {
                Tab(
                    modifier = Modifier
                        .background(
                            color = if (pagerState.currentPage == 0) DarkSapphire else Color.Transparent,
                            RoundedCornerShape(50.dp)
                        )
                        .clip(CircleShape),
                    selected = pagerState.currentPage == 0,
                    text = {
                        Text(
                            text = "Expense",
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = if(pagerState.currentPage == 0) Color.White else DarkSapphire
                        )
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                )

                Tab(
                    modifier = Modifier
                        .background(
                            color = if (pagerState.currentPage == 1) DarkSapphire else Color.Transparent,
                            RoundedCornerShape(50.dp)
                        )
                        .clip(CircleShape),
                    selected = pagerState.currentPage == 1,
                    text = {
                        Text(
                            text = "Income",
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = if(pagerState.currentPage == 1) Color.White else DarkSapphire)
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
            }
            HorizontalPager(state = pagerState) {page ->
                if(page == 0){
                    Expense(
                        onClick = { id ->
                            navController.navigate("NewCategory/$id")
                        },
                        onLongClick = {id->
                            showDialogBox.intValue = id
                        }
                    )
                }else{
                    Income(
                        onClick = { id ->
                            navController.navigate("NewCategory/$id")
                        },
                        onLongClick = {id->
                            showDialogBox.intValue = id
                        }
                    )
                }
            }

            if(showDialogBox.intValue != 0){
                DialogBox(
                    text = "You are about to delete this category.\n" +
                            "This will also delete its associated transactions.",
                    onDismissRequest = { showDialogBox.intValue = 0 },
                    onConfirmationRequest = {
                        db.deleteParticularCategory(showDialogBox.intValue)
                        showDialogBox.intValue = 0
                        Toast.makeText(context,"Category deleted successfully!",Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun Expense(onClick: (Int) -> Unit,onLongClick: (Int) -> Unit){
    val list = expensesListData()
    LazyColumn(modifier = Modifier
        .padding(10.dp)
        .fillMaxSize()
    ) {

        items(list.size) {index->
            val categoriesData = list[index]
            ListItem(
                padding = 10.dp,
                text = categoriesData.text,
                imageVector = ImageVector.vectorResource(categoriesData.imageVector),
                color = Color(categoriesData.color),
                onClick = {
                    onClick(categoriesData.id)
                },
                onLongClick = {
                    onLongClick(categoriesData.id)
                }
            )
        }
    }
}

@Composable
fun Income(onClick: (Int) -> Unit,onLongClick: (Int) -> Unit){
    val list = incomeListData()
    LazyColumn(modifier = Modifier
        .padding(10.dp)
        .fillMaxSize()
    ) {

        items(list.size) {index->
            val categoriesData = list[index]
            ListItem(
                padding = 10.dp,
                text = categoriesData.text,
                imageVector = ImageVector.vectorResource(categoriesData.imageVector),
                color = Color(categoriesData.color),
                onClick = {
                    onClick(categoriesData.id)
                },
                onLongClick = {
                    onLongClick(categoriesData.id)
                }
            )
        }
    }
}

@Preview
@Composable
fun EditCategoriesPreview(){
    EditCategories(rememberNavController())
}


@Composable
fun expensesListData(): List<CategoriesData>{
    val context = LocalContext.current
    val db = SqLiteDB(context)

    return db.getCategories("expense")

//    return listOf(CategoriesData(1,"sds","expense",R.drawable.vector, Cerise))
}

@Composable
fun incomeListData(): List<CategoriesData> {
    val context = LocalContext.current
    val db = SqLiteDB(context)

    return db.getCategories("income")
//    return listOf(CategoriesData(1,"sds","income",R.drawable.vector, Cerise))
}
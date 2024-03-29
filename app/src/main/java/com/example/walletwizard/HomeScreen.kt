package com.example.walletwizard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.walletwizard.Database.SqLiteDB
import com.example.walletwizard.Model.CategoriesData
import com.example.walletwizard.Model.PieData
import com.example.walletwizard.Model.TransactionsData
import com.example.walletwizard.ui.theme.DarkSapphire
import com.example.walletwizard.ui.theme.ExtraDarkTransparent
import com.example.walletwizard.ui.theme.FontName
import com.example.walletwizard.ui.theme.Green
import com.example.walletwizard.ui.theme.LightAliceBlue
import com.example.walletwizard.ui.theme.LightestSapphire
import com.example.walletwizard.ui.theme.WalletWizardTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletWizardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "HomeScreen" ){
                        composable("EditCategories"){
                            EditCategories(navController)
                        }
                        composable(
                            "NewCategory/{id}",
                            arguments = listOf(navArgument("id") {type = NavType.IntType})
                        ){
                            val id = it.arguments?.getInt("id") ?: -1
                            NewCategory(navController,id)
                        }
                        composable("HomeScreen"){
                            NavigationDrawer(navController)
                        }
                        composable("Settings"){
                            Settings(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationDrawer(navController: NavController){
    var scope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var navigationItems = listOf("Daily","Weekly","Monthly","Yearly")
    var navigationItemIndex by rememberSaveable{
        mutableStateOf(2)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier
                .padding(end = 80.dp)) {
                Spacer(modifier = Modifier.padding(25.dp))
                navigationItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(LightestSapphire),
                        label = {
                            Text(
                                text = item,
                                color = DarkSapphire,
                                fontFamily = FontName,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        selected = index == navigationItemIndex,
                        onClick = {
                            navigationItemIndex = index
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "Settings",
                            color = DarkSapphire,
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    selected = false,
                    onClick = {
                        navController.navigate("Settings")
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                           Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = DarkSapphire)
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.padding(bottom = 20.dp))
            }
    }) {
        ScaffoldImplementation(drawerState,navigationItemIndex){ name->
            navController.navigate(name)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScaffoldImplementation(
    drawerState:DrawerState,
    selectedItemIndex: Int,
    onClick: (String) -> Unit
){
    var transactionsData = remember {
        mutableStateOf(TransactionsData())
    }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val isSearchBarClicked = remember{
        mutableStateOf(false)
    }
    val isTransactionClicked= remember{
        mutableStateOf(false)
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp),
                title = {
                    Text(
                        text = "WalletWizard",
                        color = DarkSapphire,
                        fontSize = 20.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
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
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(22.dp),
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                tint = DarkSapphire
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .wrapContentSize()
                            .background(color = LightAliceBlue, RoundedCornerShape(100.dp))

                    ) {
                        IconButton(
                            modifier = Modifier
                                .wrapContentSize(),
                            onClick = {
                                isSearchBarClicked.value = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(22.dp),
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = DarkSapphire
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 15.dp, end = 10.dp),
                onClick = {
                    if(pagerState.currentPage == 0) {
                        onClick("EditCategories")
                    }else{
                        transactionsData.value = TransactionsData()
                        isTransactionClicked.value = true
                    }
                },
                containerColor = DarkSapphire
            ) {
                Icon(imageVector = if(pagerState.currentPage == 0) Icons.Default.Edit else Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ){ paddingContent ->
        Column(modifier = Modifier.padding(paddingContent)

        ) {
            TabRow(
                modifier = Modifier
                    .padding(top = 12.dp),
                selectedTabIndex = pagerState.currentPage,
                divider = {},
                indicator = {tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = DarkSapphire
                    )
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    text = {
                         Text(
                             text = "Categories", fontFamily = FontName,
                             fontWeight = FontWeight.Normal,
                             fontSize = 16.sp,
                             color = DarkSapphire)
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    text = {
                        Text(
                            text = "Transactions",
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = DarkSapphire
                        )
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
            }

            HorizontalPager(
                state = pagerState
            )
            {page ->
                Content(page, selectedItemIndex, onTransactionItemClicked = { data ->
                    isTransactionClicked.value = true
                    transactionsData.value = data
                })
            }
        }
    }


    if(isTransactionClicked.value){
        CreateTransaction(
            {
                isTransactionClicked.value = false
            },
            transactionsData.value
        )
    }

    if(isSearchBarClicked.value){
        SearchBar{
            isSearchBarClicked.value = false
        }
    }
}


@Composable
fun Content(page:Int,index:Int,onTransactionItemClicked: (TransactionsData) -> Unit){
    val monthList = listOf("January","February","March","April","May","June","July","August","September","October","November","December")
    val daysList = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")

    val calendar = Calendar.getInstance()
    val calendarTime = remember { mutableLongStateOf(calendar.timeInMillis) }
    val currIndex = remember { mutableIntStateOf(index) }


    // reset values
    if(index != currIndex.intValue){
        val newCalendar = Calendar.getInstance()
        calendar.timeInMillis = newCalendar.timeInMillis
        calendarTime.longValue = newCalendar.timeInMillis
        currIndex.intValue = index
    }

    Column(Modifier.background(LightAliceBlue)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    when(index) {
                        0 -> {
                            calendar.add(Calendar.DAY_OF_MONTH,-1)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                        1 -> {
                            calendar.add(Calendar.DAY_OF_MONTH,-7)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                        2 -> {
                            calendar.add(Calendar.MONTH, -1)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                        3 -> {
                            calendar.add(Calendar.YEAR, -1)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = DarkSapphire
                )
            }
            Text(
                text = (when(index){
                    0 -> {
                        calendar.apply {
                            timeInMillis = calendarTime.longValue
                        }
                        "${daysList[calendar.get(Calendar.DAY_OF_WEEK)-1]}, ${calendar.get(Calendar.DAY_OF_MONTH)} ${monthList[calendar.get(Calendar.MONTH)].substring(0,3)}"
                    }
                    1 -> {
                        calendar.apply {
                            timeInMillis = calendarTime.longValue
                        }
                        weeklyCalendar(calendar)
                    }
                    2 -> {
                        calendar.apply {
                            timeInMillis = calendarTime.longValue
                        }
                        "${monthList[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}"
                    }
                    3 -> {
                        calendar.apply {
                            timeInMillis = calendarTime.longValue
                        }
                        "${calendar.get(Calendar.YEAR)}"
                    }
                    else -> {"Error"}
                }),
                fontFamily = FontName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkSapphire
            )
            IconButton(
                onClick = {
                    when(index) {
                        0 -> {
                            calendar.add(Calendar.DAY_OF_MONTH,1)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                        1 -> {
                            calendar.add(Calendar.DAY_OF_MONTH,7)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                        2 -> {
                            calendar.add(Calendar.MONTH, 1)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                        3 -> {
                            calendar.add(Calendar.YEAR, 1)
                            calendarTime.longValue = calendar.timeInMillis
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DarkSapphire
                )
            }
        }

        if(page == 0) {
            Categories(index,calendar)
        }else{
            Transactions(index,calendar){data->
                onTransactionItemClicked(data)
            }
        }
    }
}

@Composable
fun Categories(index:Int,calendar: Calendar){
    val context = LocalContext.current
    val db = SqLiteDB(context)

    var expenselist: List<TransactionsData> = mutableListOf()
    var incomelist: List<TransactionsData> = mutableListOf()
    var expensePieData = mutableListOf<PieData>()
    var incomePieData = mutableListOf<PieData>()
    var totalExpense = 0
    var totalIncome = 0
    val isExpense = remember {
        mutableStateOf(true)
    }

    when(index){
        0 -> {
            expenselist = db.getDailyTransactions(
                "expense",
                String.format("%02d",(calendar.get(Calendar.DAY_OF_MONTH)))
            )

            incomelist = db.getDailyTransactions(
                "income",
                String.format("%02d",(calendar.get(Calendar.DAY_OF_MONTH)))
            )
        }
        1 -> {
            val tempCalendar = Calendar.getInstance()
            tempCalendar.timeInMillis = calendar.timeInMillis

            tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val dateTimeFormatter = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
            val firstDayOfWeek = dateTimeFormatter.format(tempCalendar.timeInMillis)

            tempCalendar.add(Calendar.DAY_OF_MONTH,6)
            val lastDayOfWeek = dateTimeFormatter.format(tempCalendar.timeInMillis)

            expenselist = db.getWeeklyTransactions(
                "expense",
                firstDayOfWeek,
                lastDayOfWeek
                )

            incomelist = db.getWeeklyTransactions(
                "income",
                firstDayOfWeek,
                lastDayOfWeek
            )
        }
        2 -> {
            expenselist = db.getMonthlyTransactions(
                "expense",
                String.format("%02d",(calendar.get(Calendar.MONTH)+1))
            )

            incomelist = db.getMonthlyTransactions(
                "income",
                String.format("%02d",(calendar.get(Calendar.MONTH)+1))
            )
        }
        3 -> {
            expenselist = db.getYearlyTransactions(
                "expense",
                String.format("%02d",(calendar.get(Calendar.YEAR)))
            )

            incomelist = db.getYearlyTransactions(
                "income",
                String.format("%02d",(calendar.get(Calendar.YEAR)))
            )
        }
    }

    expenselist.forEachIndexed { _, item ->
        totalExpense += item.amount
        expensePieData.add(PieData(item.name,item.amount.toLong(), Color(item.color)))
    }

    incomelist.forEachIndexed { _, item ->
        totalIncome += item.amount
        incomePieData.add(PieData(item.name,item.amount.toLong(), Color(item.color)))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isExpense.value = !isExpense.value
                    },
                contentAlignment = Alignment.Center
            )
            {
                AnimatedPieChart(
                    modifier = Modifier
                        .padding(30.dp)
                        .size(140.dp),
                    pieDataPoints = if(isExpense.value) expensePieData else incomePieData,
                    content = {
                        Column(
                            modifier = it,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                            )
                        {
                            Text(
                                text = if(isExpense.value) "$totalExpense" else "$totalIncome",
                                color = if(isExpense.value) Color.Red else Green,
                                fontFamily = FontName,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                                )
                            Text(
                                text = if(!isExpense.value) "$totalExpense" else "$totalIncome",
                                color = if(!isExpense.value) Color.Red else Green,
                                fontFamily = FontName,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }


            if(isExpense.value && expenselist.isEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    text = "No expense\ntransactions found",
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFB6B5B5)
                )
            }else if(!isExpense.value && incomelist.isEmpty()){
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    text = "No income\ntransactions found",
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFB6B5B5)
                )
            }
            if(isExpense.value){
                expenselist.forEachIndexed { index, item ->
                    ListCategoryItems(text = item.name, imageVector = ImageVector.vectorResource(item.imageVector), Color(item.color),
                        item.amount.toLong(),totalExpense.toLong(),isExpense.value)
                }
            }else{
                incomelist.forEachIndexed { index, item ->
                    ListCategoryItems(text = item.name, imageVector = ImageVector.vectorResource(item.imageVector), Color(item.color),
                        item.amount.toLong(),totalIncome.toLong(),isExpense.value)
                }
            }

        }
    }
}

@Composable
fun Transactions(index:Int,calendar: Calendar,onTransactionItemClicked: (TransactionsData) -> Unit){
    val context = LocalContext.current
    val db = SqLiteDB(context)
    var list: List<TransactionsData> = listOf()

    when (index) {
        0 -> {
            list = db.getAllDailyTransactions(
                String.format("%02d", (calendar.get(Calendar.DAY_OF_MONTH)))
            )
        }
        1 -> {
            val tempCalendar = Calendar.getInstance()
            tempCalendar.timeInMillis = calendar.timeInMillis

            tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val dateTimeFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val firstDayOfWeek = dateTimeFormatter.format(tempCalendar.timeInMillis)

            tempCalendar.add(Calendar.DAY_OF_MONTH, 6)
            val lastDayOfWeek = dateTimeFormatter.format(tempCalendar.timeInMillis)

            list = db.getAllWeeklyTransactions(
                firstDayOfWeek,
                lastDayOfWeek
            )
        }
        2 -> {
            list = db.getAllMonthlyTransactions(
                String.format("%02d", (calendar.get(Calendar.MONTH) + 1))
            )

        }
        3 -> {
            list = db.getAllYearlyTransactions(
                String.format("%02d", (calendar.get(Calendar.YEAR)))
            )
        }
    }

    var currentDate = ""
    if(list.isEmpty()){
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(
                text = "No transactions found",
                fontFamily = FontName,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFFB6B5B5)
            )
        }
    }

    val itemDateList = mutableMapOf<String,List<TransactionsData>>()
    val itemsList = mutableListOf<TransactionsData>()

    list.forEachIndexed { index, transactionsData ->
        if (currentDate != transactionsData.date) {
            // If the current date changes, add the itemsList to itemDateList
            if (itemsList.isNotEmpty()) {
                itemDateList[currentDate] = itemsList.toList()
                itemsList.clear()
            }
            currentDate = transactionsData.date
        }

        // Add transactionsData to itemsList
        itemsList.add(transactionsData)

        if(list.size -1 == index && itemsList.isNotEmpty()){
            itemDateList[currentDate] = itemsList.toList()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        itemDateList.entries.forEachIndexed { index, (date, transactionList) ->
            item{
                if(index != 0) {
                    HorizontalDivider(modifier = Modifier.padding(6.dp))
                }
                LazyColumnHeader(date)
            }
            transactionList.forEachIndexed { index, transactionsData ->
                item {
                    ListTransactionItems(
                        text = transactionsData.name,
                        imageVector = ImageVector.vectorResource(transactionsData.imageVector),
                        color = Color(transactionsData.color),
                        type = transactionsData.type,
                        amount = transactionsData.amount.toLong(),
                        time = transactionsData.timestamp,
                        onClick = {
                            onTransactionItemClicked(transactionList[index])
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LazyColumnHeader(strDate:String){
    val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val date = LocalDate.parse(strDate,dateFormat)

    val day = date.dayOfMonth
    val dayOfWeek = date.dayOfWeek.name.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase() }
    val month = date.month.name.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase() }
    val year = date.year

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "$day",
            fontFamily = FontName,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            color = DarkSapphire
        )
        Column(
            modifier = Modifier
                .padding(5.dp)
                .wrapContentSize()
        ) {
            Text(
                text = dayOfWeek,
                fontFamily = FontName,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = DarkSapphire)
            Text(
                text = "$month $year",
                fontFamily = FontName,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = DarkSapphire)
        }
    }
}

@Composable
fun ListCategoryItems(
    text:String,
    imageVector: ImageVector,
    color: Color,
    amount:Long,
    totalAmount:Long,
    isExpense:Boolean
){
    Card(shape = CircleShape,
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .background(color = Color.Transparent)
                .fillMaxWidth(),

        ) {

            Box(modifier = Modifier
                .size(36.dp)
                .wrapContentSize()
                .background(color, RoundedCornerShape(100.dp))
            ){
                IconButton(
                    modifier = Modifier
                        .wrapContentSize(),
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        modifier = Modifier.size(23.dp),
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .fillMaxWidth()
                            .weight(1f),
                        text = text,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = DarkSapphire
                    )

                    Text(
                        text = "Rs. ${amount.toDouble()}",
                        color = if(isExpense) Color.Red else Green,
                        fontSize = 13.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { (amount.div(totalAmount.toFloat())) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 15.dp, top = 2.dp, end = 5.dp)
                            .height(7.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        color = color,
                        trackColor = LightestSapphire,
                    )

                    Text(
                        modifier = Modifier
                            .width(42.dp),
                        text = "${((amount.toFloat() / totalAmount.toFloat()) * 100).toInt()} %",
                        color = DarkSapphire,
                        fontSize = 12.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}


@Composable
fun ListTransactionItems(
    text:String,
    imageVector: ImageVector,
    color: Color,
    type:String,
    amount:Long,
    time:String,
    onClick: () -> Unit
){
    Card(
        shape = CircleShape,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() },
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 3.dp, bottom = 3.dp)
                .background(color = Color.Transparent)
                .fillMaxWidth(),
            ) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .wrapContentSize()
                    .background(color, RoundedCornerShape(100.dp))
            ) {
                IconButton(
                    modifier = Modifier
                        .wrapContentSize(),
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        modifier = Modifier.size(23.dp),
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .fillMaxWidth()
                            .weight(1f),
                        text = text,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = DarkSapphire
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Rs. ${amount.toDouble()}",
                            color = if (type == "expense") Color.Red else Green,
                            fontSize = 13.sp,
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "$time",
                            color = DarkSapphire,
                            fontSize = 12.sp,
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal
                        )
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateTransaction(
    onClick: () -> Unit,
    transactionsData: TransactionsData
){
    val context = LocalContext.current
    var name = transactionsData.name
    val text =
        remember {
            mutableStateOf(
                TextFieldValue(
                    text = if(transactionsData.amount.toString() == "0") "" else transactionsData.amount.toString(),
                    selection = TextRange(transactionsData.amount.toString().length)
                )
            )
        }
    val categoryIndex = remember { mutableIntStateOf(-1) }
    val categoryId = remember { mutableIntStateOf(transactionsData.categoryId) }
    val keyId = transactionsData.id
    val type = transactionsData.type
    val selectedItemIndex = remember { mutableIntStateOf(if(type.isEmpty() || type == "expense") 0 else 1) }
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = BringIntoViewRequester()
    val focusManager = LocalFocusManager.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val db = SqLiteDB(context)

    val view = LocalView.current
    KeyboardDetection(view = view) {
        onClick()
    }

    val focusRequester = remember{ FocusRequester() }
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .background(color = ExtraDarkTransparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        ) {
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    modifier = Modifier.padding(10.dp),
                    divider = {},
                    selectedTabIndex = 1,
                    indicator = { tabPosition ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .tabIndicatorOffset(tabPosition[1])
                                .height(0.dp)
                        )
                    }
                ) {
                    Tab(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (selectedItemIndex.intValue == 0) DarkSapphire else Color.Transparent,
                                RoundedCornerShape(50.dp)
                            )
                            .clip(CircleShape),
                        selected = false,
                        onClick = {
                            selectedItemIndex.intValue = 0
                        }
                    ) {
                        Text(
                            text = "Expense",
                            color = if (selectedItemIndex.intValue == 0) Color.White else DarkSapphire
                        )
                    }
                    Tab(
                        modifier = Modifier
                            .background(
                                color = if (selectedItemIndex.intValue == 1) DarkSapphire else Color.Transparent,
                                RoundedCornerShape(50.dp)
                            )
                            .clip(CircleShape),
                        selected = true,
                        onClick = {
                            scope.launch {
                                selectedItemIndex.intValue = 1
                            }
                        }
                    ) {
                        Text(
                            text = "Income",
                            color = if (selectedItemIndex.intValue == 1) Color.White else DarkSapphire
                        )
                    }
                }


                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp, end = 50.dp)
                        .focusRequester(focusRequester)
                        .onFocusEvent {
                            if (it.isFocused) {
                                scope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        },
                    value = text.value,
                    onValueChange = { newtext ->
                        text.value = newtext
                    },
                    textStyle = TextStyle.Default.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedTextColor = DarkSapphire,
                        focusedTextColor = DarkSapphire,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = LightAliceBlue,
                        unfocusedIndicatorColor = LightAliceBlue
                    ),
                    placeholder = {
                        Text(
                            text = "Rs. 0",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.LightGray,
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),

                    keyboardActions = KeyboardActions(onDone = {

                        if (categoryIndex.intValue == -1) {
                            Toast.makeText(context, "Choose the category", Toast.LENGTH_SHORT)
                                .show()
                        } else if (text.value.text.isEmpty()) {
                            Toast.makeText(context, "Textfield cannot be empty", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val regex = "^\\d+\\.?\\d+\$".toRegex()

                            if (keyId != -1) {
                                db.updateParticularTransaction(keyId, categoryId.intValue,text.value.text.toInt())

                                focusManager.clearFocus()
                            } else if (regex.matches(text.value.text)) {
                                db.insertTransaction(
                                    categoryId.intValue,
                                    text.value.text.toInt(),
                                    dateFormatter.format(LocalDate.now()),
                                    timeFormatter.format(LocalTime.now())
                                )

                                focusManager.clearFocus()
                            } else {
                                Toast.makeText(context, "Input must be valid", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    })
                )


                Spacer(modifier = Modifier.padding(top = 20.dp))

                val list: List<CategoriesData> = if (selectedItemIndex.intValue == 0) {
                    expensesListData()
                } else {
                    incomeListData()
                }



                key(list) {
                    categoryIndex.intValue = -1
                    LazyRow(
                        modifier = Modifier
                    )
                    {
                        items(list.size) { index ->
                            val categoriesData = list[index]

                            if (name == categoriesData.text) {
                                categoryIndex.intValue = index
                            }

                            Spacer(modifier = Modifier.padding(3.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        color = if (categoryIndex.intValue == index) Color(
                                            categoriesData.color
                                        ) else LightAliceBlue,
                                    )
                            ) {
                                ListItem(
                                    useIndication = false,
                                    padding = 5.dp,
                                    size = 22,
                                    text = categoriesData.text,
                                    textColor = if (categoryIndex.intValue == index) Color.White else DarkSapphire,
                                    fontSize = 13.sp,
                                    imageVector = ImageVector.vectorResource(categoriesData.imageVector),
                                    color = Color(categoriesData.color),
                                    onClick = {
                                        name = categoriesData.text
                                        categoryIndex.intValue = index
                                        categoryId.intValue = categoriesData.id
                                    },
                                    onLongClick = {}
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .bringIntoViewRequester(bringIntoViewRequester)
                )
            }
        }
    }
}


fun weeklyCalendar(calendar: Calendar): String {
    val tempCalendar = Calendar.getInstance()
    tempCalendar.timeInMillis = calendar.timeInMillis

    val dateFormat = SimpleDateFormat("dd MMM, YYYY", Locale.getDefault())

    tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val firstDayOfWeek = dateFormat.format(tempCalendar.timeInMillis)

    tempCalendar.add(Calendar.DAY_OF_MONTH, 6)

    val lastDayOfWeek = dateFormat.format(tempCalendar.timeInMillis)

    return "$firstDayOfWeek - $lastDayOfWeek"
}

@Preview
@Composable
fun Test(){
//    CreateTransaction(focusRequester = FocusRequester(),remember{ mutableStateOf(true) })
//    ScaffoldImplementation(
//        drawerState = DrawerState(initialValue = DrawerValue.Closed),
//        selectedItemIndex = 0,
//        rememberNavController()
//    )
}

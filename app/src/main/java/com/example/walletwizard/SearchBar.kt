package com.example.walletwizard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.walletwizard.ui.theme.LightModeDarkSapphire

//@Preview
//@Composable
//fun PreviewSearchBar(){
//    SearchBar {}
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onClick: () -> Unit,
    onSearch: (String) -> Unit,
    newtext: MutableState<String> = remember {
        mutableStateOf("")
    },
    onQueryChange: (String) -> Unit
){
    val isActive = remember{ mutableStateOf(false) }
    val paddingValues = remember { mutableIntStateOf(7) }


    androidx.compose.material3.SearchBar(
        colors = SearchBarDefaults.colors(
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = LightModeDarkSapphire,
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValues.intValue.dp),
        query = newtext.value,
        onQueryChange = onQueryChange,
        onSearch = {
            isActive.value = false
            paddingValues.intValue = 7
            onSearch(it)
        },
        active = isActive.value,
        onActiveChange = { active ->
            isActive.value = active
            if (active) {
                paddingValues.intValue = 0
            }
        },
        placeholder = {
            Text(text = "Search")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = LightModeDarkSapphire
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    // TODO
                    if (newtext.value.isNotEmpty()) {
                       newtext.value = ""
                    } else {
                        isActive.value = false
                        onClick()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = LightModeDarkSapphire
                )
            }
        }
    ) {
        val list = HomeScreen.categoryList
        val transactionList = HomeScreen.transactionList


        if(HomeScreen.TYPE == 0) {
            val totalExpense = HomeScreen.totalExpense
            val totalIncome = HomeScreen.totalIncome
            val amountType = HomeScreen.amountType

            LazyColumn {
                item {
                    list.forEachIndexed { index, transactionsData ->
                        if (transactionsData.name.contains(newtext.value,ignoreCase = true)) {
                            ListCategoryItems(
                                text = transactionsData.name,
                                imageVector = ImageVector.vectorResource(transactionsData.imageVector),
                                color = Color(transactionsData.color),
                                amount = transactionsData.amount.toString(),
                                totalAmount = if (amountType == "expense") totalExpense.toString() else totalIncome.toString(),
                                isExpense = amountType == "expense"
                            )
                        }
                    }
                }
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                transactionList.entries.forEachIndexed { index, (date, transactionList) ->
                    item{
                        if(index != 0) {
                            HorizontalDivider(modifier = Modifier.padding(6.dp))
                        }
                        LazyColumnHeader(date)
                    }
                    transactionList.forEachIndexed { index, transactionsData ->
                        item {
                            if(transactionsData.name.contains(newtext.value,ignoreCase = true)) {
                                ListTransactionItems(
                                    text = transactionsData.name,
                                    imageVector = ImageVector.vectorResource(transactionsData.imageVector),
                                    color = Color(transactionsData.color),
                                    type = transactionsData.type,
                                    amount = transactionsData.amount.toDouble().toString(),
                                    time = transactionsData.timestamp,
                                    onClick = {
//                                    onTransactionItemClicked(transactionList[index])
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
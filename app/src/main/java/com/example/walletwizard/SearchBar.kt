package com.example.walletwizard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.walletwizard.ui.theme.DarkSapphire

@Preview
@Composable
fun PreviewSearchBar(){
    SearchBar {}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onClick: () -> Unit
){
    val text = remember{ mutableStateOf("") }
    val isActive = remember{ mutableStateOf(false) }
    val paddingValues = remember { mutableIntStateOf(7) }


    androidx.compose.material3.SearchBar(
        colors = SearchBarDefaults.colors(
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = DarkSapphire,
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValues.intValue.dp),
        query = text.value,
        onQueryChange = { newValue ->
            text.value = newValue
        },
        onSearch = {
            isActive.value = false
            paddingValues.intValue = 7
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
                tint = DarkSapphire
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    // TODO
                    if (text.value.isNotEmpty()) {
                        text.value = ""
                    } else {
                        isActive.value = false
                        onClick()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = DarkSapphire
                )
            }
        }
    ) {

    }
}
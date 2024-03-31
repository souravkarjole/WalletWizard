package com.example.walletwizard

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.walletwizard.ui.theme.LightModeDarkSapphire
import com.example.walletwizard.ui.theme.FontName


@Composable
fun DialogBox(
    text: String,
    onDismissRequest: () -> Unit,
    onConfirmationRequest: () -> Unit
){
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    text = "Delete",
                    color = LightModeDarkSapphire,
                    fontSize = 20.sp,
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = text,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        modifier = Modifier
                            .padding(13.dp)
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null
                            ) {
                                onConfirmationRequest()
                            },
                        text = "Confirm",
                        color = LightModeDarkSapphire,
                        fontSize = 17.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        modifier = Modifier
                            .padding(13.dp)
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null
                            ) {
                                onDismissRequest()
                            },
                        text = "Cancel",
                        color = LightModeDarkSapphire,
                        fontSize = 17.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}


@Composable
fun UploadDialogBox(
    state:MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
    progress:Double,
    onDismissRequest: () -> Unit,
    onConfirmationRequest: (phoneNumber:String) -> Unit
){
    val phoneNumber = remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Phone Number",
                    color = LightModeDarkSapphire,
                    fontSize = 18.sp,
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.padding(3.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    value = phoneNumber.value,
                    onValueChange = {
                        phoneNumber.value = it
                    },
                    label = {Text(text = "phone no.")},
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Text(
                    modifier = Modifier.padding(10.dp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontFamily = FontName,
                    fontWeight = FontWeight.Normal,
                    text = "Note: Phone number is required to save your data in Secure way."
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        val regex = "^\\d{10}\$".toRegex()
                        if(regex.matches(phoneNumber.value)){
                            onConfirmationRequest(phoneNumber.value)
                            state.value = true
                        }else{
                            Toast.makeText(context,"Invalid phone number!",Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(
                        text = if(state.value) "Uploading..." else "Confirm",
                        fontSize = 15.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
                }
                if(state.value) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.scale(0.6f))
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = "Upload Progress: ${progress.toInt()}%",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal
                        )

                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun df(){
    UploadDialogBox(
        progress = 50.55,
        onDismissRequest = { /*TODO*/ },
        onConfirmationRequest = {}
    )
}
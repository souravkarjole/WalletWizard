package com.example.walletwizard

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.walletwizard.ui.theme.DarkSapphire
import com.example.walletwizard.ui.theme.FontName
import com.example.walletwizard.ui.theme.LightAliceBlue
import com.example.walletwizard.ui.theme.LightSapphire
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.File
import java.sql.SQLException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = DarkSapphire,
                        fontSize = 20.sp,
                        fontFamily = FontName,
                        fontWeight = FontWeight.Normal
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = DarkSapphire
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val select = remember { mutableIntStateOf(0)}
        val isChecked = remember{ mutableStateOf(false) }
        val showBottomSheet = remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
        val context = LocalContext.current

        Surface(color = LightSapphire) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Data",
                            fontSize = 16.sp,
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal,
                            color = DarkSapphire
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp)
                        ) {
                            Item(img = R.drawable.create_data_backup, text = "Create data backup") {
                                showBottomSheet.value = true
                                select.intValue = 0
                            }
                            Item(img = R.drawable.data_backup, text = "Restore data"){
                                showBottomSheet.value = true
                                select.intValue = 1
                            }
                            Item(img = R.drawable.delete_cloud, text = "Delete data"){
                                showBottomSheet.value = true
                                select.intValue = 2
                            }
                        }

                        Spacer(modifier = Modifier.padding(10.dp))
                        Text(
                            text = "General",
                            fontSize = 16.sp,
                            fontFamily = FontName,
                            fontWeight = FontWeight.Normal,
                            color = DarkSapphire
                        )

                        Column(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth()
                        ) {
                            Item(img = R.drawable.report, text = "Report"){
                                generateReport(context = context)
                            }
                            Row {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Item(img = R.drawable.moon, text = "Dark theme"){

                                    }
                                    Switch(
                                        modifier = Modifier
                                            .scale(0.9f),
                                        checked = isChecked.value,
                                        onCheckedChange = {
                                            isChecked.value = it
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if(showBottomSheet.value){
                        ModalBottomSheet(
                            dragHandle = {
                                Box(modifier = Modifier.padding(20.dp)){
                                    Text(
                                        text = if(select.intValue == 0) "Create data backup" else if(select.intValue == 1) "Restore data" else "Delete data",
                                        color = DarkSapphire,
                                        fontSize = 20.sp,
                                        fontFamily = FontName,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            },
                            sheetState = sheetState,
                            onDismissRequest = { showBottomSheet.value = false }
                        ) {
                            BottomSheetContents(select.intValue){
                                showBottomSheet.value = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomSheetContents(
    actionType:Int,
    onClick: () -> Unit
){
    val phoneNumberState = remember { mutableStateOf("") }
    val sendingOtp = remember { mutableStateOf(false) }
    val otpTextList = remember { List(6) { mutableStateOf("") } }


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp, bottom = 55.dp)) {
        if(!sendingOtp.value) {
            PhoneNumberTextField(phoneNumberState.value) {
                phoneNumberState.value = it
            }
        }
        if(sendingOtp.value) {
            OtpTextField(otpTextList){ index, it->
                otpTextList[index].value = it.take(1)
            }
        }

        NoteText(actionType)

        ActionButton(
            actionType,
            phoneNumberState,
            otpTextList,
            sendingOtp,
            onClick
        )
    }
}


@Composable
fun SingleCharacterTextField(
    text:String,
    onValueChange: (String) -> Unit
){
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        value = text, onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.NumberPassword
        ),
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = DarkSapphire
        ),
        singleLine = true
    )
}
@Composable
fun PhoneNumberTextField(text:String,onValueChange: (String) -> Unit){
    Text(
        text = "Phone Number",
        color = DarkSapphire,
        fontSize = 18.sp,
        fontFamily = FontName,
        fontWeight = FontWeight.Normal
    )
    Spacer(modifier = Modifier.padding(3.dp))
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        value = text,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(
            color = DarkSapphire
        ),
        label = {Text(text = "phone no.")},
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )

    Spacer(modifier = Modifier.padding(top = 10.dp))
}
@Composable
fun OtpTextField(textList:List<MutableState<String>>,onValueChange: (Int,String) -> Unit){
    Text(
        text = "OTP",
        color = DarkSapphire,
        fontSize = 18.sp,
        fontFamily = FontName,
        fontWeight = FontWeight.Normal
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        repeat(6){index->
            Box(
                modifier = Modifier.weight(1f)
            ) {
                SingleCharacterTextField(
                    text = textList[index].value,
                    onValueChange = {
                        onValueChange(index,it)
                    }
                )
            }
            Spacer(modifier = Modifier.width(7.dp))
        }
    }
}
@Composable
fun NoteText(actionType:Int){
    val noteText = buildAnnotatedString {
        append("Note: Phone number is required to ")
        withStyle(
            style = SpanStyle(color = if (actionType == 0 || actionType == 1) Color(0xFF187A2E) else Color.Red)
        ) {
            append(if (actionType == 0) "save" else if (actionType == 1) "restore" else "delete")
        }
        append(" your data in a secure way.")
    }

    Text(
        modifier = Modifier.padding(10.dp),
        color = Color.Black,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        fontFamily = FontName,
        fontWeight = FontWeight.Normal,
        text = noteText
    )
}
@Composable
fun ActionButton(
    actionType: Int,
    phoneNumberState: MutableState<String>,
    otpTextList: List<MutableState<String>>,
    sendingOtp: MutableState<Boolean>,
    onClick: () -> Unit){

    var state = remember { mutableStateOf(false) }
    var verificationId = remember { mutableStateOf("") }
    val isAnyTextFieldEmpty = otpTextList.any { it.value.isBlank() }
    var btnText = "Sending OTP..."
    val context = LocalContext.current
    val databaseFile = context.getDatabasePath("database.db")
    val storageReference = Firebase.storage.reference.child("databases/${phoneNumberState.value}.db")

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(10.dp),
        onClick = {
            val regex = "^\\d{10}\$".toRegex()
            if(regex.matches(phoneNumberState.value)){
                if (!sendingOtp.value) {
                    sendingOtp.value = true
                    phoneNumberAuthentication(
                        number = phoneNumberState.value,
                        context = context,
                        onSuccess = {
                            verificationId.value = it
                        },
                        onFailed = {
                            sendingOtp.value = false
                        }
                    )
                } else if (!isAnyTextFieldEmpty) {
                    val otp = otpTextList.joinToString(separator = "") { it.value }

                    verifyOtp(
                        p0 = verificationId.value,
                        code = otp,
                        onSuccess = {
                            state.value = true
                        },
                        onFailed = {
                            Toast.makeText(context, "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )

                } else {
                    Toast.makeText(
                        context,
                        "Please enter 6 digit otp",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(context,"Invalid phone number!",Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        if(state.value || (sendingOtp.value && verificationId.value.isEmpty())) {
            if(state.value && actionType == 0){
                btnText = "Uploading..."
            }
            if(state.value && actionType == 1){
                btnText = "Restoring..."
            }
            if(state.value && actionType == 2){
                btnText = "Deleting..."
            }
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = if(state.value || (sendingOtp.value && verificationId.value.isEmpty())) btnText else "Confirm",
            fontSize = 15.sp,
            fontFamily = FontName,
            fontWeight = FontWeight.Normal
        )
    }

    if(state.value && actionType == 0){
        UploadData(
            storageReference,
            context,
            databaseFile,
            onClick
        )
    }

    if(state.value && actionType == 1){
        RestoreData(
            storageReference,
            context,
            databaseFile,
            onClick
        )
    }

    if(state.value && actionType == 2){
        DeleteData(
            storageReference,
            context,
            databaseFile,
            onClick
        )
    }
}

@Composable
fun UploadData(
    storageReference:StorageReference,
    context:Context,
    databaseFile:File,
    onClick: () -> Unit
){
    val fileUri = Uri.fromFile(databaseFile)

    storageReference.putFile(fileUri)
        .addOnSuccessListener {
            onClick()
            Toast.makeText(
                context,
                "Data backup created successfully",
                Toast.LENGTH_LONG
            ).show()
        }
        .addOnFailureListener {
            onClick()
            Toast.makeText(
                context,
                "Failed to create data backup",
                Toast.LENGTH_SHORT
            ).show()
        }
}
@Composable
fun RestoreData(
    storageReference:StorageReference,
    context:Context,
    databaseFile:File,
    onClick: () -> Unit
){
    val fileUri = Uri.fromFile(databaseFile)

    storageReference.getFile(fileUri)
        .addOnSuccessListener {
            onClick()
            try {
                val db = SQLiteDatabase.openOrCreateDatabase(databaseFile,null)
                db.close()
                Toast.makeText(
                    context,
                    "Data backup restored successfully",
                    Toast.LENGTH_LONG
                ).show()
            }catch (e: SQLiteException){
                Toast.makeText(
                    context,
                    "Error restoring data backup ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .addOnFailureListener {
            onClick()
            Toast.makeText(
                context,
                "Failed to restore data",
                Toast.LENGTH_SHORT
            ).show()
        }
}
@Composable
fun DeleteData(
    storageReference:StorageReference,
    context:Context,
    databaseFile:File,
    onClick: () -> Unit
){
    storageReference.delete()
        .addOnSuccessListener {
            onClick()
            try {
                val db = SQLiteDatabase.openOrCreateDatabase(databaseFile,null)
                db.close()
                Toast.makeText(
                    context,
                    "Data deleted successfully",
                    Toast.LENGTH_LONG
                ).show()
            }catch (e: SQLiteException){
                Toast.makeText(
                    context,
                    "Error deleting data ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .addOnFailureListener {
            onClick()
            Toast.makeText(
                context,
                "Failed to delete data",
                Toast.LENGTH_SHORT
            ).show()
        }
}


@Composable
fun Item(img:Int,text:String,onClick: () -> Unit){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .clip(CircleShape)
        .clickable { onClick() },
        color = Color.Transparent
        ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(img),
                contentDescription = null,
                tint = DarkSapphire
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = text,
                fontSize = 17.sp,
                fontFamily = FontName,
                fontWeight = FontWeight.Normal,
                color = DarkSapphire
            )
        }
    }
}


@Preview
@Composable
fun PreviewSettings(){
    Settings(navController = rememberNavController())
}
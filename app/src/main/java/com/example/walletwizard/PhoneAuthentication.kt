package com.example.walletwizard

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


fun phoneNumberAuthentication(
    number: String,
    context:Context,
    onSuccess:  (String) -> Unit,
    onFailed: () -> Unit
){
    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
        .setPhoneNumber("+91$number")
        .setTimeout(60L,TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(context,"Verification failed $p0",Toast.LENGTH_SHORT).show()
                onFailed()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                Toast.makeText(context,"OTP sent to your phone",Toast.LENGTH_SHORT).show()
                onSuccess(p0)
            }
        } )
        .build()


    PhoneAuthProvider.verifyPhoneNumber(options)
}


fun verifyOtp(
    p0:String,
    code:String,
    onSuccess: () -> Unit,
    onFailed: () -> Unit
){
    FirebaseAuth.getInstance().signInWithCredential(PhoneAuthProvider.getCredential(p0,code))
        .addOnCompleteListener {task->

            if(task.isSuccessful){
                onSuccess()
            }else {
                onFailed()
            }
        }

}
package com.example.walletwizard.Model


data class TransactionsData(
    val id:Int = -1,
    val categoryId:Int,
    val name:String,
    val amount:Int,
    val type:String = "",
    val date:String = "",
    val timestamp:String = "",
    val imageVector: Int,
    val color:Long
){
    constructor() : this(
        id = -1,
        categoryId = -1,
        name = "",
        amount = 0,
        imageVector = 0,
        color = 0
    )

}

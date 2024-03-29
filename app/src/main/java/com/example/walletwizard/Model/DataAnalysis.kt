package com.example.walletwizard.Model

data class DailyMonthlyYearlyTransactions(
    var totalExpense:Int,
    var totalIncome:Int
)

data class WeeklyTransactions(
    val startDate:String,
    val endDate:String,
    val totalExpense: Int,
    val totalIncome: Int
)

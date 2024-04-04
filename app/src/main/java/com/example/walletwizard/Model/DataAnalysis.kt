package com.example.walletwizard.Model

data class DailyMonthlyYearlyTransactions(
    var totalExpense:Double,
    var totalIncome:Double
)

data class WeeklyTransactions(
    val startDate:String,
    val endDate:String,
    val totalExpense: Double,
    val totalIncome: Double
)

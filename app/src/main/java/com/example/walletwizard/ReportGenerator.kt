package com.example.walletwizard

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.walletwizard.Database.SqLiteDB
import com.example.walletwizard.Model.DailyMonthlyYearlyTransactions
import com.itextpdf.io.font.FontConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.AreaBreakType
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.itextpdf.layout.property.VerticalAlignment
import java.io.File
import java.io.FileOutputStream


var items = listOf(
    "Introduction",
    "Overview",
    "Data Summary",
    "Daily Analysis",
    "Weekly Analysis",
    "Monthly Analysis",
    "Yearly Analysis"
)

fun generateReport(context:Context){
    val db = SqLiteDB(context)

    val pdfPath =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        }else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) // deprecated in Android sdk 29 i.e V 10
        }

    val file = File(pdfPath,"WalletWizardReport.pdf")
    val writer = PdfWriter(FileOutputStream(file))
    val pdf = PdfDocument(writer)

    val pageSize = PageSize.A4
    val document = Document(pdf,pageSize)


    addTitle(document,"WalletWizard\nReport")

    addBulletPoint(document,0)
    addContent(document,
        "This report provides an in-depth analysis of your transactions recorded in the Wallet Wizard database. " +
                "It aims to offer insights into income and expense patterns, category trends based on the available data."
    )

    addBulletPoint(document,1)
    addContent(document,
        "1. Categories: Stores information about different spending categories.\n" +
                "2. Transactions: Records individual financial transactions, including category, amount, and date.")

    addBulletPoint(document,2)
    val totalCategory = db.getTotalCategories()
    val totalTransactions = db.getTotalTransactions()
    val expensePercentage = (db.getExpenseCategories().toDouble() / totalCategory) * 100
    val incomePercentage = (db.getIncomeCategories().toDouble() / totalCategory) * 100
    addContent(document,
        "1. Total Categories: ${totalCategory}\n" +
                "2. Total Transactions: ${totalTransactions}\n" +
                "3. Expense Categories: ${expensePercentage.toInt()}%\n" +
                "4. Income Categories: ${incomePercentage.toInt()}%")

    // Table Daily
    document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
    addBulletPoint(document,3)
    addContent(document,"")
    addContent(document,"")
    addTableForDaily(document,context)

    // Table Weekly
    document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
    addBulletPoint(document,4)
    addContent(document,"")
    addContent(document,"")
    addTableForWeekly(document,context)

    // Table Monthly
    document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
    addBulletPoint(document,5)
    addContent(document,"")
    addContent(document,"")
    addTableForMonthly(document,context)

    // Table Yearly
    document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
    addBulletPoint(document,6)
    addContent(document,"")
    addContent(document,"")
    addTableForYearly(document,context)

    document.close()
    Toast.makeText(context,"Report generated",Toast.LENGTH_LONG).show()
}

fun addTitle(document:Document,title:String){

    val textParagraph = Paragraph(title)
        .setTextAlignment(TextAlignment.CENTER)
        .setVerticalAlignment(VerticalAlignment.MIDDLE)
        .setFontSize(24f)

    document.add(textParagraph)
}

fun addBulletPoint(document: Document,index:Int){
    val preContentParagraph = Paragraph("\n")
        .setFontSize(14f) // Set your desired font size here
    document.add(preContentParagraph)

    val textParagraph = Paragraph()
        .add("• ${items[index]}")
        .setTextAlignment(TextAlignment.LEFT)
        .setFontSize(20f)

    document.add(textParagraph)
}
fun addContent(document: Document,content:String,marginLeft:Float = 14f){
    val textParagraph = Paragraph(content)
        .setTextAlignment(TextAlignment.JUSTIFIED)
        .setMarginLeft(marginLeft)
        .setFontSize(15f)

    document.add(textParagraph)
}
fun createCellHeader(text:String):Cell{
    return Cell().add(Paragraph(text)).setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(12f).setVerticalAlignment(VerticalAlignment.MIDDLE)
}
fun createCell(text:String):Cell{
    return Cell().add(Paragraph(text)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE)
}



fun addTableForDaily(document: Document,context:Context){
    val db = SqLiteDB(context)

    val map = db.getDailyTotalIncomeAndExpense()

    val table = Table(4)
        .setVerticalAlignment(VerticalAlignment.MIDDLE)
        .setHorizontalAlignment(HorizontalAlignment.CENTER)
        .setWidth(400f)

    table.addHeaderCell(createCellHeader("Sr.No"))
    table.addHeaderCell(createCellHeader("Date"))
    table.addHeaderCell(createCellHeader("Total Expense"))
    table.addHeaderCell(createCellHeader("Total Income"))

    var index = 1
    for(m in map) {
        table.addCell(createCell("$index"))
        table.addCell(createCell(m.key))
        table.addCell(createCell("${m.value.totalExpense}"))
        table.addCell(createCell("${m.value.totalIncome}"))
        index++
    }

    document.add(table)
}
fun addTableForWeekly(document: Document,context: Context){
    val db = SqLiteDB(context)

    val table = Table(5)
        .setVerticalAlignment(VerticalAlignment.MIDDLE)
        .setHorizontalAlignment(HorizontalAlignment.CENTER)
        .setWidth(400f)

    val map = db.getWeeklyTotalIncomeAndExpense()

    table.addHeaderCell(createCellHeader("Sr.No"))
    table.addHeaderCell(createCellHeader("Start Date"))
    table.addHeaderCell(createCellHeader("End Date"))
    table.addHeaderCell(createCellHeader("Total Expense"))
    table.addHeaderCell(createCellHeader("Total Income"))

    var index = 1
    for(m in map) {
        val key = m.key.split(' ')
        table.addCell(createCell("$index"))
        table.addCell(createCell(key[0]))
        table.addCell(createCell(key[1]))
        table.addCell(createCell("${m.value.totalExpense}"))
        table.addCell(createCell("${m.value.totalIncome}"))
        index++
    }

    document.add(table)
}
fun addTableForMonthly(document: Document,context: Context){
    val db = SqLiteDB(context)

    val map = db.getMonthlyTotalIncomeAndExpense()
    val table = Table(4)
        .setVerticalAlignment(VerticalAlignment.MIDDLE)
        .setHorizontalAlignment(HorizontalAlignment.CENTER)
        .setWidth(400f)

    table.addHeaderCell(createCellHeader("Sr.No"))
    table.addHeaderCell(createCellHeader("Month"))
    table.addHeaderCell(createCellHeader("Total Expense"))
    table.addHeaderCell(createCellHeader("Total Income"))

    var index = 1
    for(m in map) {
        table.addCell(createCell("$index"))
        table.addCell(createCell(m.key))
        table.addCell(createCell("${m.value.totalExpense}"))
        table.addCell(createCell("${m.value.totalIncome}"))
        index++
    }

    document.add(table)
}
fun addTableForYearly(document: Document,context: Context){
    val db = SqLiteDB(context)

    val map = db.getYearlyTotalIncomeAndExpense()
    val table = Table(4)
        .setVerticalAlignment(VerticalAlignment.MIDDLE)
        .setHorizontalAlignment(HorizontalAlignment.CENTER)
        .setWidth(400f)

    table.addHeaderCell(createCellHeader("Sr.No"))
    table.addHeaderCell(createCellHeader("Year"))
    table.addHeaderCell(createCellHeader("Total Expense"))
    table.addHeaderCell(createCellHeader("Total Income"))

    var index = 1
    for(m in map) {
        table.addCell(createCell("$index"))
        table.addCell(createCell(m.key))
        table.addCell(createCell("${m.value.totalExpense}"))
        table.addCell(createCell("${m.value.totalIncome}"))
        index++
    }

    document.add(table)
}

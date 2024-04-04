package com.example.walletwizard.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.walletwizard.Model.CategoriesData
import com.example.walletwizard.Model.DailyMonthlyYearlyTransactions
import com.example.walletwizard.Model.TransactionsData
import com.example.walletwizard.R
import com.example.walletwizard.ui.theme.Cerise
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

class SqLiteDB(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
    companion object{
        private const val DATABASE_NAME = "database.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CATEGORIES = "Categories"
        private const val TABLE_TRANSACTIONS = "TRANSACTIONS"

        private const val COLUMN_KEY_ID = "id"

        // categories table column name
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_IMAGE_VECTOR_ID = "image_vector_id"
        private const val COLUMN_COLOR = "color"

        // transactions table column name
        private const val COLUMN_CATEGORY_ID = "category_id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIMESTAMP = "timestamp"

        // Create Categories table query
        private const val CREATE_TABLE_CATEGORIES = "CREATE TABLE $TABLE_CATEGORIES (" +
                "$COLUMN_KEY_ID INTEGER PRIMARY KEY," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_TYPE TEXT," +
                "$COLUMN_IMAGE_VECTOR_ID INTEGER," +
                "$COLUMN_COLOR LONG)"

        // Create Transactions table query
        private const val CREATE_TABLE_TRANSACTIONS = "CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$COLUMN_KEY_ID INTEGER PRIMARY KEY," +
                "$COLUMN_CATEGORY_ID INTEGER," +
                "$COLUMN_AMOUNT DOUBLE," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_TIMESTAMP TEXT," +
                "FOREIGN KEY($COLUMN_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_KEY_ID))"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_CATEGORIES)
        db?.execSQL(CREATE_TABLE_TRANSACTIONS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")

        onCreate(db)
    }


    fun insertCategory(name:String,type:String,imageVectorID:Int,color:Long){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NAME,name)
        values.put(COLUMN_TYPE, type.lowercase())
        values.put(COLUMN_IMAGE_VECTOR_ID,imageVectorID)
        values.put(COLUMN_COLOR,color)
        db.insert(TABLE_CATEGORIES,null,values)
    }

    fun insertTransaction(id:Int,amount:Double,date:String,time:String){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_CATEGORY_ID,id)
        values.put(COLUMN_AMOUNT,amount)
        values.put(COLUMN_DATE,date)
        values.put(COLUMN_TIMESTAMP,time)
        db.insert(TABLE_TRANSACTIONS,null,values)
    }


    fun getCategories(categoryType:String): List<CategoriesData>{
        val list = mutableListOf<CategoriesData>()
        val selectQuery = "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_TYPE = '$categoryType'"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i ->
            val idIndex = i.getColumnIndex(COLUMN_KEY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)

            if(i.moveToFirst()){
                do {
                    val id = i.getInt(idIndex)
                    val name = i.getString(nameIndex)
                    val type = i.getString(typeIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)


                    val categoriesData = CategoriesData(id = id,text = name,type = type,imageVector = imageVectorID, color = color)
                    list.add(categoriesData)
                }while (i.moveToNext())
            }
        }

        return list
    }
    fun getParticularCategory(id:Int): CategoriesData{
        val selectQuery = "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_KEY_ID = '$id'"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i ->
            if (i.moveToFirst()) {
                val idIndex = i.getColumnIndex(COLUMN_KEY_ID)
                val nameIndex = i.getColumnIndex(COLUMN_NAME)
                val typeIndex = i.getColumnIndex(COLUMN_TYPE)
                val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
                val colorIndex = i.getColumnIndex(COLUMN_COLOR)

                val categoryId = i.getInt(idIndex)
                val name = i.getString(nameIndex)
                val type = i.getString(typeIndex)
                val imageVectorID = i.getInt(imageVectorIndex)
                val color = i.getLong(colorIndex)

                return CategoriesData(
                    id = categoryId,
                    text = name,
                    type = type,
                    imageVector = imageVectorID,
                    color = color
                )
            }else{
                return CategoriesData(-1,"","", R.drawable.cocktail, Cerise)
            }
        }
    }


    fun getMonthlyTransactions(type:String,strDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                          "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_NAME," +
                          "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_COLOR," +
                          "SUM($TABLE_TRANSACTIONS.$COLUMN_AMOUNT) AS total_amount " +
                          "FROM $TABLE_TRANSACTIONS " +
                          "INNER JOIN $TABLE_CATEGORIES " +
                          "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                          "WHERE $TABLE_CATEGORIES.$COLUMN_TYPE = '$type' " +
                          "AND $TABLE_TRANSACTIONS.$COLUMN_DATE LIKE '$strDate-%' " +
                          "GROUP BY " +
                          "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_NAME," +
                          "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_COLOR"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)
            val amountIndex = i.getColumnIndex("total_amount")

            if(i.moveToFirst()){
                do{
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(categoryId = categoryId,name = name,amount = amount, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }
    fun getYearlyTransactions(type:String,strDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                          "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_NAME," +
                          "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_COLOR," +
                          "SUM($TABLE_TRANSACTIONS.$COLUMN_AMOUNT) AS total_amount " +
                          "FROM $TABLE_TRANSACTIONS " +
                          "INNER JOIN $TABLE_CATEGORIES " +
                          "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                          "WHERE $TABLE_CATEGORIES.$COLUMN_TYPE = '$type' " +
                          "AND $TABLE_TRANSACTIONS.$COLUMN_DATE LIKE '$strDate%' " +
                          "GROUP BY " +
                          "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_NAME," +
                          "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_COLOR"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)
            val amountIndex = i.getColumnIndex("total_amount")

            if(i.moveToFirst()){
                do{
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(categoryId = categoryId,name = name,amount = amount, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }
    fun getDailyTransactions(type:String,strDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                          "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_NAME," +
                          "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_COLOR," +
                          "SUM($TABLE_TRANSACTIONS.$COLUMN_AMOUNT) AS total_amount " +
                          "FROM $TABLE_TRANSACTIONS " +
                          "INNER JOIN $TABLE_CATEGORIES " +
                          "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                          "WHERE $TABLE_CATEGORIES.$COLUMN_TYPE = '$type' " +
                          "AND $TABLE_TRANSACTIONS.$COLUMN_DATE LIKE '%$strDate' " +
                          "GROUP BY " +
                          "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_NAME," +
                          "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                          "$TABLE_CATEGORIES.$COLUMN_COLOR"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)
            val amountIndex = i.getColumnIndex("total_amount")

            if(i.moveToFirst()){
                do{
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(categoryId = categoryId,name = name,amount = amount, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }
    fun getWeeklyTransactions(type:String,startDate:String,endDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                "$TABLE_CATEGORIES.$COLUMN_NAME," +
                "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                "$TABLE_CATEGORIES.$COLUMN_COLOR," +
                "SUM($TABLE_TRANSACTIONS.$COLUMN_AMOUNT) AS total_amount " +
                "FROM $TABLE_TRANSACTIONS " +
                "INNER JOIN $TABLE_CATEGORIES " +
                "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                "WHERE $TABLE_CATEGORIES.$COLUMN_TYPE = '$type' " +
                "AND $TABLE_TRANSACTIONS.$COLUMN_DATE BETWEEN '$startDate' AND '$endDate' " +
                "GROUP BY " +
                "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                "$TABLE_CATEGORIES.$COLUMN_NAME," +
                "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                "$TABLE_CATEGORIES.$COLUMN_COLOR"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->

            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)
            val amountIndex = i.getColumnIndex("total_amount")

            if(i.moveToFirst()){
                do{
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(categoryId = categoryId,name = name,amount = amount, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }


    fun getAllMonthlyTransactions(strDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                "$TABLE_TRANSACTIONS.$COLUMN_KEY_ID," +
                "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                "$TABLE_CATEGORIES.$COLUMN_NAME," +
                "$TABLE_TRANSACTIONS.$COLUMN_AMOUNT," +
                "$TABLE_CATEGORIES.$COLUMN_TYPE," +
                "$TABLE_TRANSACTIONS.$COLUMN_DATE," +
                "$TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP," +
                "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                "$TABLE_CATEGORIES.$COLUMN_COLOR " +
                "FROM $TABLE_TRANSACTIONS " +
                "INNER JOIN $TABLE_CATEGORIES " +
                "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                "WHERE $TABLE_TRANSACTIONS.$COLUMN_DATE LIKE '$strDate-%' " +
                "ORDER BY $TABLE_TRANSACTIONS.$COLUMN_DATE DESC, $TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP DESC"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val idIndex = i.getColumnIndex(COLUMN_KEY_ID)
            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val amountIndex = i.getColumnIndex(COLUMN_AMOUNT)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val dateIndex = i.getColumnIndex(COLUMN_DATE)
            val timeIndex = i.getColumnIndex(COLUMN_TIMESTAMP)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)

            if(i.moveToFirst()){
                do{
                    val id = i.getInt(idIndex)
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val type = i.getString(typeIndex)
                    val date = i.getString(dateIndex)
                    val time = i.getString(timeIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(id = id,categoryId = categoryId,name = name,amount = amount,type = type,date = date, timestamp = time, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }
    fun getAllYearlyTransactions(strDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                "$TABLE_TRANSACTIONS.$COLUMN_KEY_ID," +
                "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                "$TABLE_CATEGORIES.$COLUMN_NAME," +
                "$TABLE_TRANSACTIONS.$COLUMN_AMOUNT," +
                "$TABLE_CATEGORIES.$COLUMN_TYPE," +
                "$TABLE_TRANSACTIONS.$COLUMN_DATE," +
                "$TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP," +
                "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                "$TABLE_CATEGORIES.$COLUMN_COLOR " +
                "FROM $TABLE_TRANSACTIONS " +
                "INNER JOIN $TABLE_CATEGORIES " +
                "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                "WHERE $TABLE_TRANSACTIONS.$COLUMN_DATE LIKE '$strDate%' " +
                "ORDER BY $TABLE_TRANSACTIONS.$COLUMN_DATE DESC, $TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP DESC"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val idIndex = i.getColumnIndex(COLUMN_KEY_ID)
            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val amountIndex = i.getColumnIndex(COLUMN_AMOUNT)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val dateIndex = i.getColumnIndex(COLUMN_DATE)
            val timeIndex = i.getColumnIndex(COLUMN_TIMESTAMP)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)

            if(i.moveToFirst()){
                do{
                    val id = i.getInt(idIndex)
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val type = i.getString(typeIndex)
                    val date = i.getString(dateIndex)
                    val time = i.getString(timeIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(id = id,categoryId = categoryId,name = name,amount = amount,type = type,date = date, timestamp = time, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }
    fun getAllDailyTransactions(strDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                "$TABLE_TRANSACTIONS.$COLUMN_KEY_ID," +
                "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                "$TABLE_CATEGORIES.$COLUMN_NAME," +
                "$TABLE_TRANSACTIONS.$COLUMN_AMOUNT," +
                "$TABLE_CATEGORIES.$COLUMN_TYPE," +
                "$TABLE_TRANSACTIONS.$COLUMN_DATE," +
                "$TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP," +
                "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                "$TABLE_CATEGORIES.$COLUMN_COLOR " +
                "FROM $TABLE_TRANSACTIONS " +
                "INNER JOIN $TABLE_CATEGORIES " +
                "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                "WHERE $TABLE_TRANSACTIONS.$COLUMN_DATE LIKE '%$strDate' " +
                "ORDER BY $TABLE_TRANSACTIONS.$COLUMN_DATE DESC, $TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP DESC"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val idIndex = i.getColumnIndex(COLUMN_KEY_ID)
            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val amountIndex = i.getColumnIndex(COLUMN_AMOUNT)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val dateIndex = i.getColumnIndex(COLUMN_DATE)
            val timeIndex = i.getColumnIndex(COLUMN_TIMESTAMP)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)

            if(i.moveToFirst()){
                do{
                    val id = i.getInt(idIndex)
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val type = i.getString(typeIndex)
                    val date = i.getString(dateIndex)
                    val time = i.getString(timeIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(id = id,categoryId = categoryId,name = name,amount = amount,type = type,date = date, timestamp = time, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }
    fun getAllWeeklyTransactions(startDate:String,endDate:String): List<TransactionsData>{
        val list = mutableListOf<TransactionsData>()
        val selectQuery = "SELECT " +
                "$TABLE_TRANSACTIONS.$COLUMN_KEY_ID," +
                "$TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID," +
                "$TABLE_CATEGORIES.$COLUMN_NAME," +
                "$TABLE_TRANSACTIONS.$COLUMN_AMOUNT," +
                "$TABLE_CATEGORIES.$COLUMN_TYPE," +
                "$TABLE_TRANSACTIONS.$COLUMN_DATE," +
                "$TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP," +
                "$TABLE_CATEGORIES.$COLUMN_IMAGE_VECTOR_ID," +
                "$TABLE_CATEGORIES.$COLUMN_COLOR " +
                "FROM $TABLE_TRANSACTIONS " +
                "INNER JOIN $TABLE_CATEGORIES " +
                "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID " +
                "WHERE $TABLE_TRANSACTIONS.$COLUMN_DATE BETWEEN '$startDate' AND '$endDate' " +
                "ORDER BY $TABLE_TRANSACTIONS.$COLUMN_DATE DESC, $TABLE_TRANSACTIONS.$COLUMN_TIMESTAMP DESC"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val idIndex = i.getColumnIndex(COLUMN_KEY_ID)
            val categoryIdIndex = i.getColumnIndex(COLUMN_CATEGORY_ID)
            val nameIndex = i.getColumnIndex(COLUMN_NAME)
            val amountIndex = i.getColumnIndex(COLUMN_AMOUNT)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val dateIndex = i.getColumnIndex(COLUMN_DATE)
            val timeIndex = i.getColumnIndex(COLUMN_TIMESTAMP)
            val imageVectorIndex = i.getColumnIndex(COLUMN_IMAGE_VECTOR_ID)
            val colorIndex = i.getColumnIndex(COLUMN_COLOR)

            if(i.moveToFirst()){
                do{
                    val id = i.getInt(idIndex)
                    val categoryId = i.getInt(categoryIdIndex)
                    val name = i.getString(nameIndex)
                    val amount = i.getDouble(amountIndex)
                    val type = i.getString(typeIndex)
                    val date = i.getString(dateIndex)
                    val time = i.getString(timeIndex)
                    val imageVectorID = i.getInt(imageVectorIndex)
                    val color = i.getLong(colorIndex)

                    val transactionData = TransactionsData(id = id,categoryId = categoryId,name = name,amount = amount,type = type,date = date, timestamp = time, imageVector = imageVectorID, color = color)
                    list.add(transactionData)
                }while (i.moveToNext())
            }
        }
        return list
    }

    fun updateParticularTransaction(id:Int,categoryId:Int,amount:Double){
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_AMOUNT,amount)
        values.put(COLUMN_CATEGORY_ID,categoryId)

        db.update(TABLE_TRANSACTIONS,values,"$COLUMN_KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }
    fun updateParticularCategory(id:Int,name:String,type:String,imageVectorID:Int,color:Long){
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_NAME,name)
        values.put(COLUMN_TYPE,type)
        values.put(COLUMN_IMAGE_VECTOR_ID,imageVectorID)
        values.put(COLUMN_COLOR,color)

        db.update(TABLE_CATEGORIES,values,"$COLUMN_KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }


    fun deleteParticularCategory(id: Int){
        val db = this.writableDatabase

        db.delete(TABLE_CATEGORIES,"$COLUMN_KEY_ID = ?", arrayOf(id.toString()))
        db.delete(TABLE_TRANSACTIONS,"$COLUMN_CATEGORY_ID = ?", arrayOf(id.toString()))
        db.close()
    }


    fun getDailyTotalIncomeAndExpense():MutableMap<String,DailyMonthlyYearlyTransactions>{
        var map = mutableMapOf<String,DailyMonthlyYearlyTransactions>()

        val selectQuery = "SELECT " +
                          "$TABLE_TRANSACTIONS.$COLUMN_DATE," +
                          "$TABLE_CATEGORIES.$COLUMN_TYPE," +
                          "$TABLE_TRANSACTIONS.$COLUMN_AMOUNT " +
                          "FROM $TABLE_TRANSACTIONS " +
                          "INNER JOIN $TABLE_CATEGORIES " +
                          "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val dateIndex = i.getColumnIndex(COLUMN_DATE)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val amountIndex = i.getColumnIndex(COLUMN_AMOUNT)

            if(i.moveToFirst()){
                do {
                    val type = i.getString(typeIndex)
                    val date = i.getString(dateIndex)

                    val dailyTransactions = map[date] ?: DailyMonthlyYearlyTransactions(0.0,0.0)

                    if(type == "expense"){
                        dailyTransactions.totalExpense += i.getDouble(amountIndex)
                    }else{
                        dailyTransactions.totalIncome += i.getDouble(amountIndex)
                    }

                    map[date] = dailyTransactions
                }while (i.moveToNext())
            }
        }
        return map
    }
    fun getMonthlyTotalIncomeAndExpense():MutableMap<String,DailyMonthlyYearlyTransactions>{
        var map = mutableMapOf<String,DailyMonthlyYearlyTransactions>()

        val selectQuery = "SELECT " +
                          "$TABLE_TRANSACTIONS.$COLUMN_DATE," +
                          "$TABLE_CATEGORIES.$COLUMN_TYPE," +
                          "$TABLE_TRANSACTIONS.$COLUMN_AMOUNT " +
                          "FROM $TABLE_TRANSACTIONS " +
                          "INNER JOIN $TABLE_CATEGORIES " +
                          "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val dateIndex = i.getColumnIndex(COLUMN_DATE)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val amountIndex = i.getColumnIndex(COLUMN_AMOUNT)
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            if(i.moveToFirst()){
                do {
                    val type = i.getString(typeIndex)
                    val date = LocalDate.parse(i.getString(dateIndex),dateFormat)

                    val month = date.month.name.lowercase().replaceFirstChar { it.titlecase() }
                    val dailyTransactions = map[month] ?: DailyMonthlyYearlyTransactions(0.0,0.0)

                    if(type == "expense"){
                        dailyTransactions.totalExpense += i.getDouble(amountIndex)
                    }else{
                        dailyTransactions.totalIncome += i.getDouble(amountIndex)
                    }

                    map[month] = dailyTransactions
                }while (i.moveToNext())
            }
        }
        return map
    }
    fun getYearlyTotalIncomeAndExpense():MutableMap<String,DailyMonthlyYearlyTransactions>{
        var map = mutableMapOf<String,DailyMonthlyYearlyTransactions>()

        val selectQuery = "SELECT " +
                          "$TABLE_TRANSACTIONS.$COLUMN_DATE," +
                          "$TABLE_CATEGORIES.$COLUMN_TYPE," +
                          "$TABLE_TRANSACTIONS.$COLUMN_AMOUNT " +
                          "FROM $TABLE_TRANSACTIONS " +
                          "INNER JOIN $TABLE_CATEGORIES " +
                          "ON $TABLE_CATEGORIES.$COLUMN_KEY_ID = $TABLE_TRANSACTIONS.$COLUMN_CATEGORY_ID"

        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use { i->
            val dateIndex = i.getColumnIndex(COLUMN_DATE)
            val typeIndex = i.getColumnIndex(COLUMN_TYPE)
            val amountIndex = i.getColumnIndex(COLUMN_AMOUNT)
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            if(i.moveToFirst()){
                do {
                    val type = i.getString(typeIndex)
                    val date = LocalDate.parse(i.getString(dateIndex),dateFormat)

                    val year = date.year
                    val dailyTransactions = map[year.toString()] ?: DailyMonthlyYearlyTransactions(0.0,0.0)

                    if(type == "expense"){
                        dailyTransactions.totalExpense += i.getDouble(amountIndex)
                    }else{
                        dailyTransactions.totalIncome += i.getDouble(amountIndex)
                    }

                    map[year.toString()] = dailyTransactions
                }while (i.moveToNext())
            }
        }
        return map
    }
    fun getWeeklyTotalIncomeAndExpense():MutableMap<String,DailyMonthlyYearlyTransactions>{
        var map = mutableMapOf<String,DailyMonthlyYearlyTransactions>()

        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, currentDate.year)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        var startDate: LocalDate
        var endDate: LocalDate

        do {
            val startDateString = dateFormat.format(calendar.time.toInstant().atZone(ZoneId.systemDefault()))
            startDate = LocalDate.parse(startDateString, dateFormat)
            calendar.add(Calendar.DAY_OF_MONTH, 6)
            val endDateString = dateFormat.format(calendar.time.toInstant().atZone(ZoneId.systemDefault()))
            endDate = LocalDate.parse(endDateString, dateFormat)
            calendar.add(Calendar.DAY_OF_MONTH, 1)

            val list = getAllWeeklyTransactions(startDateString,endDateString)
            for(i in list){
                val key = "$startDateString $endDateString"
                val weeklyTransaction = map[key] ?: DailyMonthlyYearlyTransactions(0.0,0.0)

                if(i.type == "expense") {
                    weeklyTransaction.totalExpense += i.amount
                }else{
                    weeklyTransaction.totalIncome += i.amount
                }

                map[key] = weeklyTransaction
            }
        } while (endDate.isBefore(currentDate))

        return map
    }


    fun getTotalCategories():Int{
        var total = 0
        val selectQuery = "SELECT COUNT(*) as totalCategories FROM $TABLE_CATEGORIES"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use {i->
            val totalIndex = i.getColumnIndex("totalCategories")

            if(i.moveToFirst()) {
                do {
                    total = i.getInt(totalIndex)
                } while (i.moveToNext())
            }
        }
        return total
    }
    fun getTotalTransactions():Int{
        var total = 0
        val selectQuery = "SELECT COUNT(*) as totalTransactions FROM $TABLE_TRANSACTIONS"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use {i->
            val totalIndex = i.getColumnIndex("totalTransactions")

            if(i.moveToFirst()) {
                do {
                    total = i.getInt(totalIndex)
                } while (i.moveToNext())
            }
        }
        return total
    }
    fun getExpenseCategories():Int{
        var total = 0
        val selectQuery = "SELECT COUNT(*) as totalCategory FROM $TABLE_CATEGORIES " +
                          "WHERE $TABLE_CATEGORIES.$COLUMN_TYPE = 'expense'"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use {i->
            val totalIndex = i.getColumnIndex("totalCategory")

            if(i.moveToFirst()) {
                do {
                    total = i.getInt(totalIndex)
                } while (i.moveToNext())
            }
        }
        return total
    }
    fun getIncomeCategories():Int{
        var total = 0
        val selectQuery = "SELECT COUNT(*) as totalCategory FROM $TABLE_CATEGORIES " +
                "WHERE $TABLE_CATEGORIES.$COLUMN_TYPE = 'income'"
        val db = this.readableDatabase

        val cursor = db.rawQuery(selectQuery,null)

        cursor.use {i->
            val totalIndex = i.getColumnIndex("totalCategory")

            if(i.moveToFirst()) {
                do {
                    total = i.getInt(totalIndex)
                } while (i.moveToNext())
            }
        }
        return total
    }
}
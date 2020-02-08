package dev.chester_lloyd.moneymanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import java.lang.String.format

class dbManager {

    val dbName = "MoneyManager"
    val dbAccountTable = "Accounts"
    val dbCategoryTable = "Categories"
    val dbTransactionTable = "Transactions"
    val dbPaymentsTable = "Payments"
    private val colID = "ID"
    private val colName = "Name"
    private val colBalance = "Balance"
    private val colDate = "Date"
    private val colAmount = "Amount"
    private val colIcon = "Icon"
    private val colColour = "Colour"
    private val colCategoryID = "CategoryID"
    private val colTransactionID = "TransactionID"
    private val colAccountID = "AccountID"
    val dbVersion = 1

    var sqlDB:SQLiteDatabase? = null

    constructor(context:Context) {
        var db = DatabaseHelper(context)
//      Opens database to write to it
        sqlDB = db.writableDatabase
    }


    inner class DatabaseHelper:SQLiteOpenHelper {
        var context:Context? = null

        constructor(context:Context):super(context, dbName, null, dbVersion) {
            this.context = context
        }

//      If database is not available, super constructor above will create one
//      Function below handles stuff to do once made: Make my tables
        override fun onCreate(db: SQLiteDatabase?) {
//          Create Accounts table if it does not exist
            db!!.execSQL("CREATE TABLE IF NOT EXISTS $dbAccountTable (" +
                    "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$colName VARCHAR(30), " +
                    "$colBalance FLOAT, " +
                    "$colIcon INTEGER, " +
                    "$colColour INTEGER);")

//          Create Categories table if it does not exist
            db!!.execSQL("CREATE TABLE IF NOT EXISTS $dbCategoryTable (" +
                    "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$colName VARCHAR(30), " +
                    "$colIcon INTEGER, " +
                    "$colColour INTEGER);")

//          Create Transactions table if it does not exist
            db!!.execSQL("CREATE TABLE IF NOT EXISTS $dbTransactionTable (" +
                    "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$colCategoryID INTEGER, " +
                    "$colName VARCHAR(30), " +
                    "$colDate DATETIME, " +
                    "$colAmount FLOAT);")

//          Create Payments table if it does not exist
            db!!.execSQL("CREATE TABLE IF NOT EXISTS $dbPaymentsTable (" +
                "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$colTransactionID INTEGER, " +
                "$colAccountID INTEGER, " +
                "$colAmount FLOAT);")

            Toast.makeText(this.context, "Database is created", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL(format("DROP TABLE IF EXISTS %s", dbName))
        }
    }

//  projection - Set of columns (if null, then all columns)
//  selection - Set of rows
//  sortOrder - Order
    fun query(table:String, projection: Array<String>, selection:String, selectionArgs:Array<String>, sortOrder:String):Cursor {
        val QB = SQLiteQueryBuilder()
        // Which table to run the query on
        QB.tables = table
        val cursor = QB.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor
    }

    fun insert(dbTable:String, values:ContentValues):Long {
//      Insert into table, these values
        val ID = sqlDB!!.insert(dbTable, "", values)
        return ID
    }

//  Function that inserts an account object into the database
    fun insertAccount(account: Account):Long {
        var values = ContentValues()
        values.put(colName, account.name)
        values.put(colBalance, account.balance)
        values.put(colIcon, account.icon)
        values.put(colColour, account.colour)

        val ID = sqlDB!!.insert(dbAccountTable, "", values)
        return ID
    }
//  Function that selects a single account from the database as an Account object
    fun selectAccount(accountID: Int):Account {
        val QB = SQLiteQueryBuilder()
        QB.tables = dbAccountTable
        val projection = arrayOf(colID, colName, colBalance, colIcon, colColour)
        val selectionArgs = arrayOf(accountID.toString())
        var account = Account()
        val cursor = QB.query(sqlDB, projection, "${colID}=?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            val ID = cursor.getInt(cursor.getColumnIndex(colID))
            val name = cursor.getString(cursor.getColumnIndex(colName))
            val balance = cursor.getDouble(cursor.getColumnIndex(colBalance))
            val icon = cursor.getInt(cursor.getColumnIndex(colIcon))
            val colour = cursor.getInt(cursor.getColumnIndex(colColour))
            account = Account(ID, name, balance, icon, colour)
        }
        return account
    }
//  Function that updates an account object in the database
    fun updateAccount(account: Account, selection: String, selectionArgs: Array<String>):Int {
        var values = ContentValues()
        values.put(colName, account.name)
        values.put(colBalance, account.balance)
        values.put(colIcon, account.icon)
        values.put(colColour, account.colour)

        return sqlDB!!.update(dbAccountTable, values, selection, selectionArgs)
    }

//  Function that inserts a category object into the database
    fun insertCategory(category: Category):Long {
        var values = ContentValues()
        values.put(colName, category.name)
        values.put(colIcon, category.icon)
        values.put(colColour, category.colour)

        val ID = sqlDB!!.insert(dbCategoryTable, "", values)
        return ID
    }
//  Function that selects a single account from the database as an Account object
    fun selectCategory(categoryID: Int):Category {
        val QB = SQLiteQueryBuilder()
        QB.tables = dbCategoryTable
        val projection = arrayOf(colID, colName, colIcon, colColour)
        val selectionArgs = arrayOf(categoryID.toString())
        var category = Category()
        val cursor = QB.query(sqlDB, projection, "${colID}=?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            val ID = cursor.getInt(cursor.getColumnIndex(colID))
            val name = cursor.getString(cursor.getColumnIndex(colName))
            val icon = cursor.getInt(cursor.getColumnIndex(colIcon))
            val colour = cursor.getInt(cursor.getColumnIndex(colColour))
            category = Category(ID, name, icon, colour)
        }
        return category
    }
//  Function that updates a category object in the database
    fun updateCategory(category: Category, selection: String, selectionArgs: Array<String>):Int {
        var values = ContentValues()
        values.put(colName, category.name)
        values.put(colIcon, category.icon)
        values.put(colColour, category.colour)

        return sqlDB!!.update(dbCategoryTable, values, selection, selectionArgs)
    }

//    fun updateAccount(table: String, values: ContentValues, selection: String, selectionArgs: Array<String>):Int {


}
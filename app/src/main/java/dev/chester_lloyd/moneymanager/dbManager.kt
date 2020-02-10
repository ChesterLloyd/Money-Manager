package dev.chester_lloyd.moneymanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import java.lang.String.format
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
            db.execSQL("CREATE TABLE IF NOT EXISTS $dbCategoryTable (" +
                    "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$colName VARCHAR(30), " +
                    "$colIcon INTEGER, " +
                    "$colColour INTEGER);")

//          Create Transactions table if it does not exist
            db.execSQL("CREATE TABLE IF NOT EXISTS $dbTransactionTable (" +
                    "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$colCategoryID INTEGER, " +
                    "$colName VARCHAR(30), " +
                    "$colDate DATETIME, " +
                    "$colAmount FLOAT, " +
                    "FOREIGN KEY(${colCategoryID}) REFERENCES ${dbCategoryTable}(${colID}) );")

//          Create Payments table if it does not exist
            db.execSQL("CREATE TABLE IF NOT EXISTS $dbPaymentsTable (" +
                "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$colTransactionID INTEGER, " +
                "$colAccountID INTEGER, " +
                "$colAmount FLOAT, " +
                "FOREIGN KEY(${colTransactionID}) REFERENCES ${dbTransactionTable}(${colID}), " +
                "FOREIGN KEY(${colAccountID}) REFERENCES ${dbAccountTable}(${colID}) );")

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




//  Functions to handle Category objects within the database
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
//  Function that selects every account from the database as a list of Account objects
    fun selectAccount():ArrayList<Account> {
        val QB = SQLiteQueryBuilder()
        QB.tables = dbAccountTable
        val projection = arrayOf(colID, colName, colBalance, colIcon, colColour)
        val selectionArgs = arrayOf("%")
        var listAccounts = ArrayList<Account>()
        val cursor = QB.query(sqlDB, projection, "${colName} LIKE ?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex(colID))
                val name = cursor.getString(cursor.getColumnIndex(colName))
                val balance = cursor.getDouble(cursor.getColumnIndex(colBalance))
                val icon = cursor.getInt(cursor.getColumnIndex(colID))
                val colour = cursor.getInt(cursor.getColumnIndex(colColour))

                listAccounts.add(Account(ID, name, balance, icon, colour))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listAccounts
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




//  Functions to handle Category objects within the database
//  Function that inserts a category object into the database
    fun insertCategory(category: Category):Long {
        var values = ContentValues()
        values.put(colName, category.name)
        values.put(colIcon, category.icon)
        values.put(colColour, category.colour)

        val ID = sqlDB!!.insert(dbCategoryTable, "", values)
        return ID
    }
//  Function that selects a single category from the database as a Category object
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
//  Function that selects every category from the database as a list of Category objects
    fun selectCategory():ArrayList<Category> {
        val QB = SQLiteQueryBuilder()
        QB.tables = dbCategoryTable
        val projection = arrayOf(colID, colName, colIcon, colColour)
        val selectionArgs = arrayOf("%")
        var listCategories = ArrayList<Category>()
        val cursor = QB.query(sqlDB, projection, "${colName} LIKE ?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val name = cursor.getString(cursor.getColumnIndex("Name"))
                val icon = cursor.getInt(cursor.getColumnIndex("Icon"))
                val colour = cursor.getInt(cursor.getColumnIndex("Colour"))

                listCategories.add(Category(ID, name, icon, colour))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listCategories
    }
//  Function that updates a category object in the database
    fun updateCategory(category: Category, selection: String, selectionArgs: Array<String>):Int {
        var values = ContentValues()
        values.put(colName, category.name)
        values.put(colIcon, category.icon)
        values.put(colColour, category.colour)

        return sqlDB!!.update(dbCategoryTable, values, selection, selectionArgs)
    }




//  Functions to handle Transaction objects within the database
//  Function that inserts a transaction object into the database
    fun insertTransaction(transaction: Transaction):Long {
        var values = ContentValues()
        values.put(colCategoryID, transaction.category.categoryID)
        values.put(colName, transaction.name)
        values.put(colDate, Timestamp(transaction.date.getTimeInMillis()).toString())
        values.put(colAmount, transaction.amount)

        val ID = sqlDB!!.insert(dbTransactionTable, "", values)
        return ID
    }
//  Function that selects transactions based on Category/Account ID as a list of Transaction objects
    fun selectTransaction(id: Int, type: String):ArrayList<Transaction> {
        val QB = SQLiteQueryBuilder()
        QB.tables = dbTransactionTable
        var selectionArgs= arrayOf(id.toString())
        var listTransactions = ArrayList<Transaction>()

        var query: String = ""
        if (type == "Categories") {
            query = "SELECT T.${colID}, T.${colCategoryID}, " +
                    "T.${colName}, T.${colDate}, T.${colAmount} FROM ${dbTransactionTable} T " +
                    "JOIN ${dbCategoryTable} C ON C.${colID} = T.${colCategoryID}"

            if (id > 0) {
                query += " WHERE C.${colID} = ? "
            } else {
                selectionArgs = emptyArray()
            }
        } else if (type == "Accounts") {
            println("ACC")
            query = "SELECT T.${colID}, T.${colCategoryID}, " +
                    "T.${colName}, T.${colDate}, T.${colAmount} FROM ${dbTransactionTable} T " +
                    "JOIN ${dbPaymentsTable} P ON P.${colTransactionID} = T.${colID} " +
                    "WHERE P.${colAccountID} = ?"
        }
        query += " ORDER BY T.${colDate} DESC"
        val cursor = sqlDB!!.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex(colID))
                val categoryID = cursor.getInt(cursor.getColumnIndex(colCategoryID))
                val name = cursor.getString(cursor.getColumnIndex(colName))
                val date = cursor.getString(cursor.getColumnIndex(colDate))
                val amount = cursor.getDouble(cursor.getColumnIndex(colAmount))

                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                cal.time = sdf.parse(date)

                listTransactions.add(Transaction(ID, selectCategory(categoryID), name, cal, amount))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listTransactions
    }
//  Function that updates a Transaction object in the database
    fun updateTransaction(transaction: Transaction, selection: String, selectionArgs: Array<String>):Int {
        var values = ContentValues()
        values.put(colCategoryID, transaction.category.categoryID)
        values.put(colName, transaction.name)
        values.put(colDate, Timestamp(transaction.date.getTimeInMillis()).toString())
        values.put(colAmount, transaction.amount)

        return sqlDB!!.update(dbAccountTable, values, selection, selectionArgs)
    }




//  Functions to handle Payment objects within the database
//  Function that inserts a payment object into the database
    fun insertPayment(payment: Payment):Long {
        var values = ContentValues()
        values.put(colTransactionID, payment.transaction.transactionID)
        values.put(colAccountID, payment.account.accountID)
        values.put(colAmount, payment.amount)

        val ID = sqlDB!!.insert(dbPaymentsTable, "", values)
        return ID
    }




    fun delete(table: String, selection: String, selectionArgs: Array<String>):Int {
        return sqlDB!!.delete(table, selection, selectionArgs)
    }

//    fun updateAccount(table: String, values: ContentValues, selection: String, selectionArgs: Array<String>):Int {


}
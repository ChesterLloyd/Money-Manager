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
    private  val dbAccountTable = "Accounts"
    private val colAccountID = "ID"
    private val colAccountName = "Name"
    private val colAccountBalance = "Balance"
    private val colAccountIcon = "Icon"
    private val colAccountColour = "Colour"
    val dbVersion = 1

//  Create Accounts table if it does not exist
    val sqlCreateTable = format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, " +
            "%s VARCHAR(100), %s DOUBLE, %s INTEGER, %s INTEGER);", dbAccountTable, colAccountID,
            colAccountName, colAccountBalance, colAccountIcon, colAccountColour)

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
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, " database is created", Toast.LENGTH_SHORT).show()
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
        values.put(colAccountName, account.name)
        values.put(colAccountBalance, account.balance)
        values.put(colAccountIcon, account.icon)
        values.put(colAccountColour, account.colour)

        val ID = sqlDB!!.insert(dbAccountTable, "", values)
        return ID
    }
}
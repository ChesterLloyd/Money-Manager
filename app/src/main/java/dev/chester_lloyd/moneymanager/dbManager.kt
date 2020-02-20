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
    private val colActive = "Active"
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
                    "$colColour INTEGER, " +
                    "$colActive INTEGER);")

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
                    "FOREIGN KEY(${colTransactionID}) REFERENCES ${dbTransactionTable}(${colID}) ON DELETE CASCADE, " +
                    "FOREIGN KEY(${colAccountID}) REFERENCES ${dbAccountTable}(${colID}) ON DELETE CASCADE );")

            Toast.makeText(this.context, "Database is created", Toast.LENGTH_SHORT).show()

//          Create default accounts
            db.execSQL("INSERT INTO $dbAccountTable ($colName, $colBalance, $colIcon, $colColour, $colActive) " +
                    "VALUES ('Cash', 0.0, 2, 0, 1)");
            db.execSQL("INSERT INTO $dbAccountTable ($colName, $colBalance, $colIcon, $colColour, $colActive) " +
                    "VALUES ('Current', 50.25, 3, 0, 1)");
            db.execSQL("INSERT INTO $dbAccountTable ($colName, $colBalance, $colIcon, $colColour, $colActive) " +
                    "VALUES ('Savings', 1000.0, 0, 0, 1)");

//          Create default categories
            db.execSQL("INSERT INTO $dbCategoryTable ($colName, $colIcon, $colColour) " +
                    "VALUES ('Bills', 3, 0)");
            db.execSQL("INSERT INTO $dbCategoryTable ($colName, $colIcon, $colColour) " +
                    "VALUES ('Phone', 9, 0)");
            db.execSQL("INSERT INTO $dbCategoryTable ($colName, $colIcon, $colColour) " +
                    "VALUES ('Rent', 21, 0)");
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL(format("DROP TABLE IF EXISTS %s", dbName))
        }

        override fun onConfigure(db: SQLiteDatabase?) {
            super.onConfigure(db)
            db!!.execSQL("PRAGMA foreign_keys = ON")
        }

        override fun onOpen(db: SQLiteDatabase?) {
            super.onOpen(db)
            db!!.execSQL("PRAGMA foreign_keys = ON")
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
        values.put(colActive, 1)

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
//  Function that hides an account in the database
    private fun hideAccount(accountID: Array<String>):Int {
        var values = ContentValues()
        values.put(colActive, 0)
        return sqlDB!!.update(dbAccountTable, values, "ID = ?",
            accountID)
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
//  Function that selects a single transaction from the database as a Transaction object
    fun selectTransaction(transactionID: Int):Transaction {
        val QB = SQLiteQueryBuilder()
        QB.tables = dbTransactionTable
        val projection = arrayOf(colID, colCategoryID, colName, colDate, colAmount)
        val selectionArgs = arrayOf(transactionID.toString())
        var transaction = Transaction()
        val cursor = QB.query(sqlDB, projection, "${colID}=?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            val ID = cursor.getInt(cursor.getColumnIndex(colID))
            val categoryID = cursor.getInt(cursor.getColumnIndex(colCategoryID))
            val name = cursor.getString(cursor.getColumnIndex(colName))
            val date = cursor.getString(cursor.getColumnIndex(colDate))
            val amount = cursor.getDouble(cursor.getColumnIndex(colAmount))

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            cal.time = sdf.parse(date)

            transaction = Transaction(ID, selectCategory(categoryID), name, cal, amount)
        }
        return transaction
    }
//  Function that selects transactions based on Category/Account ID as a list of Transaction objects
    fun selectTransaction(id: Int, type: String):ArrayList<Transaction> {
//        val QB = SQLiteQueryBuilder()
//        QB.tables = dbTransactionTable
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

        return sqlDB!!.update(dbTransactionTable, values, selection, selectionArgs)
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
//  Function that selects payments based on a transaction ID as a list of Payment objects
    fun selectPayment(transactionID: Int):ArrayList<Payment> {
        var selectionArgs= arrayOf(transactionID.toString())
        var listPayments = ArrayList<Payment>()

        var query = "SELECT P.${colID}, P.${colTransactionID}, " +
                "P.${colAccountID}, P.${colAmount} FROM ${dbPaymentsTable} P " +
                "JOIN ${dbTransactionTable} T ON T.${colID} = P.${colTransactionID} " +
                "JOIN ${dbAccountTable} A ON A.${colID} = P.${colAccountID} " +
                "WHERE T.${colID} = ? " +
                "ORDER BY A.${colName} ASC"
        val cursor = sqlDB!!.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex(colID))
                val transactionID = cursor.getInt(cursor.getColumnIndex(colTransactionID))
                val accounID = cursor.getInt(cursor.getColumnIndex(colAccountID))
                val amount = cursor.getDouble(cursor.getColumnIndex(colAmount))

                listPayments.add(Payment(ID, selectTransaction(transactionID), selectAccount(accounID), amount))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listPayments
    }




    fun delete(table: String, selection: String, selectionArgs: Array<String>):Int {

        if (table == dbAccountTable) {
//          Get all transactions for this account that are safe to delete
//          This is any transaction that has only been paid for by only this account
            var query = "SELECT T.* FROM $dbTransactionTable T " +
                    "JOIN $dbPaymentsTable P ON P.${colTransactionID} = T.${colID} " +
                    "WHERE P.${colAccountID} = ? AND T.${colID} NOT IN (" +
                    "    SELECT TID FROM ( " +
                    "        SELECT T.${colID} AS TID FROM $dbTransactionTable T " +
                    "        JOIN $dbPaymentsTable P ON P.${colTransactionID} = T.${colID} " +
                    "        GROUP BY P.${colTransactionID} " +
                    "        HAVING COUNT(*) > 1 ) AS TID )"
            var cursor = sqlDB!!.rawQuery(query, selectionArgs)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(colID))
                    sqlDB!!.delete(dbTransactionTable, "$colID = ?",
                        arrayOf(id.toString()))
                    sqlDB!!.delete(dbPaymentsTable, "$colTransactionID = ? AND $colAccountID = ?",
                        arrayOf(id.toString(), selectionArgs.toString()))
                } while (cursor.moveToNext())
            }
            cursor.close()
//          Hide the account, cannot delete as there may be shared transactions
            hideAccount(selectionArgs)
//          Remove any orphaned transactions
//          These are any that were aid by multiple accounts and this is the last of those accounts
//          to be deleted, so delete all payments and transactions under these accounts
            query = "SELECT T.$colID FROM $dbTransactionTable T " +
                    "JOIN $dbPaymentsTable P ON P.$colTransactionID = T.$colID " +
                    "JOIN $dbAccountTable A ON A.$colID = P.$colAccountID " +
                    "WHERE (A.$colActive = 0 OR A.$colID = ?) AND T.$colID IN ( " +
                    "    SELECT TID FROM ( " +
                    "        SELECT T1.$colID AS TID FROM $dbTransactionTable T1 " +
                    "        JOIN $dbPaymentsTable P ON P.$colTransactionID = T1.$colID " +
                    "        JOIN $dbAccountTable A ON A.$colID = P.$colAccountID " +
                    "        GROUP BY P.$colTransactionID " +
                    "        HAVING COUNT(*) = ( " +
                    "            SELECT COUNT(*) AS currentInactive FROM $dbTransactionTable T " +
                    "            JOIN $dbPaymentsTable P ON P.$colTransactionID = T.ID " +
                    "            JOIN $dbAccountTable A ON A.$colID = P.$colAccountID " +
                    "            WHERE A.$colActive = 0 AND T.$colID = T1.$colID " +
                    "            GROUP BY P.$colTransactionID " +
                    "            HAVING COUNT(*) > 1  ) ) AS TID ) " +
                    "GROUP BY T.$colID"
            cursor = sqlDB!!.rawQuery(query, selectionArgs)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(colID))
                    sqlDB!!.delete(dbTransactionTable, "$colID = ?",
                        arrayOf(id.toString()))
                    sqlDB!!.delete(dbPaymentsTable, "$colTransactionID = ?",
                        arrayOf(id.toString()))
                } while (cursor.moveToNext())
            }
        } else if (table == dbCategoryTable) {
//          If we are deleting a category, remove all of its transactions first
            sqlDB!!.delete(dbTransactionTable, "$colCategoryID = ?", selectionArgs)
        }

//      Handle original delete request
//        return sqlDB!!.delete(table, selection, selectionArgs)
        return 0
    }

//    fun updateAccount(table: String, values: ContentValues, selection: String, selectionArgs: Array<String>):Int {


}
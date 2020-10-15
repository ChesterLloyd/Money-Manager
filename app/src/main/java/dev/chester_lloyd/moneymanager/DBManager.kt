package dev.chester_lloyd.moneymanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast
import java.lang.String.format
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Handles all database related tasks.
 *
 * @param context Context.
 * @author Chester Lloyd
 * @since 1.0
 */
open class DBManager(context: Context) {

    val dbName = "MoneyManager"
    val dbAccountTable = "Accounts"
    val dbCategoryTable = "Categories"
    val dbTransactionTable = "Transactions"
    val dbPaymentsTable = "Payments"
    internal val colID = "ID"
    private val colName = "Name"
    private val colBalance = "Balance"
    internal val colDate = "Date"
    internal val colAmount = "Amount"
    private val colIcon = "Icon"
    private val colColour = "Colour"
    internal val colCategoryID = "CategoryID"
    internal val colTransactionID = "TransactionID"
    internal val colAccountID = "AccountID"
    private val colActive = "Active"
    private val colDetails = "Details"
    private val colDefault = "TheDefault"
    private val colTransferTransactionID = "TransferTransactionID"
    val dbVersion = 1

    internal var sqlDB: SQLiteDatabase? = null

    // Opens database to write to it
    init {
        val db = DatabaseHelper(context)
        sqlDB = db.writableDatabase
    }


    inner class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, dbName, null, dbVersion) {
        var context: Context? = context

        /**
         * An [onCreate] method that creates all required tables when making the database for the
         * first time. This will also add some default entries.
         *
         * If database is not available, super constructor above will create one.
         *
         * @param db SQLiteDatabase.
         */
        override fun onCreate(db: SQLiteDatabase?) {
            // Create Accounts table if it does not exist
            db!!.execSQL(
                "CREATE TABLE IF NOT EXISTS $dbAccountTable (" +
                        "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$colName VARCHAR(30), " +
                        "$colBalance FLOAT, " +
                        "$colIcon INTEGER, " +
                        "$colColour INTEGER, " +
                        "$colActive INTEGER, " +
                        "$colDefault BOOLEAN);"
            )
            // Create Categories table if it does not exist
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS $dbCategoryTable (" +
                        "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$colName VARCHAR(30), " +
                        "$colIcon INTEGER, " +
                        "$colColour INTEGER);"
            )
            // Create Transactions table if it does not exist
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS $dbTransactionTable (" +
                        "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$colCategoryID INTEGER, " +
                        "$colName VARCHAR(30), " +
                        "$colDetails VARCHAR(100), " +
                        "$colDate DATETIME, " +
                        "$colAmount FLOAT, " +
                        "$colTransferTransactionID INTEGER, " +
                        "FOREIGN KEY(${colCategoryID}) REFERENCES ${dbCategoryTable}(${colID}) );"
            )
            // Create Payments table if it does not exist
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS $dbPaymentsTable (" +
                        "$colID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$colTransactionID INTEGER, " +
                        "$colAccountID INTEGER, " +
                        "$colAmount FLOAT, " +
                        "FOREIGN KEY(${colTransactionID}) REFERENCES ${dbTransactionTable}(${colID}) ON DELETE CASCADE, " +
                        "FOREIGN KEY(${colAccountID}) REFERENCES ${dbAccountTable}(${colID}) ON DELETE CASCADE );"
            )
            // Show confirmation toast when database is created
            Toast.makeText(this.context, "Database is created", Toast.LENGTH_SHORT).show()

            // Create default accounts
            db.execSQL(
                "INSERT INTO $dbAccountTable ($colName, $colBalance, $colIcon, $colColour, $colActive, $colDefault) " +
                        "VALUES ('Cash', 0.0, 2, 0, 1, 0)"
            )
            db.execSQL(
                "INSERT INTO $dbAccountTable ($colName, $colBalance, $colIcon, $colColour, $colActive, $colDefault) " +
                        "VALUES ('Current', 50.25, 3, 0, 1, 1)"
            )
            db.execSQL(
                "INSERT INTO $dbAccountTable ($colName, $colBalance, $colIcon, $colColour, $colActive, $colDefault) " +
                        "VALUES ('Savings', 1000.0, 0, 0, 1, 0)"
            )

            // Create default categories
            db.execSQL(
                "INSERT INTO $dbCategoryTable ($colName, $colIcon, $colColour) " +
                        "VALUES ('Transfer', 55, 0)"
            )
            db.execSQL(
                "INSERT INTO $dbCategoryTable ($colName, $colIcon, $colColour) " +
                        "VALUES ('Bills', 3, 0)"
            )
            db.execSQL(
                "INSERT INTO $dbCategoryTable ($colName, $colIcon, $colColour) " +
                        "VALUES ('Phone', 9, 0)"
            )
            db.execSQL(
                "INSERT INTO $dbCategoryTable ($colName, $colIcon, $colColour) " +
                        "VALUES ('Rent', 21, 0)"
            )
        }

        /**
         * An [onUpgrade] method that change the database when the version is updated.
         *
         * @param db SQLiteDatabase.
         * @param oldVersion Old database version number.
         * @param newVersion New database version number.
         */
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL(format("DROP TABLE IF EXISTS %s", dbName))
        }

        /**
         * An [onConfigure] method that sets the database to use foreign keys.
         *
         * @param db SQLiteDatabase.
         */
        override fun onConfigure(db: SQLiteDatabase?) {
            super.onConfigure(db)
            db!!.execSQL("PRAGMA foreign_keys = ON")
        }

        /**
         * An [onOpen] method that sets the database to use foreign keys.
         *
         * @param db SQLiteDatabase.
         */
        override fun onOpen(db: SQLiteDatabase?) {
            super.onOpen(db)
            db!!.execSQL("PRAGMA foreign_keys = ON")
        }
    }

//  projection - Set of columns (if null, then all columns)
//  selection - Set of rows
//  sortOrder - Order
//    fun query(
//        table: String,
//        projection: Array<String>,
//        selection: String,
//        selectionArgs: Array<String>,
//        sortOrder: String
//    ): Cursor {
//        val qb = SQLiteQueryBuilder()
//        // Which table to run the query on
//        qb.tables = table
//        return qb.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder)
//    }

    /**
     * Inserts an [Account] object into the database.
     *
     * @param account The [Account] to insert.
     * @return The ID that the account has been saved as.
     */
    fun insertAccount(account: Account): Long {
        if (selectAccounts("active", null).isEmpty()) {
            // This is the only active account, set as default
            account.default = true
        }

        if (account.default) {
            // Account is default, clear defaults
            clearDefaultAccount()
        }

        val values = ContentValues()
        values.put(colName, account.name)
        values.put(colBalance, account.balance)
        values.put(colIcon, account.icon)
        values.put(colColour, account.colour)
        values.put(colActive, 1)
        values.put(colDefault, account.default)

        return sqlDB!!.insert(dbAccountTable, "", values)
    }

    /**
     * Gets a single [Account] from the database based on its ID.
     *
     * @param accountID The ID of the [Account] to read.
     * @return The [Account] object with the specified ID.
     */
    fun selectAccount(accountID: Int): Account {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbAccountTable
        val projection = arrayOf(colID, colName, colBalance, colIcon, colColour, colDefault)
        val selectionArgs = arrayOf(accountID.toString())
        var account = Account()
        val cursor = qb.query(sqlDB, projection, "${colID}=?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(colID))
            val name = cursor.getString(cursor.getColumnIndex(colName))
            val balance = cursor.getDouble(cursor.getColumnIndex(colBalance))
            val icon = cursor.getInt(cursor.getColumnIndex(colIcon))
            val colour = cursor.getInt(cursor.getColumnIndex(colColour))
            val default = cursor.getInt(cursor.getColumnIndex(colDefault))
            account = Account(id, name, balance, icon, colour, (default == 1))
        }
        cursor.close()
        return account
    }

    /**
     * Gets an [ArrayList] of [Account] objects from the database based on if it is active or a
     * specified limit.
     *
     * @param type Get [Account] objects based on if they are active, or all accounts.
     * @param limit Limit the number of accounts returned by an optional limit.
     * @return An [ArrayList] of [Account] objects
     */
    fun selectAccounts(type: String, limit: String?): ArrayList<Account> {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbAccountTable
        val projection = arrayOf(colID, colName, colBalance, colIcon, colColour, colDefault)
        val listAccounts = ArrayList<Account>()

        var selection = "$colName LIKE ?"
        var selectionArgs = arrayOf("%")
        if (type == "active") {
            selection = "$colActive = ?"
            selectionArgs = arrayOf("1")
        }

        val cursor =
            qb.query(sqlDB, projection, selection, selectionArgs, null, null, colName, limit)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(colID))
                val name = cursor.getString(cursor.getColumnIndex(colName))
                val balance = cursor.getDouble(cursor.getColumnIndex(colBalance))
                val icon = cursor.getInt(cursor.getColumnIndex(colIcon))
                val colour = cursor.getInt(cursor.getColumnIndex(colColour))
                val default = cursor.getInt(cursor.getColumnIndex(colDefault))

                listAccounts.add(Account(id, name, balance, icon, colour, (default == 1)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listAccounts
    }

    /**
     * Updates an [Account] object stored in the database.
     *
     * @param account An [Account] object to update.
     * @param selection Query representing the account to update.
     * @param selectionArgs The [selection] arguments.
     * @return Number of rows updated.
     */
    fun updateAccount(account: Account, selection: String, selectionArgs: Array<String>): Int {
        if (account.default) {
            clearDefaultAccount()
        }

        val values = ContentValues()
        values.put(colName, account.name)
        values.put(colBalance, account.balance)
        values.put(colIcon, account.icon)
        values.put(colColour, account.colour)
        values.put(colDefault, account.default)

        return sqlDB!!.update(dbAccountTable, values, selection, selectionArgs)
    }
    //I love you <3

    /**
     * Hides an account that is stored in the database.
     *
     * @param accountID The ID of the [Account] object to hide.
     * @return Number of rows updated.
     */
    private fun hideAccount(accountID: Array<String>): Int {
        val account = selectAccount(accountID[0].toInt())
        if (account.default) {
            // Account is default, clear defaults
            clearDefaultAccount()
        }

        val values = ContentValues()
        values.put(colActive, 0)
        values.put(colDefault, false)
        return sqlDB!!.update(dbAccountTable, values, "$colID = ?", accountID)
    }

    /**
     * Gets the [Account] when given a [Transaction]. NOTE: this will only work reliably when a
     * transaction is paid by only one account. Useful when updating a transfer.
     *
     * @return One [Account] linked to the transaction.
     */
    fun getAccountByTransaction(transaction: Transaction): Account? {
        val selectionArgs = arrayOf(transaction.transactionID.toString())

        val query = "SELECT A.${colID} FROM $dbAccountTable A " +
                "JOIN $dbPaymentsTable P ON P.${colAccountID} = A.${colID} " +
                "JOIN $dbTransactionTable T ON T.${colID} = P.${colTransactionID} " +
                "WHERE T.${colID} = ? LIMIT 1"

        val cursor = sqlDB!!.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(colID))
            return selectAccount(id)
        }
        cursor.close()
        return null
    }

    /**
     * Gets the default [Account].
     *
     * @return The default [Account].
     */
    fun getDefaultAccount(): Account? {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbAccountTable
        val projection = arrayOf(colID, colName, colBalance, colIcon, colColour, colDefault)
        val selectionArgs = arrayOf("1")
        val cursor = qb.query(sqlDB, projection, "${colDefault}=?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(colID))
            println(id)
            return selectAccount(id)
        }
        cursor.close()
        return null
    }

    /**
     * Clears the default flag for all accounts.
     *
     * @return Number of rows updated.
     */
    private fun clearDefaultAccount(): Int {
        val values = ContentValues()
        values.put(colDefault, false)

        return sqlDB!!.update(dbAccountTable, values, "$colID LIKE ?", arrayOf("%"))
    }

    /**
     * Inserts a [Category] object into the database.
     *
     * @param category The [Category] to insert.
     * @return The ID that the category has been saved as.
     */
    fun insertCategory(category: Category): Long {
        val values = ContentValues()
        values.put(colName, category.name)
        values.put(colIcon, category.icon)
        values.put(colColour, category.colour)

        return sqlDB!!.insert(dbCategoryTable, "", values)
    }

    /**
     * Gets a single [Category] from the database based on its ID.
     *
     * @param categoryID The ID of the [Category] to read.
     * @return The [Category] object with the specified ID.
     */
    fun selectCategory(categoryID: Int): Category {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbCategoryTable
        val projection = arrayOf(colID, colName, colIcon, colColour)
        val selectionArgs = arrayOf(categoryID.toString())
        var category = Category()
        val cursor = qb.query(sqlDB, projection, "${colID}=?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(colID))
            val name = cursor.getString(cursor.getColumnIndex(colName))
            val icon = cursor.getInt(cursor.getColumnIndex(colIcon))
            val colour = cursor.getInt(cursor.getColumnIndex(colColour))
            category = Category(id, name, icon, colour)
        }
        cursor.close()
        return category
    }

    /**
     * Gets an [ArrayList] of all [Category] objects from the database.
     *
     * @return An [ArrayList] of [Category] objects
     */
    fun selectCategories(): ArrayList<Category> {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbCategoryTable
        val projection = arrayOf(colID, colName, colIcon, colColour)
        val selectionArgs = arrayOf("%")
        val listCategories = ArrayList<Category>()
        val cursor =
            qb.query(sqlDB, projection, "$colName LIKE ?", selectionArgs, null, null, colName)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(colID))
                val name = cursor.getString(cursor.getColumnIndex(colName))
                val icon = cursor.getInt(cursor.getColumnIndex(colIcon))
                val colour = cursor.getInt(cursor.getColumnIndex(colColour))

                listCategories.add(Category(id, name, icon, colour))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listCategories
    }

    /**
     * Updates a [Category] object stored in the database.
     *
     * @param category A [Category] object to update.
     * @param selection Query representing the account to update.
     * @param selectionArgs The [selection] arguments.
     * @return Number of rows updated.
     */
    fun updateCategory(category: Category, selection: String, selectionArgs: Array<String>): Int {
        val values = ContentValues()
        values.put(colName, category.name)
        values.put(colIcon, category.icon)
        values.put(colColour, category.colour)

        return sqlDB!!.update(dbCategoryTable, values, selection, selectionArgs)
    }

    /**
     * Inserts a [Transaction] object into the database.
     *
     * @param transaction The [Transaction] to insert.
     * @return The ID that the transaction has been saved as.
     */
    fun insertTransaction(transaction: Transaction): Long {
        val values = ContentValues()
        values.put(colCategoryID, transaction.category.categoryID)
        values.put(colName, transaction.merchant)
        values.put(colDetails, transaction.details)
        values.put(colDate, Timestamp(transaction.date.timeInMillis).toString())
        values.put(colAmount, transaction.amount)
        values.put(colTransferTransactionID, transaction.transferTransactionID)

        return sqlDB!!.insert(dbTransactionTable, "", values)
    }

    /**
     * Gets a single [Transaction] from the database based on its ID.
     *
     * @param transactionID The ID of the [Transaction] to read.
     * @return The [Transaction] object with the specified ID.
     */
    fun selectTransaction(transactionID: Int): Transaction {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbTransactionTable
        val projection = arrayOf(
            colID, colCategoryID, colName, colDetails,
            colDate, colAmount, colTransferTransactionID
        )
        val selectionArgs = arrayOf(transactionID.toString())
        var transaction = Transaction()
        val cursor = qb.query(
            sqlDB, projection, "${colID}=?", selectionArgs,
            null, null, colName
        )
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(colID))
            val categoryID = cursor.getInt(cursor.getColumnIndex(colCategoryID))
            val merchant = cursor.getString(cursor.getColumnIndex(colName))
            val details = cursor.getString(cursor.getColumnIndex(colDetails))
            val date = cursor.getString(cursor.getColumnIndex(colDate))
            val amount = cursor.getDouble(cursor.getColumnIndex(colAmount))
            val transferID = cursor.getInt(cursor.getColumnIndex(colTransferTransactionID))

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            cal.time = sdf.parse(date)

            transaction =
                Transaction(
                    id, selectCategory(categoryID), merchant, details, cal, amount, transferID
                )
        }
        cursor.close()
        return transaction
    }

    /**
     * Gets an [ArrayList] of [Transaction] objects from the database based on its category ID or
     * account ID with an optional specified limit.
     *
     * @param id The category ID or account ID of the [Transaction]
     * @param type Get [Transaction] objects based on categories or accounts.
     * @param limit Limit the number of accounts returned by an optional limit.
     * @return An [ArrayList] of [Transaction] objects
     */
    fun selectTransactions(
        id: Int,
        type: String,
        limit: String?,
        includeSecondTransfer: Boolean = true
    ): ArrayList<Transaction> {
        var selectionArgs = arrayOf(id.toString())
        val listTransactions = ArrayList<Transaction>()

        var query = ""
        if (type == "Categories") {
            query = "SELECT T.${colID}, T.${colCategoryID}, T.${colName}, T.${colDetails}, " +
                    "T.${colDate}, T.${colAmount}, T.${colTransferTransactionID} " +
                    "FROM $dbTransactionTable T " +
                    "JOIN $dbCategoryTable C ON C.${colID} = T.${colCategoryID}"

            if (id > 0) {
                query += " WHERE C.${colID} = ? "
                if (!includeSecondTransfer) query += " AND "
            } else {
                selectionArgs = emptyArray()
                if (!includeSecondTransfer) query += " WHERE "
            }

        } else if (type == "Accounts") {
            query = "SELECT T.${colID}, T.${colCategoryID}, T.${colName}, T.${colDetails}, " +
                    "T.${colDate}, T.${colAmount}, T.${colTransferTransactionID} " +
                    "FROM $dbTransactionTable T " +
                    "JOIN $dbPaymentsTable P ON P.${colTransactionID} = T.${colID} " +
                    "WHERE P.${colAccountID} = ?"

            if (!includeSecondTransfer) query += " AND "
        }

        // Hide the positive transfers
        if (!includeSecondTransfer) {
            query += "NOT (T.${colTransferTransactionID} > 0 AND T.${colAmount} > 0)"
        }

        query += " ORDER BY T.${colDate} DESC"
        if (limit != null) {
            query += " LIMIT $limit"
        }
        val cursor = sqlDB!!.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val selectedID = cursor.getInt(cursor.getColumnIndex(colID))
                val categoryID = cursor.getInt(cursor.getColumnIndex(colCategoryID))
                val name = cursor.getString(cursor.getColumnIndex(colName))
                val details = cursor.getString(cursor.getColumnIndex(colDetails))
                val date = cursor.getString(cursor.getColumnIndex(colDate))
                val amount = cursor.getDouble(cursor.getColumnIndex(colAmount))
                val transferID = cursor.getInt(cursor.getColumnIndex(colTransferTransactionID))

                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                cal.time = sdf.parse(date)

                listTransactions.add(
                    Transaction(
                        selectedID,
                        selectCategory(categoryID),
                        name,
                        details,
                        cal,
                        amount,
                        transferID
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listTransactions
    }

    /**
     * Updates a [Transaction] object stored in the database.
     *
     * @param transaction A [Transaction] object to update.
     * @param selection Query representing the account to update.
     * @param selectionArgs The [selection] arguments.
     * @return Number of rows updated.
     */
    fun updateTransaction(
        transaction: Transaction,
        selection: String,
        selectionArgs: Array<String>
    ): Int {
        val values = ContentValues()
        values.put(colCategoryID, transaction.category.categoryID)
        values.put(colName, transaction.merchant)
        values.put(colDetails, transaction.details)
        values.put(colDate, Timestamp(transaction.date.timeInMillis).toString())
        values.put(colAmount, transaction.amount)
        values.put(colTransferTransactionID, transaction.transferTransactionID)

        return sqlDB!!.update(dbTransactionTable, values, selection, selectionArgs)
    }

    /**
     * Deletes a [Transaction] object stored in the database.
     *
     * @param selectionArgs The arguments used to select the transaction to delete (transaction ID).
     */
    fun deleteTransaction(selectionArgs: Array<String>) {
        val transaction = selectTransaction(selectionArgs[0].toInt())
        val payments = selectPayments(selectionArgs[0].toInt(), "transaction")
        for (payment in payments.indices) {
            // Get account for the payment and update the balance
            val account = payments[payment].account
            account.balance = account.balance - payments[payment].amount
            updateAccount(account, "$colID = ?", arrayOf(account.accountID.toString()))

            // Delete the payment
            sqlDB!!.delete(
                dbPaymentsTable, "$colTransactionID = ? AND $colAccountID = ?",
                arrayOf(selectionArgs[0], account.accountID.toString())
            )
        }

        // Now delete the transaction
        sqlDB!!.delete(dbTransactionTable, "$colID = ?", arrayOf(selectionArgs[0]))

        if (transaction.transferTransactionID != 0) {
            // This is a transfer, delete the linked transaction
            deleteTransaction(arrayOf(transaction.transferTransactionID.toString()))
        }
    }

    /**
     * Inserts a [Payment] object into the database.
     *
     * @param payment The [Payment] to insert.
     * @return The ID that the payment has been saved as.
     */
    fun insertPayment(payment: Payment): Long {
        val values = ContentValues()
        values.put(colTransactionID, payment.transaction.transactionID)
        values.put(colAccountID, payment.account.accountID)
        values.put(colAmount, payment.amount)

        val insert = sqlDB!!.insert(dbPaymentsTable, "", values)
        if (insert > 0) {
            val account = payment.account
            account.balance = account.balance + payment.amount
            updateAccount(account, "$colID = ?", arrayOf(account.accountID.toString()))
        }
        return insert
    }

    /**
     * Gets a single [Payment] from the database based on its transaction ID and account ID.
     *
     * @param transactionID The transaction ID of the [Payment] to read.
     * @param accountID The account ID of the [Payment] to read.
     * @return The [Payment] object with the specified ID, else null if a payment is not found.
     */
    private fun selectPayment(transactionID: Int, accountID: Int): Payment? {
        val selectionArgs = arrayOf(transactionID.toString(), accountID.toString())

        val query = "SELECT P.${colTransactionID}, P.${colAccountID}, P.${colAmount} " +
                "FROM $dbPaymentsTable P " +
                "WHERE P.${colTransactionID} = ? AND P.${colAccountID} = ?"
        val cursor = sqlDB!!.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            val selectedTransactionID = cursor.getInt(cursor.getColumnIndex(colTransactionID))
            val selectedAccountID = cursor.getInt(cursor.getColumnIndex(colAccountID))
            val amount = cursor.getDouble(cursor.getColumnIndex(colAmount))

            return Payment(
                selectTransaction(selectedTransactionID),
                selectAccount(selectedAccountID),
                amount
            )
        }
        cursor.close()
        return null
    }

    /**
     * Gets an [ArrayList] of [Payment] objects from the database based on its transaction ID or
     * account ID.
     *
     * @param id The transaction ID or account ID of the [Payment] objects to read.
     * @param type Get [Payment] objects based on transaction or account.
     * @return An [ArrayList] of [Payment] objects
     */
    fun selectPayments(id: Int, type: String): ArrayList<Payment> {
        val selectionArgs = arrayOf(id.toString())
        val listPayments = ArrayList<Payment>()

        var query = "SELECT P.${colID}, P.${colTransactionID}, " +
                "P.${colAccountID}, P.${colAmount} FROM $dbPaymentsTable P " +
                "JOIN $dbTransactionTable T ON T.${colID} = P.${colTransactionID} " +
                "JOIN $dbAccountTable A ON A.${colID} = P.${colAccountID} "

        query += if (type == "transaction") {
            // Get by transaction ID
            "WHERE T.${colID} = ? ORDER BY A.${colName} ASC"
        } else {
            // Get by account ID
            "WHERE P.${colAccountID} = ? ORDER BY T.${colDate} DESC"
        }

        val cursor = sqlDB!!.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val transactionID = cursor.getInt(cursor.getColumnIndex(colTransactionID))
                val accountID = cursor.getInt(cursor.getColumnIndex(colAccountID))
                val amount = cursor.getDouble(cursor.getColumnIndex(colAmount))

                listPayments.add(
                    Payment(
                        selectTransaction(transactionID),
                        selectAccount(accountID),
                        amount
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listPayments
    }

    /**
     * Updates a [Payment] object stored in the database.
     *
     * @param payment A [Payment] object to update.
     * @param selection Query representing the account to update.
     * @param selectionArgs The [selection] arguments.
     * @return Number of rows updated.
     */
    fun updatePayment(payment: Payment, selection: String, selectionArgs: Array<String>): Int {
        var result = 0
        val existingPayment = selectPayment(
            payment.transaction.transactionID,
            payment.account.accountID
        )

        // If this payment exists, update it
        if (existingPayment != null) {
            val initialAmount = existingPayment.amount
            val values = ContentValues()
            values.put(colTransactionID, payment.transaction.transactionID)
            values.put(colAccountID, payment.account.accountID)
            values.put(colAmount, payment.amount)

            result = sqlDB!!.update(dbPaymentsTable, values, selection, selectionArgs)
            if (result > 0) {
                // If the new payment amount is 0, delete it
                if (payment.amount == 0.0) {
                    delete(
                        dbPaymentsTable, arrayOf(
                            payment.transaction.transactionID.toString(),
                            payment.account.accountID.toString()
                        )
                    )
                }
                // Update the account's balance where this payment was made
                val account = payment.account
                account.balance = account.balance - initialAmount + payment.amount
                updateAccount(account, "$colID = ?", arrayOf(account.accountID.toString()))
            }
        } else if (payment.amount != 0.0) {
            // Adding a new payment for this transaction, insert it
            result = insertPayment(payment).toInt()
        }
        return result
    }

    /**
     * Updates a [Payment] object when updating a transfer.
     *
     * @param accountID The account ID before changes.
     * @param initialAmount The amount before changes.
     * @param newPayment A [Payment] object containing the new data to keep.
     * @param swap True if the source/destination account becomes the opposite after changes.
     * @return Number of rows updated.
     */
    fun updatePaymentTransfer(
        accountID: Int,
        initialAmount: Double,
        newPayment: Payment,
        swap: Boolean
    ): Int {
        println("Updating payment")
        println(" - old payment $initialAmount for account $accountID")
        println(" - new payment ${newPayment.amount} for account ${newPayment.account.accountID} " +
                "and trans ${newPayment.transaction.transactionID}")

        // Initialise variables
        val transactionID = newPayment.transaction.transactionID
        val selection = "TransactionID=? AND AccountID=?"
        val selectionArgs = arrayOf(
            transactionID.toString(),
            accountID.toString()
        )

        // Get values ready to update the payment
        val values = ContentValues()
        values.put(colTransactionID, transactionID)
        values.put(colAccountID, newPayment.account.accountID)
        values.put(colAmount, newPayment.amount)

        val result = sqlDB!!.update(dbPaymentsTable, values, selection, selectionArgs)
        if (result > 0) {
            // Update the account's balance where this payment was made
            val account = newPayment.account
            val oldBal = account.balance

            when {
                newPayment.account.accountID == accountID -> {
                    // Same account
                    account.balance = account.balance - initialAmount + newPayment.amount
                }
                swap -> {
                    // Source is now destination (or vice versa)
                    account.balance = account.balance + initialAmount + newPayment.amount
                }
                else -> {
                    // Another uninvolved account
                    account.balance = account.balance + newPayment.amount
                }
            }
            println("new account balance $oldBal -> ${account.balance}")
            updateAccount(account, "$colID = ?", arrayOf(account.accountID.toString()))
        }
        return result
    }

    /**
     * Generic database delete method.
     *
     * @param table The table to delete a record from.
     * @param selectionArgs The selection arguments.
     */
    fun delete(table: String, selectionArgs: Array<String>) {
        when (table) {
            dbAccountTable -> {
                // Get all transactions for this account that are safe to delete
                // This is any transaction that has only been paid for by only this account
                var query = "SELECT T.* FROM $dbTransactionTable T " +
                        "JOIN $dbPaymentsTable P ON P.${colTransactionID} = T.${colID} " +
                        "WHERE P.${colAccountID} = ? AND T.${colID} NOT IN (" +
                        "    SELECT TID FROM ( " +
                        "        SELECT T.${colID} AS TID FROM $dbTransactionTable T " +
                        "        JOIN $dbPaymentsTable P ON P.${colTransactionID} = T.${colID} " +
                        "        GROUP BY P.${colTransactionID} " +
                        "        HAVING COUNT(*) > 1 ) AS TID ) " +
                        "AND T.${colID} NOT IN ( " +
                        "    SELECT TID FROM ( " +
                        "        SELECT T.${colID} AS TID FROM $dbTransactionTable T " +
                        "        WHERE T.${colTransferTransactionID} != 0) AS TID )"
                var cursor = sqlDB!!.rawQuery(query, selectionArgs)
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(cursor.getColumnIndex(colID))
                        sqlDB!!.delete(
                            dbTransactionTable, "$colID = ?",
                            arrayOf(id.toString())
                        )
                        sqlDB!!.delete(
                            dbPaymentsTable, "$colTransactionID = ? AND $colAccountID = ?",
                            arrayOf(id.toString(), selectionArgs.toString())
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()
                // Hide the account, cannot delete as there may be shared transactions
                hideAccount(selectionArgs)
                /*
                   Remove any orphaned transactions
                   These are any that were paid by multiple accounts and this is the last of those
                   accounts to be deleted, so delete all payments and transactions under these
                   accounts
                   This also includes any transfers where both accounts have been deleted
                */
                val selectionArgsDel: Array<String> =
                    arrayOf(selectionArgs[0], selectionArgs[0], selectionArgs[0])
                query = "SELECT T.$colID FROM $dbTransactionTable T " +
                        "JOIN $dbPaymentsTable P ON P.$colTransactionID = T.$colID " +
                        "JOIN $dbAccountTable A ON A.$colID = P.$colAccountID " +
                        "WHERE (A.$colActive = 0 OR A.$colID = ?) AND (T.$colID IN ( " +
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
                        "OR T.$colID IN ( " +
                        "    SELECT T1.$colID FROM $dbTransactionTable T1 " +
                        "    JOIN $dbPaymentsTable P1 ON P1.$colTransactionID = T1.$colID " +
                        "    JOIN $dbAccountTable A1 ON A1.$colID = P1.$colAccountID " +
                        "    JOIN $dbTransactionTable T2 ON T1.$colTransferTransactionID = T2.$colID " +
                        "    JOIN $dbPaymentsTable P2 ON P2.$colTransactionID = T2.$colID " +
                        "    JOIN $dbAccountTable A2 ON A2.$colID = P2.$colAccountID " +
                        "    WHERE A1.$colActive = 0 AND A2.$colActive = 0 " +
                        "    AND (A1.$colID = ? OR A2.$colID = ?) ) ) " +
                        "GROUP BY T.$colID"
                cursor = sqlDB!!.rawQuery(query, selectionArgsDel)
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(cursor.getColumnIndex(colID))
                        sqlDB!!.delete(
                            dbTransactionTable, "$colID = ?",
                            arrayOf(id.toString())
                        )
                        sqlDB!!.delete(
                            dbPaymentsTable, "$colTransactionID = ?",
                            arrayOf(id.toString())
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            dbCategoryTable -> {
                // If we are deleting a category, remove all of its transactions first
                val result = sqlDB!!.delete(
                    dbTransactionTable, "$colCategoryID = ?",
                    selectionArgs
                )
                if (result >= 0) {
                    sqlDB!!.delete(dbCategoryTable, "$colID = ?", selectionArgs)
                }
            }
            dbPaymentsTable -> {
                sqlDB!!.delete(
                    dbPaymentsTable, "$colTransactionID = ? AND $colAccountID = ?",
                    selectionArgs
                )
            }
        }
    }
}
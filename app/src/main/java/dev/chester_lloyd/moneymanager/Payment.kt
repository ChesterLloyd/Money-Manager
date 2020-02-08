package dev.chester_lloyd.moneymanager

class Payment {
    var paymentID: Int = 0
    var transaction = Transaction()
    var account = Account()
    var amount = 0.0

    constructor(paymentID: Int, transaction: Transaction, account: Account, amount: Double) {
        this.paymentID = paymentID
        this.transaction = transaction
        this.account = account
        this.amount = amount
    }

    constructor() {
        this.paymentID = 0
    }

}
package dev.chester_lloyd.moneymanager

class Account {
    var accountID: Int? = null
    var name: String? = null
    var balance: Double? = null
    var icon: Int = 0
    var colour: Int = 0

    constructor(accountID: Int, name: String, balance: Double, icon: Int, colour: Int) {
        this.accountID = accountID
        this.name = name
        this.balance = balance
        this.icon = icon
        this.colour = colour
    }

    constructor() {
        this.accountID = 4
    }

//  Return object as a string
    override fun toString() :String {
        return String.format("ID: %d, Name: %s, Balance: %.2f, Icon: %d, Colour: %d", accountID, name, balance, icon, colour)
    }
}
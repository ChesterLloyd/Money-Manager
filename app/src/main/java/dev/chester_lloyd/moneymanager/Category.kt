package dev.chester_lloyd.moneymanager

class Category {
    var categoryID: Int = 0
    var name: String = ""
    var icon: Int = 0
    var colour: Int = 0

    constructor(categoryID: Int, name: String, icon: Int, colour: Int) {
        this.categoryID = categoryID
        this.name = name
        this.icon = icon
        this.colour = colour
    }

    constructor() {
        this.categoryID = 0
    }

}
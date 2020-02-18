package dev.chester_lloyd.moneymanager.ui

import android.content.Context
import dev.chester_lloyd.moneymanager.R

class IconManager {

    var colourNames = arrayOf<String>()
    var accountNames = arrayOf<String>()
    var categoryNames = arrayOf<String>()

    var colourIcons = arrayOf<Icon>()
    var accountIcons = arrayOf<Icon>()
    var categoryIcons = arrayOf<Icon>()

    constructor(context: Context) {

//      Setup the arrays that store the icons

        colourNames = context.resources.getStringArray(R.array.colour_names)
        colourIcons = arrayOf(
            Icon(0, R.drawable.ic_circle_green, colourNames[0]),
            Icon(1, R.drawable.ic_circle_dark_blue, colourNames[1]),
            Icon(2, R.drawable.ic_circle_paypal, colourNames[2]))

        accountNames = context.resources.getStringArray(R.array.account_names)
        accountIcons = arrayOf(
            Icon(0, R.drawable.ic_account_bank, accountNames[0]),
            Icon(1, R.drawable.ic_account_business, accountNames[1]),
            Icon(2, R.drawable.ic_account_cash, accountNames[2]),
            Icon(3, R.drawable.ic_account_credit_card, accountNames[3]),
            Icon(4, R.drawable.ic_account_dollar, accountNames[4]),
            Icon(5, R.drawable.ic_account_gift_card, accountNames[5]),
            Icon(6, R.drawable.ic_account_joint, accountNames[6]),
            Icon(7, R.drawable.ic_account_membership_card, accountNames[7]),
            Icon(8, R.drawable.ic_account_paypal, accountNames[8]),
            Icon(9, R.drawable.ic_account_travel_card, accountNames[9]),
            Icon(10, R.drawable.ic_account_wallet, accountNames[10]))

        categoryNames = context.resources.getStringArray(R.array.category_names)
        categoryIcons = arrayOf(
            Icon(0, R.drawable.ic_category_bar, categoryNames[0]),
            Icon(1, R.drawable.ic_category_bills_lightbulb, categoryNames[1]),
            Icon(2, R.drawable.ic_category_bills_lightbulb_2, categoryNames[2]),
            Icon(3, R.drawable.ic_category_bills_power, categoryNames[3]),
            Icon(4, R.drawable.ic_category_business, categoryNames[4]),
            Icon(5, R.drawable.ic_category_cake, categoryNames[5]),
            Icon(6, R.drawable.ic_category_camera, categoryNames[6]),
            Icon(7, R.drawable.ic_category_computer_cloud, categoryNames[7]),
            Icon(8, R.drawable.ic_category_computer_laptop, categoryNames[8]),
            Icon(9, R.drawable.ic_category_computer_phone, categoryNames[9]),
            Icon(10, R.drawable.ic_category_computer_servers, categoryNames[10]),
            Icon(11, R.drawable.ic_category_computer_storage, categoryNames[11]),
            Icon(12, R.drawable.ic_category_fridge, categoryNames[12]),
            Icon(13, R.drawable.ic_category_people_child, categoryNames[13]),
            Icon(14, R.drawable.ic_category_people_people, categoryNames[14]),
            Icon(15, R.drawable.ic_category_people_person, categoryNames[15]),
            Icon(16, R.drawable.ic_category_places_cafe, categoryNames[16]),
            Icon(17, R.drawable.ic_category_places_cinema, categoryNames[17]),
            Icon(18, R.drawable.ic_category_places_dining, categoryNames[18]),
            Icon(19, R.drawable.ic_category_places_event, categoryNames[19]),
            Icon(20, R.drawable.ic_category_places_florist, categoryNames[20]),
            Icon(21, R.drawable.ic_category_places_home, categoryNames[21]),
            Icon(22, R.drawable.ic_category_places_hotel, categoryNames[22]),
            Icon(23, R.drawable.ic_category_places_mail, categoryNames[23]),
            Icon(24, R.drawable.ic_category_places_pharmacy, categoryNames[24]),
            Icon(25, R.drawable.ic_category_places_pizza, categoryNames[25]),
            Icon(26, R.drawable.ic_category_places_receipt, categoryNames[26]),
            Icon(27, R.drawable.ic_category_places_restaurant, categoryNames[27]),
            Icon(28, R.drawable.ic_category_places_school, categoryNames[28]),
            Icon(29, R.drawable.ic_category_shopping_basket, categoryNames[29]),
            Icon(30, R.drawable.ic_category_shopping_cart, categoryNames[30]),
            Icon(31, R.drawable.ic_category_shopping_estore, categoryNames[31]),
            Icon(32, R.drawable.ic_category_shopping_store, categoryNames[32]),
            Icon(33, R.drawable.ic_category_sports_golf, categoryNames[33]),
            Icon(34, R.drawable.ic_category_sports_swim, categoryNames[34]),
            Icon(35, R.drawable.ic_category_stationary, categoryNames[35]),
            Icon(36, R.drawable.ic_category_stationary_printer, categoryNames[36]),
            Icon(37, R.drawable.ic_category_subscription_dvr, categoryNames[37]),
            Icon(38, R.drawable.ic_category_subscription_movie, categoryNames[38]),
            Icon(39, R.drawable.ic_category_subscription_music, categoryNames[39]),
            Icon(40, R.drawable.ic_category_subscription_ondemand, categoryNames[40]),
            Icon(41, R.drawable.ic_category_subscription_radio, categoryNames[41]),
            Icon(42, R.drawable.ic_category_subscription_subscriptions, categoryNames[42]),
            Icon(43, R.drawable.ic_category_subscriptions_book, categoryNames[43]),
            Icon(44, R.drawable.ic_category_ticket, categoryNames[44]),
            Icon(45, R.drawable.ic_category_transport_bus, categoryNames[45]),
            Icon(46, R.drawable.ic_category_transport_car, categoryNames[46]),
            Icon(47, R.drawable.ic_category_transport_flight, categoryNames[47]),
            Icon(48, R.drawable.ic_category_transport_motorbike, categoryNames[48]),
            Icon(49, R.drawable.ic_category_transport_station_ev, categoryNames[49]),
            Icon(50, R.drawable.ic_category_transport_station_gas, categoryNames[50]),
            Icon(51, R.drawable.ic_category_transport_subway, categoryNames[51]),
            Icon(52, R.drawable.ic_category_transport_taxi, categoryNames[52]),
            Icon(53, R.drawable.ic_category_transport_train, categoryNames[53]),
            Icon(54, R.drawable.ic_category_work, categoryNames[54]))
    }

//  Returns an icon given its database storage ID
    fun getIconByID(iconArray: Array<Icon>, id: Int):Icon {
        for (icon in 0..iconArray.size - 1) {
            if (iconArray[icon].id == id) {
                return iconArray[icon]
            }
            println("ICON COUNT - > " + icon)
        }
        return colourIcons[0]
    }

//  Returns an icons position in a given array given its database storage ID
//  Useful when setting the icons position in a spinner
    fun getIconPositionID(iconArray: Array<Icon>, id: Int):Int {
        for (icon in 0..iconArray.size - 1) {
            if (iconArray[icon].id == id) {
                return icon
            }
        }
        return 0
    }

}
package com.jawnnypoo.openmeh.shared.extension

import com.jawnnypoo.openmeh.shared.model.Deal
import java.text.NumberFormat
import java.util.*

private val PRICE_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US)
private const val PATH_CHECKOUT = "/checkout"

fun Deal.getPriceRange(): String {
    val items = items.sortedBy { it.price }
    if (items.size == 1) {
        return PRICE_FORMATTER.format(items.first().price)
    }
    val lowestPrice = items.first().price
    val highestPrice = items.last().price
    //Same price between highest and lowest, just show the one price
    return if (lowestPrice == highestPrice) {
        PRICE_FORMATTER.format(items.first().price)
    } else PRICE_FORMATTER.format(lowestPrice) + "-" + PRICE_FORMATTER.format(highestPrice)
}

fun Deal.getCheckoutUrl(): String {
    return url + PATH_CHECKOUT
}

fun Deal.isSoldOut(): Boolean {
    return soldOutAt != null
}

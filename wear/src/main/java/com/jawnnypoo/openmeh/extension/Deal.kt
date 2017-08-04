package com.jawnnypoo.openmeh.extension

import android.text.TextUtils
import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Item
import java.text.NumberFormat
import java.util.*

private val PRICE_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US)
private val PATH_CHECKOUT = "/checkout"

fun Deal.getPriceRange(): String {
    if (items.size == 1) {
        return PRICE_FORMATTER.format(items[0].price)
    }
    Collections.sort<Item>(items)
    val lowestPrice = items[0].price
    val highestPrice = items[items.size - 1].price
    //Same price between highest and lowest, just show the one price
    if (lowestPrice == highestPrice) {
        return PRICE_FORMATTER.format(items[0].price)
    }
    return PRICE_FORMATTER.format(items[0].price) + "-" + PRICE_FORMATTER.format(items[items.size - 1].price)
}

fun Deal.getCheckoutUrl(): String {
    return url + PATH_CHECKOUT
}

fun Deal.isSoldOut(): Boolean {
    return !TextUtils.isEmpty(soldOutAt)
}

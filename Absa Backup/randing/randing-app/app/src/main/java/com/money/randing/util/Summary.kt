package com.money.randing.util

import com.money.randing.domain.model.Person
import com.money.randing.domain.model.SummaryData
import kotlin.math.absoluteValue

fun calculateSummaryData(list: List<Person>): SummaryData {
    var balance = 0.0
    var positive = 0.0
    var negative = 0.0
    list.forEach { person ->
        val total = person.total
        if (total != null) {
            balance += total

            if (total > 0) {
                positive += total
            } else if (total < 0) {
                negative += total.absoluteValue
            }
        }
    }

    return SummaryData(balance, positive, negative)
}
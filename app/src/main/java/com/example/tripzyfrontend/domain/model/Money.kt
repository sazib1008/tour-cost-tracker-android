package com.example.tripzyfrontend.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class Money(
    val paisa: Long = 0L,
    val currency: String = "BDT"
) : Comparable<Money> {

    val symbol: String
        get() = when (currency.uppercase()) {
            "BDT" -> "৳"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "INR" -> "₹"
            else -> currency
        }

    fun formatted(showTrailingZeroes: Boolean = false): String {
        val major = paisa / 100.0
        val symbols = DecimalFormatSymbols(Locale.US)
        val pattern = if (showTrailingZeroes || paisa % 100 != 0L) "#,##0.00" else "#,##0"
        val df = DecimalFormat(pattern, symbols)
        return "$symbol${df.format(major)}"
    }

    operator fun plus(other: Money): Money {
        require(this.currency == other.currency) { "Cannot add different currencies: ${this.currency} and ${other.currency}" }
        return Money(this.paisa + other.paisa, this.currency)
    }

    operator fun minus(other: Money): Money {
        require(this.currency == other.currency) { "Cannot subtract different currencies: ${this.currency} and ${other.currency}" }
        return Money(this.paisa - other.paisa, this.currency)
    }

    operator fun unaryMinus(): Money = Money(-this.paisa, this.currency)

    override fun compareTo(other: Money): Int {
        require(this.currency == other.currency) { "Cannot compare different currencies: ${this.currency} and ${other.currency}" }
        return this.paisa.compareTo(other.paisa)
    }

    companion object {
        fun ofPaisa(paisa: Long, currency: String = "BDT"): Money = Money(paisa, currency)
        fun ofMajor(major: Double, currency: String = "BDT"): Money = Money((major * 100).toLong(), currency)
        val ZERO = Money(0L, "BDT")
    }
}

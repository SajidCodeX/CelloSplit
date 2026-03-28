package com.cellosplit.app.core.utils

/**
 * Formats paise amounts into display strings.
 * All formatting is purely presentational — never used in calculations.
 *
 * Examples:
 *   54500L  → "₹545.00"
 *   100L    → "₹1.00"
 *   1L      → "₹0.01"
 *   0L      → "₹0.00"
 */
object MoneyFormatter {

    fun format(paise: Long): String {
        val isNegative = paise < 0
        val absPaise = Math.abs(paise)
        val rupees = absPaise / 100
        val cents  = absPaise % 100
        val sign   = if (isNegative) "-" else ""
        return "${sign}₹${rupees}.${cents.toString().padStart(2, '0')}"
    }

    /** Returns just the integer-rupee portion as a string (for large hero display). */
    fun formatRupees(paise: Long): String = (Math.abs(paise) / 100).toString()

    /** Returns the cents part padded to 2 digits (e.g. "07", "00", "45"). */
    fun formatPaise(paise: Long): String = (Math.abs(paise) % 100).toString().padStart(2, '0')

    /** Sign prefix for color-coding: + or - */
    fun sign(paise: Long): String = if (paise >= 0) "+" else "-"
}

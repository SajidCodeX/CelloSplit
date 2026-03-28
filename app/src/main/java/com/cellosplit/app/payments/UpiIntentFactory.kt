package com.cellosplit.app.payments

import android.content.Intent
import android.net.Uri
import java.util.Locale

object UpiIntentFactory {

    fun createPaymentIntent(
        payeeVpa: String,
        payeeName: String,
        amountPaise: Long,
        transactionNote: String,
        transactionRef: String
    ): Intent {
        val amountRupees = String.format(Locale.US, "%.2f", amountPaise / 100.0)

        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", payeeVpa)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("am", amountRupees)
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("tn", transactionNote)
            .appendQueryParameter("tr", transactionRef)
            .build()

        return Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }
    }
}

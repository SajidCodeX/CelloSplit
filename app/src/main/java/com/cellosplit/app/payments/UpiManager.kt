package com.cellosplit.app.payments

import android.content.Intent
import android.net.Uri
import java.util.Locale

object UpiManager {

    /**
     * Builds the standard UPI intent for Indian Payment apps (GPay, PhonePe, Paytm, etc.)
     */
    fun createUpiIntent(
        upiId: String,
        payeeName: String,
        amountRupees: Double,
        transactionNote: String,
        transactionRefId: String
    ): Intent {
        val amountString = String.format(Locale.US, "%.2f", amountRupees)

        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId) // Payee Address (UPI ID)
            .appendQueryParameter("pn", payeeName) // Payee Name
            .appendQueryParameter("mc", "") // Merchant Code (optional)
            .appendQueryParameter("tr", transactionRefId) // Transaction Ref ID
            .appendQueryParameter("tn", transactionNote) // Transaction Note
            .appendQueryParameter("am", amountString) // Amount (formatted)
            .appendQueryParameter("cu", "INR") // Currency
            .build()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        
        // We use createChooser to ensure the OS presents the list of installed UPI apps.
        return Intent.createChooser(intent, "Pay securely with:")
    }

    /**
     * Parses the onActivityResult data from a UPI intent.
     * Most UPI apps return data in a tightly coupled query string format.
     * Example: txnId=AXI...&responseCode=00&Status=SUCCESS&txnRef=...
     */
    fun parseUpiResponse(responseString: String?): UpiResult {
        if (responseString.isNullOrBlank()) {
            return UpiResult.Failed("No response from payment app. Payment may have been cancelled.")
        }

        // Map the query string response
        val params = responseString.split("&").associate { pair ->
            val parts = pair.split("=")
            if (parts.size >= 2) parts[0].uppercase(Locale.ROOT) to parts[1]
            else "" to ""
        }

        val status = params["STATUS"]?.uppercase(Locale.ROOT) ?: ""
        val responseCode = params["RESPONSECODE"]
        val txnId = params["TXNID"]
        val txnRef = params["TXNREF"]

        return when {
            status == "SUCCESS" || status == "SUBMITTED" -> {
                UpiResult.Success(txnId, txnRef)
            }
            status == "FAILURE" -> {
                UpiResult.Failed("Transaction Failed (Code: $responseCode)")
            }
            else -> UpiResult.Cancelled
        }
    }
}

sealed class UpiResult {
    data class Success(val transactionId: String?, val transactionRef: String?) : UpiResult()
    data class Failed(val message: String) : UpiResult()
    object Cancelled : UpiResult()
}

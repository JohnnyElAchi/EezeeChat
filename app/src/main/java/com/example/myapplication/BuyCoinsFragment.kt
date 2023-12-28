package com.johnnyelachi

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import androidx.fragment.app.Fragment



class BuyCoinsFragment : DialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedViewModel: SharedViewModel
    private var billingClient: BillingClient? = null
    private var skuDetailsList: List<SkuDetails>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        return inflater.inflate(R.layout.fragment_buy_coins, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBillingClient()
        setClickListeners(view)
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    val purchasedTokens = mutableListOf<String>() // Initialize an empty list to store purchase tokens

                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            val selectedSkuId = determineSkuId(purchase) // Implement your logic to determine the SKU
                            val skuDetails = findSkuDetails(selectedSkuId)

                            val packNumber = getPackNumberFromSkuId(skuDetails!!.sku)
                            val minutesPurchased = skuDetails.description.toIntOrNull() ?: 0

                            if (skuDetails != null) {
                                showConfirmationDialog(packNumber, minutesPurchased, purchase.purchaseToken)
                                purchasedTokens.add(purchase.purchaseToken) // Add the purchase token to the list
                            }
                        }
                    }
                }
                else {
                    showConfirmationDialogNeg()
                }

            }
            .build()

        connectToGooglePlayBilling()
    }

    private fun determineSkuId(purchase: Purchase): String {
        // Check for the purchased SKU from the purchase data
        if (purchase.skus.contains("five_minutes")) {
            return "five_minutes"
        } else if (purchase.skus.contains("ten_minutes")) {
            return "ten_minutes"
        } else if (purchase.skus.contains("twentyfive_minutes")) {
            return "twentyfive_minutes"
        } else if (purchase.skus.contains("fifty_minutes")) {
            return "fifty_minutes"
        }else if (purchase.skus.contains("one_month")) {
            return "one_month"
        }

        // Default to "five_minutes" if the purchased SKU is not recognized
        return " "
    }

    private fun setClickListeners(view: View) {
        val pack1Layout = view.findViewById<RelativeLayout>(R.id.pack1)
        val pack2Layout = view.findViewById<RelativeLayout>(R.id.pack2)
        val pack3Layout = view.findViewById<RelativeLayout>(R.id.pack3)
        val pack4Layout = view.findViewById<RelativeLayout>(R.id.pack4)
        val subsc4Layout = view.findViewById<RelativeLayout>(R.id.subsc4)

        pack1Layout.setOnClickListener {
            initiatePurchase("five_minutes")
        }

        pack2Layout.setOnClickListener {
            initiatePurchase("ten_minutes")
        }

        pack3Layout.setOnClickListener {
            initiatePurchase("twentyfive_minutes")
        }

        pack4Layout.setOnClickListener {
            initiatePurchase("fifty_minutes")
        }

        subsc4Layout.setOnClickListener {
            initiatePurchaseSUBS("one_month")
        }
    }

    private fun initiatePurchase(skuId: String) {
        val skuDetails = findSkuDetails(skuId)

        if (skuDetails != null) {
            val billingParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()

            billingClient?.launchBillingFlow(requireActivity(), billingParams)
        } else {
            showToast("Unable to find product details")
        }
    }

    private fun initiatePurchaseSUBS(skuId: String) {
        val skuDetails = findSkuDetails(skuId)

        if (skuDetails != null) {
            val billingParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()

            billingClient?.launchBillingFlow(requireActivity(), billingParams)
        } else {
            showToast("Unable to find or initiate subscription details")
        }
    }

    private fun connectToGooglePlayBilling() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                connectToGooglePlayBilling()
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    getProductDetails()
                } else {
                    showToast("Billing setup failed: ${billingResult.debugMessage}")
                }
            }
        })
    }

    private fun getProductDetails() {
        val productIds = listOf("five_minutes", "ten_minutes", "twentyfive_minutes", "fifty_minutes")
        val subsId = listOf("one_month")

        val getSubsDetailsQuery = SkuDetailsParams.newBuilder()
            .setSkusList(subsId)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        val getProductDetailsQuery = SkuDetailsParams.newBuilder()
            .setSkusList(productIds)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient?.querySkuDetailsAsync(getSubsDetailsQuery) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                this.skuDetailsList = skuDetailsList
            } else {
                showToast("Failed to retrieve subscription details: ${billingResult.debugMessage}")
            }
        }

        billingClient?.querySkuDetailsAsync(getProductDetailsQuery) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                this.skuDetailsList = skuDetailsList
            } else {
                showToast("Failed to retrieve product details: ${billingResult.debugMessage}")
            }
        }
    }

    private fun purchasePack(packNumber: Int, minutesPurchased: Int) {
        // Retrieve the current total purchased minutes from SharedPreferences
        val totalPurchasedMinutes = sharedPreferences.getInt("userPurchasedMinutes", 0)

        // Calculate the new total by adding the purchased minutes
        val newTotalMinutes = totalPurchasedMinutes + minutesPurchased

        // Store the new total purchased minutes in SharedPreferences
        sharedPreferences.edit().putInt("userPurchasedMinutes", newTotalMinutes).apply()

        // Update the numberOfCoins in your SharedViewModel
        sharedViewModel.addPurchasedMinutes(minutesPurchased)

        // Save the updated coin balance to SharedPreferences (if needed)
        // Note: You should adjust this part based on your application's logic
        sharedPreferences.edit().putInt("userCoinsBalance", sharedViewModel.numberOfCoins).apply()

        // Record the purchase information
        val purchaseRecord = PurchaseRecord(packNumber, minutesPurchased, System.currentTimeMillis())
        savePurchaseRecord(purchaseRecord)
    }

    data class PurchaseRecord(
        val packNumber: Int,
        val minutesPurchased: Int,
        val purchaseDateTime: Long // Use System.currentTimeMillis() to get the current time
    )

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    private fun savePurchaseRecord(purchaseRecord: PurchaseRecord) {
        // Retrieve existing purchase records from SharedPreferences
        val purchaseRecords = getPurchaseRecords()

        // Add the new purchase record
        purchaseRecords.add(purchaseRecord)

        // Save the updated list of purchase records to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("purchaseRecords", Gson().toJson(purchaseRecords))
        editor.apply()
    }

    private fun getPurchaseRecords(): MutableList<PurchaseRecord> {
        val purchaseRecordsJson = sharedPreferences.getString("purchaseRecords", "")
        return if (purchaseRecordsJson.isNullOrEmpty()) {
            mutableListOf()
        } else {
            Gson().fromJson(purchaseRecordsJson, object : TypeToken<List<PurchaseRecord>>() {}.type)
        }
    }

    private fun showSubscriptionConfirmationDialog(subscriptionDetails: SkuDetails, purchaseToken: String) {
        // Implement the subscription confirmation dialog here
        // You can customize it based on your requirements
        // For example:
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Subscribed!")
            .setMessage("You are now subscribed to ${subscriptionDetails.title}!")
            .setPositiveButton("Done") { _, _ ->
                // Handle subscription logic if needed
            }
            .create()

        alertDialog.show()
    }

    private fun showConfirmationDialog(packNumber: Int, minutesPurchased: Int, purchaseToken: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Purchased!")
            .setMessage("Your purchase $minutesPurchased minutes for pack$packNumber was successfully done!")
            .setPositiveButton("Done") { _, _ ->
                // User confirmed the purchase, proceed with purchase logic
                purchasePack(packNumber, minutesPurchased)

                // Send purchase receipt email (if needed)
                // val purchaseRecord = PurchaseRecord(packNumber, minutesPurchased, System.currentTimeMillis())
                // sendPurchaseReceipt(purchaseRecord)

                // Consume the purchase
                consumePurchase(purchaseToken)

                // Refresh the fragment (if needed)
//                val fragments = requireActivity().supportFragmentManager.fragments
//                val fragmentToRefresh = fragments.firstOrNull { it is SwipeleftrightFragment }
//
//                fragmentToRefresh?.let {
//                    // Fragment found, refresh it
//                    requireActivity().supportFragmentManager.beginTransaction()
//                        .detach(it)
//                        .attach(it)
//                        .commit()
//
//                    // Log for debugging
//                    Log.d("FragmentRefresh", "Fragment refreshed successfully")
//                } ?: run {
//                    // Fragment not found, log an error
//                    Log.e("FragmentRefresh", "SwipeleftrightFragment not found or null")
//                }

            }
            .create()

        alertDialog.show()
    }





    private fun showConfirmationDialogNeg() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Not Purchased!")
            .setMessage("Your purchase was Unsuccessful!\n\nUnexpected Error while trying to purchase the package.\nTry again later")
            .setPositiveButton("Done") { _, _ ->
                // User confirmed the purchase, proceed with purchase logic
            }
            .create()

        alertDialog.show()
    }

    private fun getPackNumberFromSkuId(skuId: String): Int {
        // Your SKU IDs are assumed to be in the format "packX" where X is the pack number
        // Extract the pack number by splitting the SKU ID and converting the last part to an integer
        val parts = skuId.split("_")
        if (parts.size >= 2) {
            val packNumberStr = parts.last()
            try {
                return packNumberStr.toInt()
            } catch (e: NumberFormatException) {
                // Handle any parsing errors or return a default value
            }
        }
        // Return a default value or handle the case where the SKU ID doesn't match the expected format
        return -1 // Default value, you can choose a different default value
    }

    private fun sendPurchaseReceipt(purchaseRecord: PurchaseRecord) {
        // Replace these values with your email and password
        val fromEmail = "elachijohny@gmail.com"
        val password = "Johnny700&80"

        // Retrieve the authenticated user's email address from Google Sign-In
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val userEmail = account?.email

        if (userEmail.isNullOrEmpty()) {
            showToast("Unable to retrieve user's email address.")
            return
        }

        val ownerEmail = "elachijohny@gmail.com"

        try {
            // Construct the user email
            val userMessage = """
        Dear $userEmail,
        
        Thank you for your purchase!
        
        Pack Number: ${purchaseRecord.packNumber}
        Minutes Purchased: ${purchaseRecord.minutesPurchased}
        Purchase Date and Time: ${purchaseRecord.purchaseDateTime}
    """.trimIndent()

            // Send the user email
            sendEmail(fromEmail, password, userEmail, "Purchase Receipt", userMessage)

            // Construct the owner email
            val ownerMessage = """
        User $userEmail Purchased item ${purchaseRecord.packNumber}!
        
        Pack Number: ${purchaseRecord.packNumber}
        Minutes Purchased: ${purchaseRecord.minutesPurchased}
        Purchase Date and Time: ${purchaseRecord.purchaseDateTime}
    """.trimIndent()

            // Send the owner email
            sendEmail(fromEmail, password, ownerEmail, "Owner Purchase Receipt", ownerMessage)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }


    }

    private fun sendEmail(fromEmail: String, password: String, toEmail: String, subject: String, messageBody: String) {
        Thread {
            try {
                val props = Properties()
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.socketFactory.port"] = "465"
                props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.port"] = "465"

                val session = Session.getDefaultInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(fromEmail, password)
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress(fromEmail))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(toEmail))
                message.subject = subject
                message.setText(messageBody)

                // Send the email
                Transport.send(message)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun consumePurchase(purchaseToken: String) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.consumeAsync(consumeParams) { billingResult, purchaseToken ->
            // Handle the billingResult and purchaseToken
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    // Purchase successfully consumed
                    // You may want to perform additional actions here
                    // after a successful consumption, if needed.
                }
                else -> {
                    // Handle other response codes if necessary
                    // For example, log an error or notify the user about the failure
                    // You might want to add more detailed error handling here.
                    Log.e("Billing", "Failed to consume purchase. Response code: ${billingResult.responseCode}")
                }
            }
        }
    }




    private fun findSkuDetails(skuId: String): SkuDetails? {
        return skuDetailsList?.find { it.sku == skuId }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        billingClient?.endConnection()
    }
}

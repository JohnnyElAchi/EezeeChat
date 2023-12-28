package com.johnnyelachi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.johnnyelachi.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerifyCodeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_code)

        auth = FirebaseAuth.getInstance()

        // Retrieve the verificationId from the previous activity
        verificationId = intent.getStringExtra("verificationId") ?: ""

        val codeEditText: EditText = findViewById(R.id.codeEditText)
        val verifyButton: TextView = findViewById(R.id.verifyButton)

        verifyButton.setOnClickListener {
            val createProfileFragment = CreateProfileFragment()
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentvc, createProfileFragment)
            transaction.addToBackStack(null)
            transaction.commit()

//            if (verificationId.isNotEmpty() && code.isNotEmpty()) {
//                // Create a PhoneAuthCredential using the verification ID and code entered by the user
//                val credential = PhoneAuthProvider.getCredential(verificationId, code)
//
//                // Sign in with the credential
//                auth.signInWithCredential(credential)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val intent = Intent(this, LoginLoading::class.java)
//                            startActivity(intent)
//                            finish()
//                        } else {
//                            val exception = task.exception
//                            if (exception != null) {
//                                Log.e("VerifyCodeActivity", "Verification failed: ${exception.message}")
//                            }
//                        }
//                    }
//            } else {
//                // Handle the case where verificationId or code is empty.
//                codeEditText.error = "Verification code is required."
//            }
        }
    }
}

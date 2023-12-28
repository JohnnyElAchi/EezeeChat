package com.johnnyelachi

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.johnnyelachi.R

class Second : AppCompatActivity() {

    private lateinit var menuButton: ImageView
    private lateinit var backbtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        menuButton = findViewById(R.id.menuButton)
        backbtn = findViewById(R.id.backbtn)
        backbtn.setOnClickListener{
            finish()
        }

        val CallButton = this.findViewById<View>(R.id.callguy)
        CallButton.setOnClickListener {
            val intent = Intent(this, CallGuyActivity::class.java)
            startActivity(intent)
        }

        menuButton.setOnClickListener { v ->
            val popupMenu = PopupMenu(this, v)
            popupMenu.menuInflater.inflate(R.menu.menu_zero, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_favorite -> {
                        true
                    }
                    R.id.action_block -> {
                        true
                    }
                    R.id.action_report -> {
                        showReportDialog()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

    }
    private fun showReportDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Report User")

        val input = EditText(this)
        input.hint = "Enter your report"
        alertDialogBuilder.setView(input)

        alertDialogBuilder.setPositiveButton("Send") { dialog, _ ->
            val reportText = input.text.toString()
            sendReportByEmail(reportText)
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun sendReportByEmail(reportText: String) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("johnyelachi@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Report")
        emailIntent.putExtra(Intent.EXTRA_TEXT, reportText)

        try {
            startActivity(Intent.createChooser(emailIntent, "Send report via email..."))
        } catch (ex: ActivityNotFoundException) {
            // Handle the case where no email app is installed
        }
    }
}
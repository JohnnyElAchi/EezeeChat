package com.johnnyelachi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.johnnyelachi.R

class CallGirlActivity : AppCompatActivity() {

    private lateinit var ringingTextView: TextView

    private val handler = Handler()
    private var elapsedTimeInSeconds = 0

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            val hours = elapsedTimeInSeconds / 3600
            val minutes = (elapsedTimeInSeconds % 3600) / 60
            val seconds = elapsedTimeInSeconds % 60
            val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            ringingTextView.text = formattedTime
            elapsedTimeInSeconds++
            handler.postDelayed(this, 1000) // Update every 1 second (1000 milliseconds)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_girl)

        ringingTextView = findViewById(R.id.ringing)

        // Start the timer
        handler.post(updateTimerRunnable)


        val endCallButton = findViewById<View>(R.id.endCallButton)
        endCallButton.setOnClickListener {
            // Stop the timer
            handler.removeCallbacks(updateTimerRunnable)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        }
    }
}
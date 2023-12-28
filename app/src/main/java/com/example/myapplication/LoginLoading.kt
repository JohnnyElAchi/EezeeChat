package com.johnnyelachi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.johnnyelachi.R


class LoginLoading : AppCompatActivity() {

    private var progBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_loading)

        progBar = findViewById(R.id.progressBar)


        val h = Handler()
        h.postDelayed(
            Runnable {
                Log.d("LoginLoading", "Transitioning to MainActivity")
                val intent = Intent(this@LoginLoading, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            3500
        )

    }
}
package com.johnnyelachi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupMenu

import com.johnnyelachi.R

class Zero : AppCompatActivity() {

    private lateinit var menuButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Hide both the navigation bar and the status bar.
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // Keep the screen on while the app is running.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        setContentView(R.layout.activity_zero)

        menuButton = findViewById(R.id.menuButton)



        menuButton.setOnClickListener { v ->
            val popupMenu = PopupMenu(this, v)
            popupMenu.menuInflater.inflate(R.menu.menu_zero, popupMenu.menu)

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_favorite -> {
                        true
                    }
                    R.id.action_block -> {
                        // Handle "Block" action
                        // Add your logic here
                        true
                    }
                    R.id.action_report -> {
                        // Handle "Report" action
                        // Add your logic here
                        true
                    }
                    else -> false
                }
            }

            // Show the popup menu
            popupMenu.show()
        }

        }

    private fun addToFav(){


    }

    private fun getCVById(){



    }



}

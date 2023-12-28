package com.johnnyelachi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class HelpFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnClickListener {
            val recipientEmail = "info@eezeechat.com" // Replace with your company's email
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$recipientEmail")

            // Use Intent.createChooser to display app options
            val chooser = Intent.createChooser(intent, "Send Email via...")
            startActivity(chooser)
        }
    }

    // Other methods and UI elements in your fragment...
}

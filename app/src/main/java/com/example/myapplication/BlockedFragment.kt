package com.johnnyelachi

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.johnnyelachi.R


class BlockedFragment : Fragment() {

    private lateinit var blockedPerson: Person // The blocked person's data
    private lateinit var sharedViewModel: SharedViewModel


    companion object {
        fun newInstance(person: Person): BlockedFragment {
            val fragment = BlockedFragment()
            val args = Bundle()
            args.putParcelable("blockedPerson", person)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blocked, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Retrieve the user's purchased minutes from SharedPreferences
        val buyCoins = view.findViewById<View>(R.id.buycoins)
        val numberOfCoinsTextView = view.findViewById<TextView>(R.id.numberOfCoins)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Retrieve the user's coin balance and purchased minutes from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)
        val userCoinsBalance = sharedPreferences.getInt("userCoinsBalance", 0)
        val userPurchasedMinutes = sharedPreferences.getInt("userPurchasedMinutes", 0)

        // Update the SharedViewModel with the retrieved balance
        sharedViewModel.numberOfCoins = userCoinsBalance

        // Display the purchased minutes in numberOfCoinsTextView
        numberOfCoinsTextView.text = "$userPurchasedMinutes"

        buyCoins.setOnClickListener {
            // Call the function to handle the purchase
            showBuyCoinsActivity()
        }


        return view
    }

    // Storing purchased minutes
    private fun handlePurchase(purchasedMinutes: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userPurchasedMinutes = sharedPreferences.getInt("userPurchasedMinutes", 0)
        val newPurchasedMinutes = userPurchasedMinutes + purchasedMinutes
        editor.putInt("userPurchasedMinutes", newPurchasedMinutes)
        editor.apply()
    }

    // Retrieving purchased minutes
    private fun retrievePurchasedMinutes(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userPurchasedMinutes", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val numberOfCoinsTextView = view.findViewById<TextView>(R.id.numberOfCoins)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Retrieve the user's coin balance and purchased minutes from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)
        val userCoinsBalance = sharedPreferences.getInt("userCoinsBalance", 0)
        val userPurchasedMinutes = sharedPreferences.getInt("userPurchasedMinutes", 0)

        // Update the SharedViewModel with the retrieved balance
        sharedViewModel.numberOfCoins = userCoinsBalance

        // Display the purchased minutes in numberOfCoinsTextView
        numberOfCoinsTextView.text = "$userPurchasedMinutes"
        retrievePurchasedMinutes()


    }

    private fun showBuyCoinsActivity() {
        val dialogFragment = BuyCoinsFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        dialogFragment.show(fragmentManager, "BuyCoinsFragment")
        retrievePurchasedMinutes()
    }

}

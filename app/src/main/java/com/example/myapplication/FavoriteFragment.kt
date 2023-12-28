package com.johnnyelachi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johnnyelachi.CallGirlActivity
import com.johnnyelachi.CallGuyActivity
import com.johnnyelachi.R
import java.lang.reflect.Array.newInstance
import javax.xml.datatype.DatatypeFactory.newInstance

class FavoriteFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var lastCalledPerson: Person? = null
    private val favoriteCards = mutableListOf<Person>()
    private lateinit var sharedViewModel: SharedViewModel


    private var blockUserCallback: BlockUserCallback? = null

    private lateinit var favoriteViewModel: SwipeleftrightViewModel

    private val placeholderGirlImageResId = R.drawable.girlpic
    private val placeholderGuyImageResId = R.drawable.guypic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val firstCardView = view.findViewById<CardView>(R.id.first)
        val secondCardView = view.findViewById<CardView>(R.id.second)
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


        val parentActivity = activity as? First
        // Replace YourHostingActivity with the actual hosting activity's class


        firstCardView.setOnClickListener {
            // Create the current user
            // Create an intent to launch the First activity
            val intent = Intent(requireContext(), First::class.java)
            intent.putExtra("userId", R.id.first) // Replace favoriteFragmentReference with the actual reference
            startActivity(intent)

        }

        secondCardView.setOnClickListener {
            val intent = Intent(requireContext(), Second::class.java)
            startActivity(intent)
        }

        val CallButton = view.findViewById<View>(R.id.CallButton1)
        CallButton.setOnClickListener {
            val intent = Intent(requireContext(), CallGirlActivity::class.java)
            startActivity(intent)
            updateLastCalledPerson(Person("Jane Doe", "27 Years", "R.drawable.girlpic", true))
        }

        val CallButton2 = view.findViewById<View>(R.id.CallButton2)
        CallButton2.setOnClickListener {
            val intent = Intent(requireContext(), CallGuyActivity::class.java)
            startActivity(intent)
            updateLastCalledPerson(Person("John Doe", "22 Years", "R.drawable.guypic", false))
        }


//        favoriteViewModel = ViewModelProvider(requireActivity()).get(SwipeleftrightViewModel::class.java)
//
//        // Observe changes to the list of favorite users
//        favoriteViewModel.favoriteUsers.observe(viewLifecycleOwner, Observer { favoriteUsers ->
//            // Update the UI with the list of favorite users
//            // You can use a RecyclerView and Adapter to display the list
//        })

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

    }

//    private fun getLastCalledPerson(): Person? {
//        val sharedPreferences = requireContext().getSharedPreferences("LastCalledPerson", Context.MODE_PRIVATE)
//        val name = sharedPreferences.getString("name", "")
//        val age = sharedPreferences.getString("age", "")
//        val isGirl = sharedPreferences.getBoolean("isGirl", false) // Default to false if not found
//        val imageResId = sharedPreferences.getString("imageResId", "R.drawable.eezeechatapplogo") // Retrieve the image resource ID
//
//        return if (name!!.isNotBlank() && age!!.isNotBlank()) {
//            Person(name, age, imageResId, isGirl)
//        } else {
//            null
//        }
//    }


    private fun updateLastCalledPerson(person: Person) {
        val sharedPreferences = requireContext().getSharedPreferences("LastCalledPerson", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("name", person.name)
        editor.putString("age", person.age)
        editor.putBoolean("isGirl", person.isGirl)

        editor.apply()
    }

    fun setBlockUserCallback(callback: BlockUserCallback) {
        blockUserCallback = callback
    }

    fun blockUser(person: Person) {
        // Remove the card from favorites
        favoriteCards.remove(person)

        // Pass the blocked card to the BlockedFragment
        blockUserCallback?.onBlockUser(person)
    }
    interface BlockUserCallback {
        fun onBlockUser(person: Person)
    }

    private fun showBuyCoinsActivity() {
        val dialogFragment = BuyCoinsFragment()
        dialogFragment.show(parentFragmentManager, "BuyCoinsFragment")
        retrievePurchasedMinutes()
    }


}

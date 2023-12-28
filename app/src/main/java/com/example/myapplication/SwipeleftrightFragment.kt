package com.johnnyelachi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import com.google.firebase.auth.FirebaseAuth
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.SharedPrefsManager
import kotlin.math.abs
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SwipeleftrightFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gestureDetector: GestureDetector
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModel: SwipeleftrightViewModel
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val coinsDatabaseReference: DatabaseReference = database.getReference("user_coins")



    private fun storeLastCalledPerson(person: Person) {
        val preferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("lastCalledName", person.name)
        editor.putString("lastCalledAge", person.age)
        editor.putString("lastCalledImageResId", person.imageResId)
        editor.apply()
    }
    private fun updatePurchasedMinutes(newPurchasedMinutes: Int?) {
        val purchasedMinutesTextView = view?.findViewById<TextView>(R.id.numberOfCoins)
        purchasedMinutesTextView?.text = "$newPurchasedMinutes"

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            // Save the new value to Firebase Realtime Database using the user's UID as the key
            coinsDatabaseReference.child(uid).setValue(newPurchasedMinutes)
        }

        // Update the SharedViewModel with the new value
        sharedViewModel.numberOfPurchasedMinutes.value = newPurchasedMinutes
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedPrefsManager = SharedPrefsManager(requireContext())

        val view = inflater.inflate(R.layout.fragment_swipeleftright, container, false)
        viewModel = ViewModelProvider(this).get(SwipeleftrightViewModel::class.java)

        val swipeMessageView = view.findViewById<View>(R.id.swipeMessageView)
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


/*
        // Display the purchased minutes in numberOfCoinsTextView
        numberOfCoinsTextView?.text = "$userPurchasedMinutes"*/

        buyCoins.setOnClickListener {
            // Call the function to handle the purchase
            showBuyCoinsActivity()
        }

//        // Retrieve the user's purchased minutes from SharedPreferences
//        val sharedPreferences = requireContext().getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)
//        val userPurchasedMinutes = sharedPreferences.getInt("userPurchasedMinutes", 0)
//
//        // Update the SharedViewModel with the retrieved purchased minutes
//        sharedViewModel.numberOfPurchasedMinutes.value = userPurchasedMinutes
//
//
//        // Display the purchased minutes in numberOfCoinsTextView
//        numberOfCoinsTextView?.text = "$userPurchasedMinutes"
//
//
//        buyCoins.setOnClickListener {
//            showBuyCoinsActivity()
//        }

        gestureDetector = GestureDetector(requireContext(), SwipeGestureListener())

        swipeMessageView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)

            // Check for a click (ACTION_UP) event
            if (event.action == MotionEvent.ACTION_UP) {
                swipeMessageView.performClick()
            }

            true // Return true to indicate that the touch event was handled
        }

        return view
    }

    private fun content() {
        // Update your fragment's content here
        // For example, update UI, fetch data, etc.

        // Schedule the next refresh after a delay (e.g., every 1000 milliseconds)
        refresh(1000)
    }

    private fun refresh(milliseconds: Long) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                content() // Refresh the content
            }
        }
        handler.postDelayed(runnable, milliseconds)
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

        content()

        retrievePurchasedMinutes()
    }
    override fun onResume() {
        super.onResume()
        retrievePurchasedMinutes()
    }

        private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
        val numberOfCoinsTextView = view?.findViewById<TextView>(R.id.numberOfCoins)


        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            val userCoinsBalance = sharedPreferences.getInt("userCoinsBalance", 0)
            if(userCoinsBalance <= 0){
                showBuyCoinsActivity()
                return false
            }

            if (abs(diffX) > abs(diffY) &&
                abs(diffX) > SWIPE_THRESHOLD &&
                abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
            ) {
                // Here, you can determine the swipe direction
                if (diffX > 0) {
                    // Swipe to the right
                    val person = Person("John Doe", "27 Years", "@drawable/guypic", true)
                    storeLastCalledPerson(person)
                    val intent = Intent(requireContext(), CallGuyActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    requireActivity().finish()

                    // Decrement numberOfCoins by 1
                    val newCoinBalance = userCoinsBalance - 1
                    sharedPreferences.edit().putInt("userCoinsBalance", newCoinBalance).apply()

                    sharedViewModel.addPurchasedMinutes(1) // Increment purchased minutes by 1
                    updatePurchasedMinutes(sharedViewModel.numberOfPurchasedMinutes.value)

                    // Handle other logic related to swiping right
                } else {
                    // Swipe to the left
                    val person = Person("Jane Doe", "27 Years", "@drawable/girlpic", true)
                    storeLastCalledPerson(person)
                    val intent = Intent(requireContext(), CallGirlActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    requireActivity().finish()

                    // Decrement numberOfCoins by 1
                    val newCoinBalance = userCoinsBalance - 1
                    sharedPreferences.edit().putInt("userCoinsBalance", newCoinBalance).apply()

                    sharedViewModel.addPurchasedMinutes(1) // Increment purchased minutes by 1
                    updatePurchasedMinutes(sharedViewModel.numberOfPurchasedMinutes.value)

                }
                return true
            }
            return false
        }
    }

    private fun showBuyCoinsActivity() {
        val dialogFragment = BuyCoinsFragment()
        dialogFragment.show(parentFragmentManager, "BuyCoinsFragment")
    }

}
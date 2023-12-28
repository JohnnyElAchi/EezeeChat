package com.johnnyelachi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johnnyelachi.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var usernameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var interestedInTextView: TextView
    private lateinit var aboutMeTextView: TextView
    private lateinit var profileImageView: ImageView

    private lateinit var addImageButton: ImageView
    private lateinit var galleryRecyclerView: RecyclerView
    private val galleryImages = mutableListOf<Uri>() // Store selected gallery images

    companion object {
        //lateinit var sharedPreferences: SharedPreferences
        private const val GALLERY_REQUEST_CODE = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //sharedPreferences = requireContext().getSharedPreferences("profile_image", Context.MODE_PRIVATE)
        addImageButton = view.findViewById(R.id.addImageButton)
        galleryRecyclerView = view.findViewById(R.id.galleryRecyclerView)
        profileImageView = view.findViewById(R.id.profileImageView)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        galleryRecyclerView.layoutManager = layoutManager
        val adapter = ImageAdapter(galleryImages)
        galleryRecyclerView.adapter = adapter

        addImageButton.setOnClickListener {
            openGalleryForImage()
        }

        val userProfileImageUri = getStoredUserProfileImageUri()

        if (userProfileImageUri != null) {
            profileImageView.setImageURI(userProfileImageUri)
        }

        // Initialize views
        usernameTextView = view.findViewById(R.id.usernameTextView)
        ageTextView = view.findViewById(R.id.ageTextView)
        genderTextView = view.findViewById(R.id.genderTextView)
        interestedInTextView = view.findViewById(R.id.interestedInTextView)
        aboutMeTextView = view.findViewById(R.id.aboutMeTextView)

        // Fetch and display user profile data
        fetchUserProfileData()

        return view
    }

    private fun fetchUserProfileData() {
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.uid?.let { userId ->
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)

                        // Populate UI elements with user profile data
                        usernameTextView.text = userProfile?.name
                        ageTextView.text = userProfile?.age
                        genderTextView.text = userProfile?.gender
                        interestedInTextView.text = userProfile?.interests
                        aboutMeTextView.text = userProfile?.aboutMe
                    }
                }
                .addOnFailureListener { e ->
                    // Handle any errors here
                }
        }
    }

    private fun getStoredUserProfileImageUri(): Uri? {
//        if (sharedPreferences != null) {
//            val uriString = sharedPreferences.getString("profile_image_uri", null)
//            return uriString?.let { Uri.parse(it) }
//        }
        return null
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                // Add the selected image URI to the galleryImages list
                galleryImages.add(selectedImageUri)
                // Notify the adapter that the data set has changed
                galleryRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }



}

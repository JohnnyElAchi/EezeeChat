package com.johnnyelachi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.johnnyelachi.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class CreateProfileFragment : Fragment() {
    private lateinit var profileImageView: ImageButton
    private lateinit var nameEditText: EditText
    private lateinit var usernameTextView: TextView
    private lateinit var userabout: EditText
    private lateinit var age: EditText
    private lateinit var saveButton: TextView
    private lateinit var changeImageButton: ImageButton

    companion object {
        private const val GALLERY_REQUEST_CODE = 1
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_profile, container, false)

        nameEditText = view.findViewById(R.id.NameEditText)
        usernameTextView = view.findViewById(R.id.username)
        userabout = view.findViewById(R.id.aboutedt)
        saveButton = view.findViewById(R.id.saveButton)
        changeImageButton = view.findViewById(R.id.imageButton)
        age = view.findViewById(R.id.ageEditText)

        val genderRadioGroup = view.findViewById<RadioGroup>(R.id.genderRadioGroup)
        val interestRadioGroup = view.findViewById<RadioGroup>(R.id.interestRadioGroup)

        genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male -> {
                    // User selected Male
                    // You can store or process this information as needed
                    val selectedGender = "Male"
                }
                R.id.female -> {
                    // User selected Female
                    // You can store or process this information as needed
                    val selectedGender = "Female"
                }
            }
        }


        val user = FirebaseAuth.getInstance().currentUser
        user?.displayName?.let { displayName ->
            usernameTextView.text = displayName
        }

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                usernameTextView.text = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


        changeImageButton.setOnClickListener {
            // Handle the "Change Profile Picture" button click event
            openGalleryForImage()
        }

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            val newAbout = userabout.text.toString()
            val newAge = age.text.toString()

            val selectedGender = when (genderRadioGroup.checkedRadioButtonId) {
                R.id.male -> "Male"
                R.id.female -> "Female"
                else -> "" // Handle other cases as needed
            }

            val selectedInterest = when (interestRadioGroup.checkedRadioButtonId) {
                R.id.intmale -> "Male"
                R.id.intfemale -> "Female"
                else -> "" // Handle other cases as needed
            }

            if (newName.isNotEmpty() && selectedGender.isNotEmpty()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userPhoneNumber = user.phoneNumber ?: ""
                            saveProfileDataToFirestore(newName, userPhoneNumber, newAge, selectedGender, selectedInterest, newAbout)

                            Toast.makeText(
                                requireContext(),
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Navigate to the next screen or perform other actions
                            // For example, you can navigate to the main app screen
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to update profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter a name and select a gender.", Toast.LENGTH_SHORT).show()
            }
        }


        return view
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
                // Load the selected image into your profileImageView
                changeImageButton.setImageURI(selectedImageUri)
            }
        }
    }

    private fun saveProfileDataToFirestore(name: String, phoneNumber: String, age: String, gender: String, interests: String, aboutMe: String) {
        val db = FirebaseFirestore.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {

            val user = UserProfile(
                userId,
                name,
                phoneNumber,
                age,
                gender,
                interests,
                aboutMe
            )

            val userDocRef = db.collection("users").document(userId)

            userDocRef.set(user)
                .addOnSuccessListener {
                    // Data has been successfully saved
                    Toast.makeText(
                        requireContext(),
                        "Profile data saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->

                    Toast.makeText(
                        requireContext(),
                        "Error saving profile data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}

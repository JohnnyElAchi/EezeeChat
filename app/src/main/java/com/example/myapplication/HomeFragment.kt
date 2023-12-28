package com.johnnyelachi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johnnyelachi.R


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val firstCardView = view.findViewById<CardView>(R.id.first)
        val secondCardView = view.findViewById<CardView>(R.id.second)


        firstCardView.setOnClickListener {
            val intent = Intent(requireContext(), First::class.java)
            startActivity(intent)
        }

        secondCardView.setOnClickListener {
            val intent = Intent(requireContext(), Second::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)


        firstCardView.setOnClickListener {
            val intent = Intent(requireContext(), First::class.java)
            startActivity(intent)
        }


        val CallButton = view.findViewById<View>(R.id.CallButton)
        CallButton.setOnClickListener {
            val intent = Intent(requireContext(), CallGirlActivity::class.java)
            startActivity(intent)
        }

        val CallButton2 = view.findViewById<View>(R.id.CallButton2)
        CallButton.setOnClickListener {
            val intent = Intent(requireContext(), CallGirlActivity::class.java)
            startActivity(intent)
        }

        val CallButton1 = view.findViewById<View>(R.id.CallButton2)
        CallButton1.setOnClickListener {
            val intent = Intent(requireContext(), CallGuyActivity::class.java)
            startActivity(intent)
        }

        val profileIconImageView = view.findViewById<ImageView>(R.id.profileIcon)
        profileIconImageView.setOnClickListener {
            navigateToProfileFragment(it)
        }

        return view

    }

    fun navigateToProfileFragment(view: View) {
        val fragment = ProfileFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.homefragment, fragment) // Replace with your fragment container ID
        transaction.addToBackStack(null)
        transaction.commit()
    }


}

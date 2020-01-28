package com.example.orstedwidget

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.jetbrains.anko.find

class WelcomeFrag : Fragment() {

    lateinit var welcomeText: TextView
    lateinit var logOutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.frag_welcome_screen, container, false)

        val sharedPref = context!!.getSharedPreferences(resources.getString(R.string.sharedPrefs_log_in_key), Context.MODE_PRIVATE)
        val firstName = sharedPref.getString(resources.getString(R.string.first_name_key), resources.getString(R.string.empty_text))

        //finding text view and setting first name as text
        welcomeText = layout.findViewById(R.id.welcome_title)
        welcomeText.text = resources.getString(R.string.welcome, firstName)

        //log out button on click listener
        logOutButton = layout.findViewById(R.id.welcome_log_out_button)
        logOutButton.setOnClickListener {

        }

        return layout
    }

}
package com.example.orstedwidget

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.frag_log_in.*

class LogInFrag : Fragment() {
    lateinit var logInButton: Button

    lateinit var email: String
    lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.frag_log_in, container, false)

        //if the user is already logged in we skip the loading screen and jump to welcome page
        val sharedPrefs = context!!.getSharedPreferences(resources.getString(R.string.sharedPrefs_log_in_key), Context.MODE_PRIVATE)
        val isLoggedIn: Boolean = sharedPrefs.getBoolean(resources.getString(R.string.is_logged_in_key), false)
        if(isLoggedIn) {
            fragmentManager!!.beginTransaction()
                .replace(R.id.main_frameLayout, WelcomeFrag())
                .commit()
        }

        logInButton = layout.findViewById(R.id.log_in_button)

        //on click listener
        logInButton.setOnClickListener {
            if(!validate()) {
                println("validation error")
            } else {
                this.email = log_in_email.text.toString()
                this.password = log_in_password.text.toString()

                println("i shared: $email og $password")
                val sharedPrefs: SharedPreferences = context!!.getSharedPreferences(getString(R.string.sharedPrefs_log_in_key), Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPrefs.edit()
                editor.putString(getString(R.string.email_key), email)
                editor.putString(getString(R.string.password_key), password)
                editor.apply()

                fragmentManager!!.beginTransaction()
                    .replace(R.id.main_frameLayout, LoadingFrag())
                    .commit()
            }
        }

        return layout
    }

    fun validate(): Boolean {
        var valid = true

        //check for email
        val email = log_in_email.text.toString()
        if(email.isEmpty()) {
            log_in_email.error = "Email required"
            valid = false
        } else {
            log_in_email.error = null
        }

        //check for password
        val password = log_in_password.text.toString()
        if(password.isEmpty()) {
            log_in_password.error = "Password required"
            valid = false
        } else {
            log_in_password.error = null
        }

        return valid
    }

}
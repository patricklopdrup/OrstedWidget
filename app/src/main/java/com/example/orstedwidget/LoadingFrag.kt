package com.example.orstedwidget

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

class LoadingFrag : Fragment() {

    lateinit var email: String
    lateinit var password: String

    lateinit var firstName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.frag_loading_log_in, container, false)

        val sharedPref = context!!.getSharedPreferences(
            resources.getString(R.string.sharedPrefs_log_in_key),
            Context.MODE_PRIVATE
        )
        email = sharedPref.getString(
            resources.getString(R.string.email_key),
            resources.getString(R.string.empty_text)
        )!!
        password = sharedPref.getString(
            resources.getString(R.string.password_key),
            resources.getString(R.string.empty_text)
        )!!

        CoroutineScope(Main).launch {
            try {
                val result = withContext(IO) {
                    authenticate(email, password)
                }
                firstName = result.getValue("first_name")
                println("firstname: $firstName")

                //adding first name to shared prefs
                val editor = sharedPref.edit()
                editor.putString(resources.getString(R.string.first_name_key), firstName)
                editor.apply()

                //
                fragmentManager!!.beginTransaction()
                    .replace(R.id.main_frameLayout, WelcomeFrag())
                    .commit()

            //when we get a server error
            } catch (e: FileNotFoundException) {
                println(resources.getString(R.string.user_not_found))
                Toast.makeText(context, resources.getString(R.string.user_not_found), Toast.LENGTH_LONG).show()
                fragmentManager!!.beginTransaction()
                    .replace(R.id.main_frameLayout, LogInFrag())
                    .commit()
            }
        }

        return layout
    }

}
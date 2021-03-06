package com.example.orstedwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RemoteViews
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeFrag : Fragment() {

    lateinit var welcomeTitle: TextView
    lateinit var weekUsage: TextView
    lateinit var logOutButton: Button
    lateinit var updateButton: Button
    lateinit var email: String
    lateinit var password: String

    lateinit var sharedPref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.frag_welcome_screen, container, false)

        sharedPref = context!!.getSharedPreferences(resources.getString(R.string.sharedPrefs_log_in_key), Context.MODE_PRIVATE)
        editor = sharedPref.edit()

        //setting the user as "logged in" in sharedprefs
        editor.putBoolean(resources.getString(R.string.is_logged_in_key), true)
        editor.apply()

        //getting first name from sharedprefs
        val firstName = sharedPref.getString(resources.getString(R.string.first_name_key), resources.getString(R.string.empty_text))
        email = sharedPref.getString(resources.getString(R.string.email_key), resources.getString(R.string.empty_text))!!
        password = sharedPref.getString(resources.getString(R.string.password_key), resources.getString(R.string.empty_text))!!

        //finding text view and setting first name as text
        welcomeTitle = layout.findViewById(R.id.welcome_title)
        welcomeTitle.text = resources.getString(R.string.welcome, firstName)

        //setting saved (old) data on screen from sharedprefs
        weekUsage = layout.findViewById(R.id.welcome_week_usage_text)
        val oldKwhData = sharedPref.getFloat(resources.getString(R.string.kwh_this_week_key), 0.0f)
        val oldPrefix = sharedPref.getString(resources.getString(R.string.consumption_prefix_key), resources.getString(R.string.loading_text))
        weekUsage.text = resources.getString(R.string.week_usage_kwh, oldPrefix, oldKwhData)
        //retrieving new data and updating data on screen
        getThisWeek()

        //log out button on click listener
        logOutButton = layout.findViewById(R.id.welcome_log_out_button)
        logOutButton.setOnClickListener {
            //clear the sharedprefs (also means clearing the "is_logged_in" boolean)
            editor.clear()
            editor.apply()

            //go to login page
            fragmentManager!!.beginTransaction()
                .replace(R.id.main_frameLayout, LogInFrag())
                .commit()
        }

        //Update the widget within the app
        updateButton = layout.findViewById(R.id.welcome_update_widget_button)
        updateButton.setOnClickListener {
            println("klikker på update")
            val appwidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context!!, WidgetListProvider::class.java)
            val appWidgetIds = appwidgetManager.getAppWidgetIds(thisWidget)
            appwidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view)
        }

        return layout
    }

    fun getThisWeek() {
        CoroutineScope(Main).launch {
            val result = withContext(IO) {
                getData(email, password, TimeInterval.weekly)
            }
            val kwhThisWeek: Float = result[result.lastIndex].kWh.toFloat()
            val prefixThisWeek: String = result[result.lastIndex].COPI[resources.getString(R.string.copi_currentConsumptionPrefix)]!!
            //saving new data in sharedprefs
            editor.putFloat(resources.getString(R.string.kwh_this_week_key), kwhThisWeek)
            editor.putString(resources.getString(R.string.consumption_prefix_key), prefixThisWeek)
            editor.apply()
            //showing new data on screen
            weekUsage.text = resources.getString(R.string.week_usage_kwh, prefixThisWeek, kwhThisWeek)
        }
    }

}
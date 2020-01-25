package com.example.orstedwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.RemoteViews
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of App Widget functionality.
 */
class WidgetListProvider : AppWidgetProvider() {

    lateinit var email: String
    lateinit var password: String

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefs_log_in_key),
                Context.MODE_PRIVATE
            )
            email = sharedPref.getString(
                context.getString(R.string.email_key),
                context.getString(R.string.empty_text)
            )!!
            password = sharedPref.getString(
                context.getString(R.string.password_key),
                context.getString(R.string.empty_text)
            )!!

            println("email: $email og pass: $password")

            //if email or password is not set we don't call for the widget
            if (email == context.getString(R.string.empty_text) || password == context.getString(R.string.empty_text)) {
                println("email or pass not set")

                //else we update the widget
            } else {
                //starting coroutine on main thread (UI thread)
                CoroutineScope(Main).launch {
                    println("start launch")
                    //switching to IO background thread and sending HTTP request
                    val result = withContext(IO) {
                        getJsonData(email, password, TimeInterval.daily)
                    }

                    //sequentially after data is retrieved we parse the result in the intent to WidgetListService
                    val serviceIntent = Intent(context, WidgetListService::class.java)
                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    serviceIntent.putExtra(context.getString(R.string.json_data_key), result)
                    //if this was not here and we had more than one widget on the homescreen they would get the same appWidgetId
                    serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

                    val views = RemoteViews(context.packageName, R.layout.widget_list)
                    views.setRemoteAdapter(R.id.widget_list_view, serviceIntent)
                    views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
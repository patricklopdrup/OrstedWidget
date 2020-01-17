package com.example.orstedwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class NumberWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.widget)
    //views.setTextViewText(R.id.appwidget_text, widgetText)



    views.setTextViewText(R.id.blue_button_kwh, "hej")
    val hej = RemoteViews(context.packageName, R.id.blue_button_3)
    println("layout id: ${hej.layoutId}")
    hej.setTextViewText(R.id.blue_button_kwh, "orale")


    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
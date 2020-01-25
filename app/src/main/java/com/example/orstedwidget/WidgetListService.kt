package com.example.orstedwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class WidgetListService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {

        return WidgetItemFactory(applicationContext, intent!!)
    }

    class WidgetItemFactory : RemoteViewsFactory {
        var context: Context
        var appWidgetId: Int = 0
        var jsonData: String?
        lateinit var consumptionList: List<ConsumptionData>

        constructor(context: Context, intent: Intent) {
            this.context = context
            this.appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            this.jsonData = intent.getStringExtra(context.getString(R.string.json_data_key))
        }

        override fun onCreate() {
            println("er i oncreate")
            //parsing the String of data to a list of ConsumptionData
            consumptionList = getConsumptionDataList(jsonData!!)
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun onDataSetChanged() {

        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getViewAt(position: Int): RemoteViews {
            println("getViewAt bliver kaldt")
            val views = RemoteViews(context.packageName, R.layout.widget_list_item)
            views.setTextViewText(R.id.widget_consumption_interval_text, "Ørsted Widget")
            views.setTextViewText(R.id.list_item_prefix, consumptionList[consumptionList.size-1-position].COPI["currentConsumptionPrefix"])
            views.setTextViewText(R.id.list_item_kwh, consumptionList[consumptionList.size-1-position].kWh.toString())
            views.setTextViewText(R.id.list_item_x_label, consumptionList[consumptionList.size-1-position].COPI["xAxisLabel"])
            return views
        }

        override fun getCount(): Int {
            println("er i count")
            return consumptionList.size
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun onDestroy() {

        }

    }
}
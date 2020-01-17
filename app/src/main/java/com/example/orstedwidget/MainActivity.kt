package com.example.orstedwidget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = ""
        val pass = ""

        println("hello")
        CoroutineScope(IO).launch {
            network(email, pass, TimeInterval.weekly)
        }

    }

    suspend fun network(email: String, pass: String, interval: TimeInterval) {

//        val hej = getData(email, pass, interval)
//        hej.forEach { println("hej: $it") }
        val email = ""
        val pass = ""

        //saves the return of the authentication in a Map
        val map = authenticate(email, pass)
        //externalID and token is how you retrieve data from the other API calls
        val externalID = map["external_id"]
        val token = map["token"]

        val weeklyJson: String = getConsumptions(externalID!!, token!!, TimeInterval.weekly)
        println("json stringen er: $weeklyJson")

        println("kwh er: ${getConsumptionDataList(weeklyJson)[4].kWh}")


//        val auth: Map<String, String> = authenticate(email, pass)
//        println("fået auth")
//        val externalID = auth["external_id"]
//        val token = auth["token"]
//        println("id: $externalID og token: $token")
//        setTextOnMainThread(externalID.toString())
        /**
         * virker her til. jeg kan sætte min tekst til min external_id
         */
//        val jsonData: String = getConsumptions(externalID!!, token!!, interval)
//        println("fået json")
//        val consumptionList: List<ConsumptionData> = getConsumptionDataList(jsonData)
//        println("fået list")
//
//        setTextOnMainThread(consumptionList[1].kWh.toString())

    }

    fun setText(input: String) {
        test_id.text = input
    }

    suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setText(input)
        }
    }

}
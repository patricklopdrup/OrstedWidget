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

        CoroutineScope(IO).launch {
            network(email, pass, TimeInterval.monthly)
        }

    }

    suspend fun network(email: String, pass: String, interval: TimeInterval) {
        //authenticate user
        val auth: Map<String, String> = authenticate(email, pass)
        println("fået auth")

        //take externalID and token from auth
        val externalID = auth["external_id"]
        val token = auth["token"]
        println("id: $externalID og token: $token")

        //get the consumption data
        val jsonData: String = getConsumptions(externalID!!, token!!, interval)
        println("fået json")

        //make a list of consumption data
        val consumptionList: List<ConsumptionData> = getConsumptionDataList(jsonData)
        println("fået list")

        setTextOnMainThread(consumptionList[4].kWh.toString())

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
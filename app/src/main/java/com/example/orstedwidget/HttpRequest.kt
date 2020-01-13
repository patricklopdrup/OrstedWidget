package com.example.orstedwidget

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import org.json.JSONArray




fun main() {
    val email = ""
    val pass = ""

    //saves the return of the authentication in a Map
    val map = authenticate(email, pass)
    //externalID and token is how you retrieve data from the other API calls
    val externalID = map["external_id"]
    val token = map["token"]

    val weeklyMap = getConsumptions(externalID!!, token!!, TimeInterval.weekly)
    println("size er: $weeklyMap")

    val consumptions = (((weeklyMap["data"] as ArrayList<*>)[0]) as Map<*, *>)["consumptions"] as ArrayList<*>
    //val consumptions = weeklyMap["data"]!![0]!!["consumptions"] as ArrayList<*>
    println("consumption er: $consumptions")

    println("consumption 3 er ${consumptions[3]}")

    consumptions[0] as Map<*, *>

    println()

    //println("test er: ${(arrayOf(test).contentToString())}")
//    val json = JSONObject(str)
//    val bank = json.getString("bank")
//    tv.append("\n\n=== Oversigt over " + bank + "s kunder ===\n")
//    var totalKredit = 0.0
//
//    val kunder = json.getJSONArray("kunder")
//    val antal = kunder.length()
//    for (i in 0 until antal) {
//        val kunde = kunder.getJSONObject(i)
//        System.err.println("obj = $kunde")
//        val navn = kunde.getString("navn")
//        val kredit = kunde.getDouble("kredit")
//        tv.append(navn + " med " + kredit + " kr.\n")
//        totalKredit = totalKredit + kredit
//    }

}

enum class TimeInterval {
    hourly, daily, weekly, monthly, yearly
}

fun authenticate(email: String, password: String): Map<String, String> {

    //url to authenticate POST method
    val mURL = URL("https://api.obviux.dk/v2/authenticate")

    //make a connection
    val conn = mURL.openConnection() as HttpURLConnection

    conn.requestMethod = "POST"

    //setting headers
    conn.setRequestProperty("authority", "api.obviux.dk")
    conn.setRequestProperty("Accept", "application/json, text/plain, */*")
    conn.setRequestProperty("x-customer-ip", "194.251.71.73")
    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")

    //to get output
    conn.doOutput = true

    //setting the body (in JSON) with credentials
    val jsonInputString = "{\"customer\":\"$email\",\"password\":\"$password\"}"

    conn.outputStream.use { os ->
        val input = jsonInputString.toByteArray(charset("utf-8"))
        os.write(input, 0, input.size)
    }

    //read the data
    val response = StringBuilder()

    BufferedReader(
        InputStreamReader(conn.inputStream, "utf-8")
    ).use { br ->
        var responseLine: String?
        while (br.readLine().also { responseLine = it } != null) {
            response.append(responseLine!!.trim { it <= ' ' })
        }
        println("$response\n")
    }

    //using Gson to parse the JSON response to a Map
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, Any>>() {}.type

    return gson.fromJson(response.toString(), mapType)
}

fun getConsumptions(externalID: String, token: String, interval: TimeInterval): Map<String, String> {
    //url to authenticate POST method
    val mURL = URL("https://prod.copi.obviux.dk/consumptionPage/$externalID/$interval")

    //make a connection
    val conn = mURL.openConnection() as HttpURLConnection

    conn.requestMethod = "GET"

    //setting headers
    conn.setRequestProperty("authority", "prod.copi.obviux.dk")
    conn.setRequestProperty("Accept", "application/json, text/plain, */*")
    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
    conn.setRequestProperty("authorization", token)

    //to get output
    conn.doOutput = true

    //read the data
    val response = StringBuilder()

    val br = BufferedReader(InputStreamReader(conn.inputStream, "utf-8"))
    var responseLine: String?
    while (br.readLine().also { responseLine = it } != null) {
        response.append(responseLine!!.trim())
    }

    //using Gson to parse the JSON response to a Map
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, Any>>() {}.type

    println(response.toString())
    return gson.fromJson(response.toString(), mapType)
}
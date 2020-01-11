package com.example.orstedwidget

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


fun main() {
    val email = ""
    val pass = ""

    val map = authenticate(email, pass)
    val externalID = map["external_id"]
    val authToken = map["token"]

    val weeklyMap = getUsage(externalID!!, authToken!!, TimeInterval.weekly)

    println()
    /**
     * virker ikke
     */
    val weeklyData = weeklyMap["data"].toString()
    println("weekly: $weeklyData")

    val gson = Gson()
    val type = object: TypeToken<Map<String, Any>>(){}.type
    println("type er: $type")
    val newMap: Map<String, Any> = gson.fromJson(weeklyData, type)

    println("\n nyt map: $newMap")
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

fun getUsage(externalID: String, token: String, interval: TimeInterval): Map<String, String> {
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
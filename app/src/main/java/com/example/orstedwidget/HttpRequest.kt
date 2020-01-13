package com.example.orstedwidget

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


data class WeeklyConsumption(
    val start: String,
    val end: String,
    val kWh: Double,
    val COPI: HashMap<String, String>
)

fun main() {
    val email = ""
    val pass = ""

    //saves the return of the authentication in a Map
    val map = authenticate(email, pass)
    //externalID and token is how you retrieve data from the other API calls
    val externalID = map["external_id"]
    val token = map["token"]

    val weeklyJson = getConsumptions(externalID!!, token!!, TimeInterval.weekly)
    println("size er: $weeklyJson")

    //parsing our String to json
    val json = JsonParser().parse(weeklyJson)
    //finding the data array in JSON
    val dataArray = json.asJsonObject["data"].asJsonArray
    //finding the consumptions array at index 0 in dataArray
    val consumptions = dataArray[0].asJsonObject["consumptions"].asJsonArray
    println("consumptions: $consumptions")

    //list of the data classes "WeeklyConsumption"
    val weeklyConsumptions = ArrayList<WeeklyConsumption>()
    //looping through all consumptions element in the json file
    for (i in 0 until consumptions.size()) {
        var start = consumptions[i].asJsonObject["start"].asString
        var end = consumptions[i].asJsonObject["end"].asString
        var kWh = consumptions[i].asJsonObject["kWh"].asDouble

        //COPI is a map in the json file
        var copiMap = HashMap<String, String>()
        var copi = consumptions[i].asJsonObject["COPI"].asJsonObject

        copiMap["xAxisLabel"] = copi["xAxisLabel"].asString
        copiMap["currentConsumptionPrefix"] = copi["currentConsumptionPrefix"].asString
        copiMap["unit"] = copi["unit"].asString

        //adding to the array of the data class "WeeklyConsumption"
        weeklyConsumptions.add(WeeklyConsumption(start, end, kWh, copiMap))
    }

    weeklyConsumptions.forEach { println("hej $it") }

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

fun getConsumptions(externalID: String, token: String, interval: TimeInterval): String {
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

    return response.toString()
    //using Gson to parse the JSON response to a Map
//    val gson = Gson()
//    val mapType = object : TypeToken<Map<String, Any>>() {}.type
//
//    println(response.toString())
//    return gson.fromJson(response.toString(), mapType)
}
package com.example.orstedwidget

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


fun main() {
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

}

enum class TimeInterval {
    hourly, daily, weekly, monthly, yearly
}

fun getData(email: String, password: String, interval: TimeInterval): List<ConsumptionData> {
    val authMap = authenticate(email, password)
    val externalID = authMap["external_id"]
    val token = authMap["token"]

    val jsonData = getConsumptions(externalID!!, token!!, interval)

    return getConsumptionDataList(jsonData)
}

fun getJsonData(email: String, password: String, interval: TimeInterval): String {
    val authMap = authenticate(email, password)
    val externalID = authMap["external_id"]
    val token = authMap["token"]

    return getConsumptions(externalID!!, token!!, interval)
}

/**
 * To authenticate the user with email and password. "external_id" and "token" can be retrieved.
 * Those are used for getting the consumptions
 *
 * @param email the users email
 * @param password the users password
 * @return a json file as a Map<String, String>
 */
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

    //to get output (will also make it POST (do NOT use in GET method))
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

/**
 * Gets the JSON output from Ørsted for the given user.
 *
 * @param externalID a users ID on Ørsted servers (used in the HTTP request
 * @param token a token to see if the login is still valid. This token expires after some time
 * @param interval form the enum class. This could fx be: TimeInterval.Daily, TimeInterval.Yearly etc.
 * @return the JSON as a String
 */
fun getConsumptions(externalID: String, token: String, interval: TimeInterval): String {
    //url get consumptions for a specific interval. E.g. daily, weekly, monthly.
    val mURL = URL("https://prod.copi.obviux.dk/consumptionPage/$externalID/$interval")

    //printing URL
    println("URL: $mURL")

    //make a connection
    val conn = mURL.openConnection() as HttpURLConnection

    conn.requestMethod = "GET"

    //setting headers
    conn.setRequestProperty("authority", "prod.copi.obviux.dk")
    conn.setRequestProperty("Accept", "application/json, text/plain, */*")
    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
    conn.setRequestProperty("authorization", token)

    //read the data
    val response = StringBuilder()

    val br = BufferedReader(InputStreamReader(conn.inputStream, "utf-8"))
    var responseLine: String?
    while (br.readLine().also { responseLine = it } != null) {
        response.append(responseLine!!.trim())
    }

    //returning the output as a String
    return response.toString()
}

/**
 * To get a list of ConsumptionData
 *
 * @param jsonData the json for a specific interval as a String. Get with "getConsumptions()"
 * @return a list of ConsumptionData
 */
fun getConsumptionDataList(jsonData: String): List<ConsumptionData> {
    //parsing our String to json
    val json = JsonParser().parse(jsonData)
    //finding the data array in JSON
    val dataArray = json.asJsonObject["data"].asJsonArray
    //finding the consumptions array at index 0 in dataArray
    val consumptions = dataArray[0].asJsonObject["consumptions"].asJsonArray

    //list of the data classes "ConsumptionData"
    val consumptionList = ArrayList<ConsumptionData>()
    //looping through all consumptions element in the json file
    for (i in 0 until consumptions.size()) {
        val start = consumptions[i].asJsonObject["start"].asString
        val end = consumptions[i].asJsonObject["end"].asString
        val kWh = consumptions[i].asJsonObject["kWh"].asDouble

        //COPI is a map in the json file
        val copiMap = HashMap<String, String>()
        val copi = consumptions[i].asJsonObject["COPI"].asJsonObject

        copiMap["xAxisLabel"] = copi["xAxisLabel"].asString
        copiMap["currentConsumptionPrefix"] = copi["currentConsumptionPrefix"].asString
        copiMap["unit"] = copi["unit"].asString

        if(kWh != 0.0) {
            consumptionList.add(ConsumptionData(start, end, kWh, copiMap))
        }
    }
    return consumptionList
}
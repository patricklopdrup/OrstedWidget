# Ørsted Widget

This is an <code>Android</code> app written in <code>Kotlin</code>. The purpose is to have an app where you log in with your <code>Ørsted</code> account once and the app provides a <code>widget</code> where you can see your electricity consumption.

<h2>Widget in action</h2>
<img src="img/widget.gif" width="180">
Widget running on an OnePlus 7T Pro.

<h2>First Mock-up</h2>
<img align="left" src="https://imgur.com/HhJpP31.png" width="320">
<img src="https://imgur.com/3hil0Aq.png" width="180">
Mock-up made in Figma. Showed on an OnePlus 7T Pro.

<h2>JSON</h2>

```yaml 
   "consumptions": [
                {
                    "start": "2019-11-23T23:00:00.000Z",
                    "end": "2019-11-24T23:00:00.000Z",
                    "kWh": 0.63,
                    "COPI": {
                        "xAxisLabel": "Søn",
                        "currentConsumptionPrefix": "Søndag den 24. november 2019 brugte du",
                        "unit": "kWh",
                        "currentConsumption": 0.63
                    }
                },
                {
                    "start": "2019-11-24T23:00:00.000Z",
                    "end": "2019-11-25T23:00:00.000Z",
                    "kWh": 2.26,
                    "COPI": {
                        "xAxisLabel": "Man",
                        "currentConsumptionPrefix": "Mandag den 25. november 2019 brugte du",
                        "unit": "kWh",
                        "currentConsumption": 2.26
                    }
                },
```
Here is how some of the <code>consumptions</code> objects looks in the <code>data</code> array in the <code>JSON</code>.</br></br>
This is by using the <code>HTTP</code> request:
```kotlin
   https://prod.copi.obviux.dk/consumptionPage/${my_external_id}/daily
```

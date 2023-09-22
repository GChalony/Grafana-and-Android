package com.test.demo_grafana

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var db: Database

    private lateinit var sensorManager: SensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        db = Room.databaseBuilder(
            context = this,
            Database::class.java, "sensors-db"
        ).build()
        val dao = db.sensorDao()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        println("Sensors: $deviceSensors")

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        super.onCreate(savedInstanceState)
        setContent {

                var acceleration by remember {
                    mutableStateOf("")
                }

                LaunchedEffect(key1 = true) {
                    sensorManager.registerListener(object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent?) {
                            acceleration = "${event?.values?.get(0)} - ${event?.values?.get(1)} - ${event?.values?.get(2)}"
                            if (event == null) return
                            println("Sensor $event")
                            runBlocking(Dispatchers.IO) {
                                println("Inserting data $event")
                                dao.insert(
                                    SensorData(
                                        0,
                                        System.currentTimeMillis(),
                                        event.values[0].toDouble(),
                                        event.values[1].toDouble(),
                                        event.values[2].toDouble()
                                    )
                                )
                            }
                        }
                        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                    }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        Text(text = "Accelerometer", style = MaterialTheme.typography.h3)
                        Text(text = acceleration)
                    }
                }
            }
        }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


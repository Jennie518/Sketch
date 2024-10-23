package com.example.lab2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

class ShakeDetector(private val onShake: () -> Unit) {


    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var shakeThreshold = 1.5f
    private var lastShakeTimestamp: Long = 0
    private var shakeTimeThreshold = 500L // 500 milliseconds between shakes

    fun start(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            Log.d("ShakeDetector", "Accelerometer sensor available")
            sensorManager?.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.e("ShakeDetector", "Accelerometer sensor NOT available")
        }
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
//                Log.d("ShakeDetector", "Sensor event: X=${it.values[0]}, Y=${it.values[1]}, Z=${it.values[2]}")

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val gX = x / SensorManager.GRAVITY_EARTH
                val gY = y / SensorManager.GRAVITY_EARTH
                val gZ = z / SensorManager.GRAVITY_EARTH

                val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

                if (gForce > shakeThreshold) {
                    Log.d("ShakeDetector", "Getting into if")
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastShakeTimestamp > shakeTimeThreshold) {
                        lastShakeTimestamp = currentTime
                        Log.d("ShakeDetector", "Shake detected, increasing brush size.")
                        onShake()
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Not needed for shake detection
        }
    }


    fun stop() {
        Log.d("ShakeDetector", "ShakeDetector stopped.")
        sensorManager?.unregisterListener(sensorEventListener)
    }
}


package com.example.weatherapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.util.concurrent.Executors

const val key:String = "7f46ec81682646709f8194238232401"
const val url:String = "https://api.weatherapi.com/v1/current.json?key=7f46ec81682646709f8194238232401&q=Astana&aqi=no"

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide();
        val queue = Volley.newRequestQueue(this)
        val stringRequest= StringRequest(Request.Method.GET, url,
            {
                response ->
                    val obj = JSONObject(response)
                    val locationInfo = obj.getJSONObject("location")
                    val forecastInfo = obj.getJSONObject("current")
                    binding.address.text = locationInfo.getString("name")
                    binding.updatedAt.text = "Updated at: ${forecastInfo.getString("last_updated")}"
                    binding.temp.text = "${forecastInfo.getString("temp_c")}°C"
                    binding.feelslike.text = "Feels like ${forecastInfo.getString("feelslike_c")}°C"
                    binding.wind.text = "Wind ${forecastInfo.getString("wind_kph")} kph"
                    val executor = Executors.newSingleThreadExecutor()

                    val handler = Handler(Looper.getMainLooper())

                    var image: Bitmap? = null
                    executor.execute {
                        val imageURL = forecastInfo.getJSONObject("condition").getString("icon")

                        try {
                            val `in` = java.net.URL(imageURL).openStream()
                            image = BitmapFactory.decodeStream(`in`)

                            handler.post {
                                binding.img1.setImageBitmap(image)
                            }
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
            {Log.e("My Log", "Error: $it")})
        queue.add(stringRequest)
    }
}
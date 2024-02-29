package com.atilsamancioglu.weatherapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.atilsamancioglu.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//api key 97fca536bc2ca0fa3efa4ba4aa17e873

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchingaview
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true

            }

        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(APiInterface::class.java)

        val response =
            retrofit.getwetaherDetails(cityName, "97fca536bc2ca0fa3efa4ba4aa17e873", "metric")
        response.enqueue(object : Callback<weatherapp> {
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val temperature = responseBody.main?.temp.toString()
                        val humidity = responseBody.main?.humidity
                        val windspeed = responseBody.wind.speed
                        val sunRise = responseBody.sys.sunrise.toLong()
                        val Sunset = responseBody.sys.sunset.toLong()
                        val mintemp = responseBody.main?.temp_min
                        val seaLevel = responseBody.main?.pressure
                        var condition = responseBody.weather.firstOrNull()?.main ?: "Unknown"
                        val maxtemp = responseBody.main?.temp_max



                        binding.temp.text = "$temperature°C"
                        binding.humidity.text = "$humidity%"
                        binding.wind.text = "$windspeed m/s"
                        binding.sunsets.text = "${time(Sunset)}"
                        binding.sunrise.text = "${time(sunRise)}"
                        binding.textView4.text = "$condition"
                        binding.conditions.text = "$condition"
                        binding.sea.text = "$seaLevel hpa"
                        binding.textView5.text = "Max Temp:$maxtemp°C"
                        binding.textView6.text = "Min Temp $mintemp°C"
                        binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.cityName.text = "$cityName"

                        changecloud(condition)


                    } else {
                        Log.d("Response Error", "Response body is null")
                    }
                } else {
                    Log.d("Response Error", "Response is not successful")
                }
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                Log.e("API Call Failure", "Failed to fetch weather data", t)
            }
        })
    }

    private fun changecloud(condition: String) {
        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Party Clouds", "Clouds", "Overcast", "Mist", "Foggy","Haze" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }


        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))

    }
}





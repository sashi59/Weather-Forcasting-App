package com.example.weatherforcastingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherforcastingapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

//8f7d7cffbab296578a7f27aa77b1c4d0

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Surat")

        searchCity()
    }

    private fun searchCity() {
        val searchCity = binding.search
        searchCity.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return  true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(city : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(city, "8f7d7cffbab296578a7f27aa77b1c4d0", "metric")
        response.enqueue(object : Callback<WeatherForcastingApp>{
            override fun onResponse(
                call: Call<WeatherForcastingApp>,
                response: Response<WeatherForcastingApp>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max.toString()
                    val minTemp = responseBody.main.temp_min.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val windspeed = responseBody.wind.speed
                    val sealevel = responseBody.main.pressure
//                    Log.d("TAG", "onResponse: $condition")



                    binding.temp.text = "$temperature"
                    binding.sunset.text = "${timeFormat(sunset)}"
                    binding.sunrise.text = "${timeFormat(sunrise)}"
                    binding.humidity.text = "$humidity %"
                    binding.maxTemp.text = "Max: $maxTemp °C"
                    binding.minTemp.text = "Min: $minTemp °C"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sealevel.text = "$sealevel hPa"
                    binding.condition.text = "$condition"
                    binding.conditions.text = "$condition"
                    binding.day.text = dayFormat(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.city.text = "$city"

                    onBackgroundChange(condition)




                }
            }




            override fun onFailure(call: Call<WeatherForcastingApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun onBackgroundChange(condition: String) {
        when(condition){
            "Haze", "Mist", "Clouds", "Partly Clouds", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Sunny", "Clear", "Clear Sky"-> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain", "Heavy Rain", "Moderate Rain", "Drizzle", "Showers" -> {
            binding.root.setBackgroundResource(R.drawable.rain_background)
            binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Heavy Snow", "Moderate Snow", "Blizzards" -> {
            binding.root.setBackgroundResource(R.drawable.snow_background)
            binding.lottieAnimationView.setAnimation(R.raw.snow)
        }
            else ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun dayFormat(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun date(): String {
        val sdf =  SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun timeFormat(timestamp: Long): String {
        val sdf =  SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

}
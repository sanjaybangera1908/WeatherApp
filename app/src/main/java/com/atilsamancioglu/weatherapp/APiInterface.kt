package com.atilsamancioglu.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APiInterface {
    @GET("weather")
    fun getwetaherDetails(
        @Query("q") city:String,
        @Query("appid") appid:String,
        @Query("units") units:String
        ): Call<weatherapp>

}
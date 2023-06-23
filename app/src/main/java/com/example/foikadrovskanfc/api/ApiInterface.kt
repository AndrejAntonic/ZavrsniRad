package com.example.foikadrovskanfc.api

import com.example.foikadrovskanfc.entities.UserResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("users")
    fun getAllPersonnelData(): Call<UserResponse>
}
package com.example.foikadrovskanfc.api

import android.util.Log
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.entities.UserResponse
import com.example.foikadrovskanfc.helpers.ResponseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    val BASE_URL = "https://kadrovska.foi.hr/foi-api/public/"
    val responseHandler = ResponseHandler()

    fun getPersonnelData(callback: (MutableList<Personnel>) -> Unit) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getAllPersonnelData()

        retrofitData.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val personnelList = responseHandler.convertResponseToPersonnelList(responseBody)
                    callback(personnelList)
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Log.d("Proba2", "onFailure" + t.message)
            }
        })
    }
}
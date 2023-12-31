package com.example.foikadrovskanfc.api

import android.util.Log
import com.example.foikadrovskanfc.database.PersonnelDatabase
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.entities.UserResponse
import com.example.foikadrovskanfc.helpers.ResponseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ApiService {
    private const val BASE_URL = "https://kadrovska.foi.hr/foi-api/public/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)
    private val responseHandler = ResponseHandler()

    fun getPersonnelDataReturnList(callback: (MutableList<Personnel>) -> Unit) {
        val retrofitData = apiInterface.getAllPersonnelData()

        retrofitData.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val personnelList = responseHandler.convertResponseToPersonnelList(responseBody)
                    PersonnelDatabase.getInstance().getPersonnelDAO().insertPersonnel(personnelList)
                    callback(personnelList)
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Log.d("Data fetching failure", "Failure while fetching API data: " + t.message)
            }
        })
    }

    suspend fun getPersonnelData(): UserResponse? {
        return suspendCoroutine { continuation ->
            val retrofitData = apiInterface.getAllPersonnelData()

            retrofitData.enqueue(object : Callback<UserResponse?> {
                override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val personnelList = responseHandler.convertResponseToPersonnelList(responseBody)
                        PersonnelDatabase.getInstance().getPersonnelDAO().insertPersonnel(personnelList)
                        continuation.resume(responseBody)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                    Log.d("Data fetching failure", "Failure while fetching API data: ${t.message}")
                    continuation.resume(null)
                }
            })
        }
    }



    private fun insertPersonnelIntoDatabase(personnelList: MutableList<Personnel>) {
        PersonnelDatabase.getInstance().getPersonnelDAO().insertPersonnel(personnelList)
    }
}

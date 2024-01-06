package com.example.final_project_android

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class PlacesApiClient {
    private val apiKey = "AIzaSyDqOB2ikBicjeHk9ezHPjQ8IqaBhIL42Tg"

    fun getRestaurants(category: String, callback: PlacesCallback) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PlacesApiService::class.java)

        val call = service.getNearbyPlacesByCategory(
            "43.2567,76.9286",
            5500,
            "restaurant",
            category,
            apiKey,
            "name,business_status,opening_hours,price_level,rating,user_ratings_total,vicinity"
        )

        call.enqueue(object : Callback<PlacesApiService.PlacesResponse> {
            override fun onResponse(
                call: Call<PlacesApiService.PlacesResponse>,
                response: Response<PlacesApiService.PlacesResponse>
            ) {
                if (response.isSuccessful) {
                    val placesResponse = response.body()
                    placesResponse?.let {
                        val places = it.places
                        Log.e("Success", places.toString())
                        callback.onPlacesFetched(places)
                    }
                } else {
                    val errorMessage = "Error: ${response.message()}"
                    callback.onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<PlacesApiService.PlacesResponse>, t: Throwable) {
                val errorMessage = "Call failed: ${t.message}"
                callback.onError(errorMessage)
            }
        })
    }

    interface PlacesCallback {
        fun onPlacesFetched(restaurants: List<PlacesApiService.Place>)
        fun onError(errorMessage: String)
    }
}




interface PlacesApiService {
    @GET("nearbysearch/json")
    fun getNearbyPlacesByCategory(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("keyword") keyword: String,
        @Query("key") apiKey: String,
        @Query("fields") fields: String
    ): Call<PlacesResponse>

    data class PlacesResponse(
        @SerializedName("results")
        val places: List<Place>
    )

    data class Place(
        @SerializedName("name")
        val name: String,

        @SerializedName("business_status")
        val businessStatus: String?,

        @SerializedName("opening_hours")
        val openingHours: OpeningHours?,

        @SerializedName("price_level")
        val priceLevel: Int,

        @SerializedName("rating")
        val rating: Double?,

        @SerializedName("user_ratings_total")
        val userRatingsTotal: Int?,

        @SerializedName("vicinity")
        val address: String,

        val isFavourite: Boolean = false
    )

    data class OpeningHours(
        @SerializedName("open_now")
        val openNow: Boolean?
    )
}

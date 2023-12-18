package com.example.final_project_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class RestaurantViewModel(val twoGisApiClient: PlacesApiClient = PlacesApiClient()) : ViewModel() {
    private val _restaurants = MutableLiveData<List<PlacesApiService.Place>>()
    val restaurants: LiveData<List<PlacesApiService.Place>> get() = _restaurants

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadRestaurants(category: String) {
        twoGisApiClient.getRestaurants(category, object: PlacesApiClient.PlacesCallback {
            override fun onPlacesFetched(places: List<PlacesApiService.Place>) {
                _restaurants.value = places
            }

            override fun onError(errorMessage: String) {
                _error.value = errorMessage
            }
        })
    }
}

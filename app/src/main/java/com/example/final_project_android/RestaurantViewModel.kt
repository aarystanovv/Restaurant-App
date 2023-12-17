package com.example.final_project_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_project_android.PlacesApiService.Place
import kotlinx.coroutines.launch

class RestaurantViewModel(private val placesApiClient: PlacesApiClient) : ViewModel() {

    private val _restaurants = MutableLiveData<List<Place>>()
    val restaurants: LiveData<List<Place>> = _restaurants

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadRestaurants(category: String) {
        viewModelScope.launch {
            placesApiClient.getRestaurants(category, object : PlacesApiClient.PlacesCallback {
                override fun onPlacesFetched(places: List<Place>) {
                    _restaurants.value = places
                }

                override fun onError(errorMessage: String) {
                    _error.value = errorMessage
                }
            })
        }
    }
}
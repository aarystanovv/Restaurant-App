package com.example.final_project_android

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RestaurantViewModel(private val twoGisApiClient: PlacesApiClient = PlacesApiClient()) : ViewModel() {
    private val _restaurants = MutableLiveData<List<PlacesApiService.Place>>(emptyList())
    val restaurants: LiveData<List<PlacesApiService.Place>> get() = _restaurants

    private val _favouriteRestaurants = mutableStateOf<List<PlacesApiService.Place>>(emptyList())
    fun getFavouriteRestaurants(): List<PlacesApiService.Place> = _favouriteRestaurants.value ?: emptyList()

    fun addToFavorites(restaurant: PlacesApiService.Place?) {
        if (restaurant != null) {
            val currentFavorites = _favouriteRestaurants.value ?: emptyList()
            val updatedFavorites = if (currentFavorites.any { it.name == restaurant.name }) {
                currentFavorites.filter { it.name != restaurant.name }
            } else {
                currentFavorites + restaurant
            }
            _favouriteRestaurants.value = updatedFavorites

            _restaurants.value = _restaurants.value?.map { it.copy(isFavourite = updatedFavorites.contains(it)) }
            println(_favouriteRestaurants.value)
            println(_restaurants.value)
        }
    }


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadRestaurants(category: String) {
        twoGisApiClient.getRestaurants(category, object: PlacesApiClient.PlacesCallback {
            override fun onPlacesFetched(restaurants: List<PlacesApiService.Place>) {
                val currentFavorites = _favouriteRestaurants.value ?: emptyList()
                val updatedRestaurants = restaurants.map { restaurant ->
                    restaurant.copy(isFavourite = currentFavorites.any { fav -> fav.name == restaurant.name })
                }
                _restaurants.value = updatedRestaurants
            }

            override fun onError(errorMessage: String) {
                _error.value = errorMessage
            }
        })
    }



}

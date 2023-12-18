package com.example.final_project_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.final_project_android.model.Category

class HomeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                MyApp()
            }
        }

        return view
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    val categoryViewModel: CategoryViewModel = viewModel()
    val categories by categoryViewModel.categories.observeAsState(emptyList())

    val restaurantViewModel: RestaurantViewModel = viewModel()

    NavHost(navController, startDestination = "categories") {
        composable("categories") {
            CategoriesScreen(
                categories = categories,
                navigateToRestaurants = { selectedCategory ->
                    navController.navigate("restaurants/$selectedCategory")
                }
            )
        }
        composable(
            "restaurants/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            category?.let { selectedCategory ->
                restaurantViewModel.loadRestaurants(selectedCategory)
                RestaurantsScreen(
                    category = selectedCategory,
                    viewModel = restaurantViewModel
                ) { selectedRestaurant ->
                    navController.navigate("restaurantDetail/${selectedRestaurant.name}")
                }
            }
        }
        composable(
            "restaurantDetail/{restaurantName}",
            arguments = listOf(navArgument("restaurantName") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantName = backStackEntry.arguments?.getString("restaurantName")
            restaurantName?.let {
                RestaurantDetailScreen(
                    restaurantName = it,
                    viewModel = restaurantViewModel,
                    onBackPress = { navController.popBackStack() }
                )
            }
        }
    }
}
@Composable
fun CategoriesScreen(
    categories: List<Category>,
    navigateToRestaurants: (String) -> Unit
) {
    LazyColumn {
        items(categories) { category ->
            Button(
                onClick = { navigateToRestaurants(category.title) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 16.dp
                )
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

@Composable
fun RestaurantsScreen(
    category: String,
    viewModel: RestaurantViewModel,
    onSelectRestaurant: (PlacesApiService.Place) -> Unit
) {
    val restaurants by viewModel.restaurants.observeAsState(emptyList())
    val error by viewModel.error.observeAsState("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Restaurants in $category",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(restaurants) { restaurant ->
                Card(
                    modifier = Modifier
                        .clickable { onSelectRestaurant(restaurant) }
                        .fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = restaurant.address,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                style = MaterialTheme.typography.body2,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun RestaurantDetailScreen(
    restaurantName: String,
    viewModel: RestaurantViewModel,
    onBackPress: () -> Unit
) {
    val restaurants by viewModel.restaurants.observeAsState(emptyList())
    val restaurant = restaurants.find { it.name == restaurantName }

    if (restaurant != null) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = restaurant.address,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Rating: ${restaurant.rating}",
                style = MaterialTheme.typography.body1
            )

            Text(
                text = "Open Now: ${if (restaurant.openingHours?.openNow == true) "Yes" else "No"}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Price Level: ${restaurant.priceLevel ?: "N/A"}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Total User Ratings: ${restaurant.userRatingsTotal}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Back",
                style = MaterialTheme.typography.body2,
                color = Color.Blue,
                modifier = Modifier
                    .clickable(onClick = onBackPress)
                    .padding(top = 16.dp)
            )
        }
    } else {
        Text(
            text = "Restaurant not found",
            style = MaterialTheme.typography.body1,
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    }
}
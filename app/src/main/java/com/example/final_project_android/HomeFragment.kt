package com.example.final_project_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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
            CategoriesScreen(categories) { selectedCategory ->
                navController.navigate("restaurants/$selectedCategory")
            }
        }
        composable(
            "restaurants/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            category?.let { selectedCategory ->
                restaurantViewModel.loadRestaurants(selectedCategory)
                RestaurantsScreen(category = selectedCategory, restaurantViewModel)
            }
        }
    }
}

@Composable
fun CategoriesScreen(categories: List<Category>, navigateToRestaurants: (String) -> Unit) {
    LazyColumn {
        items(categories) { category ->
            Button(
                onClick = { navigateToRestaurants(category.title) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = category.title)
            }
        }
    }
}

@Composable
fun RestaurantsScreen(category: String, viewModel: RestaurantViewModel) {
    val restaurants by viewModel.restaurants.observeAsState(emptyList())
    val error by viewModel.error.observeAsState("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Restaurants in $category",
            style = MaterialTheme.typography.h5
        )
        LazyColumn {
            items(restaurants) { restaurant ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = restaurant.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        if (error.isNotEmpty()) {
            Text(text = error)
        }
    }
}



@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.final_project_android

import androidx.compose.runtime.remember
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.final_project_android.model.Category

class HomeFragment: Fragment() {
    private val restaurantViewModel: RestaurantViewModel by activityViewModels()
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

class Favourite : Fragment() {
    private val restaurantViewModel: RestaurantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                FavouritesScreen(
                    viewModel = restaurantViewModel,
                    onBackPress = { requireActivity().onBackPressed()  }
                )
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

        composable("favourite") {
            FavouritesScreen(
                viewModel = restaurantViewModel,
                onBackPress = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun FavouritesScreen(
    viewModel: RestaurantViewModel,
    onBackPress: () -> Unit
) {

    val newData = listOf(
        PlacesApiService.Place(
            name = "Trattoria",
            businessStatus = "OPERATIONAL",
            openingHours = PlacesApiService.OpeningHours(openNow = true),
            priceLevel = 3,
            rating = 4.6,
            userRatingsTotal = 385,
            address = "Furmanov Ave 220, Almaty",
            isFavourite = false
        ),
        PlacesApiService.Place(
            name = "Villa Dei Fiori",
            businessStatus = "OPERATIONAL",
            openingHours = PlacesApiService.OpeningHours(openNow = true),
            priceLevel = 4,
            rating = 4.5,
            userRatingsTotal = 993,
            address = "пр. Аль-Фараби 140А, Al-Farabi Avenue 140a",
            isFavourite = false
        )
    )

    newData.forEach { viewModel.addToFavorites(it) }

    val favouriteRestaurants = viewModel.getFavouriteRestaurants()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favorite Restaurants",
                        color = Color.hsl(0.61F, 0.51F, 0.16F)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        if (favouriteRestaurants.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            ) {
                items(favouriteRestaurants) { restaurant ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable {
                            }
                    ) {
                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.h6,
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    FavoriteRestaurantsItem(restaurant = restaurant)
                    Divider()
                }
            }
        }
    }
}

@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun FavoriteRestaurantsItem(restaurant: PlacesApiService.Place) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = restaurant.name, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium, color = Color.hsl(0.61F, 0.51F, 0.16F))
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun CategoriesScreen(
    categories: List<Category>,
    navigateToRestaurants: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // Set your background color
    ) {
        Image(
            painter = painterResource(id = R.drawable.category_back_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Foreground Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)) // Set your foreground color and transparency
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Categories :",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.White
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(categories) { category ->
                        Button(
                            onClick = { navigateToRestaurants(category.title) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(/*horizontal = 16.dp, */vertical = 8.dp),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 16.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(25.dp),
                        ) {
                            Text(
                                text = category.title,
                                //style = MaterialTheme.typography.button
                                style = TextStyle(
                                    //fontFamily = FontFamily(Font(R.font.lobster)),
                                    color = Color.Black,
                                    fontSize = 25.sp
                                )
                            )
                        }
                    }
                }
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
    LazyColumn {
        items(restaurants) { restaurant ->
            Card(modifier = Modifier.clickable { onSelectRestaurant(restaurant) }) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = restaurant.name)
                        Text(text = restaurant.address)
                    }
                    IconButton(onClick = { viewModel.addToFavorites(restaurant) }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Add to Favorites"
                        )
                    }
                }
            }
        }
    }


        Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // Set your background color
    ) {
        Image(
            painter = painterResource(id = R.drawable.restaurants_back_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Foreground Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)) // Set your foreground color and transparency
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Restaurants in $category :",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.White
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
                            elevation = 4.dp,
                            shape = RoundedCornerShape(25.dp)
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
    }
}



@OptIn(ExperimentalGraphicsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RestaurantDetailScreen(
    restaurantName: String,
    viewModel: RestaurantViewModel,
    onBackPress: () -> Unit
) {
    val restaurants by viewModel.restaurants.observeAsState(emptyList())
    val restaurant = restaurants.find { it.name == restaurantName }
    var isFavourite by remember { mutableStateOf(restaurant?.isFavourite ?: false) }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = restaurant?.name ?: "Restaurant Detail", color = Color.hsl(0.61F, 0.51F, 0.16F)) },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isFavourite = !isFavourite
                        restaurant?.let {
                            viewModel.addToFavorites(restaurant)
                        }
                    }) {
                        Image(
                            painter = painterResource(
                                id = if (isFavourite) R.drawable.ic_like else R.drawable.ic_unlike
                            ),
                            contentDescription = "Favorite"
                        )
                    }
                }
            )
        }
    ) {
        if (restaurant != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.detail_back_image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = restaurant.address,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontSize = 17.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Rating: ${restaurant.rating}",
                        style = MaterialTheme.typography.body1,
                        fontSize = 17.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Open Now: ${if (restaurant.openingHours?.openNow == true) "Yes" else "No"}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontSize = 17.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Price Level: ${restaurant.priceLevel ?: "N/A"}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontSize = 17.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Total User Ratings: ${restaurant.userRatingsTotal}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontSize = 17.sp,
                        color = Color.DarkGray
                    )
                }
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
}


package com.example.pokemon.ui.screens.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokemon.ui.navigation.Routes
import com.example.pokemon.ui.screens.list.PokemonListScreen
import com.example.pokemon.ui.screens.moves.MovesScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Pokedex : BottomNavItem("pokedex", Icons.Default.CatchingPokemon, "Pokédex")
    object Moves : BottomNavItem("moves", Icons.Default.Gamepad, "Moves")
}

@Composable
fun MainScreen(
    mainNavController: NavHostController
) {
    val bottomBarNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = bottomBarNavController)
        }
    ) { innerPadding ->
        BottomNavGraph(
            mainNavController = mainNavController,
            bottomBarNavController = bottomBarNavController,
            paddingValues = innerPadding
        )
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Pokedex,
        BottomNavItem.Moves
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavGraph(
    mainNavController: NavHostController,
    bottomBarNavController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = bottomBarNavController,
        startDestination = BottomNavItem.Pokedex.route
    ) {
        composable(BottomNavItem.Pokedex.route) {
            PokemonListScreen(
                paddingValues = paddingValues,
                onCharacterClick = { pokemonId ->
                    mainNavController.navigate("${Routes.DETAIL}/$pokemonId")
                }
            )
        }
        composable(BottomNavItem.Moves.route) {
            MovesScreen(paddingValues = paddingValues)
        }
    }
}
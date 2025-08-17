package com.example.pokemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokemon.ui.screens.detail.PokemonDetailScreen
import com.example.pokemon.ui.screens.main.MainScreen

object Routes {
    const val MAIN = "main"
    const val DETAIL = "detail"
}

object Arguments {
    const val POKEMON_ID = "pokemonId"
}

@Composable
fun AppNavHost(navController: NavHostController) {

    val detailScreenRoute = "${Routes.DETAIL}/{${Arguments.POKEMON_ID}}"

    NavHost(navController = navController, startDestination = Routes.MAIN) {

        composable(route = Routes.MAIN) {
            MainScreen(mainNavController = navController)
        }

        composable(
            route = detailScreenRoute,
            arguments = listOf(
                navArgument(Arguments.POKEMON_ID) { type = NavType.IntType }
            )
        ) {
            PokemonDetailScreen(
                onUpClick = { navController.popBackStack() }
            )
        }
    }
}
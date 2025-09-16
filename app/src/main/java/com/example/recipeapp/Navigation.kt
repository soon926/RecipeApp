package com.example.recipeapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.screen.AddEditRecipe
import com.example.recipeapp.screen.RecipeDetail
import com.example.recipeapp.screen.RecipeList

enum class Navigation {
    RecipeList,
    Detail,
    AddEditRecipe
}

@Composable
fun Navigate(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.RecipeList.name,
        modifier = Modifier
    ) {
        composable(route = Navigation.RecipeList.name) {
            RecipeList(navController = navController)
        }
        composable(
            route = "${Navigation.AddEditRecipe.name}/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) {
            AddEditRecipe(navController = navController)
        }
        composable(
            route = "${Navigation.Detail.name}/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) {
            RecipeDetail(navController = navController)
        }
    }

}
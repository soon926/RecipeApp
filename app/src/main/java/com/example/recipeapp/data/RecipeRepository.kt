package com.example.recipeapp.data

import android.content.Context
import android.util.JsonReader
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray

class RecipeRepository(private val recipeDao: RecipeDao, private val context: Context) {


    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    fun getRecipeByType(type: String): Flow<List<Recipe>> {
        return recipeDao.getRecipesByType(type)
    }

    fun getRecipeById(id: Int): Flow<Recipe> {
        return recipeDao.getRecipe(id)
    }

    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }

    suspend fun update(recipe: Recipe) {
        recipeDao.update(recipe)
    }

    suspend fun delete(recipe: Recipe) {
        recipeDao.delete(recipe)
    }

    fun getRecipeTypes(): List<String> {
        val json = context.assets.open("recipetypes.json").bufferedReader()
            .use { it.readText() }
        val jsonArray = JSONArray(json)
        val types = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val type = jsonObject.getString("name")
            types.add(type)
        }
        return types
    }
}
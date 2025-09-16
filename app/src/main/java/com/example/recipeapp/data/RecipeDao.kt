package com.example.recipeapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipe(id: Int): Flow<Recipe>

    @Query("SELECT * FROM recipes WHERE recipeType = :type")
    fun getRecipesByType(type: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<Recipe>>
}

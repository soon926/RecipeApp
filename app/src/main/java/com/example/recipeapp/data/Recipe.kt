package com.example.recipeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val recipeType: String,
    val ingredients: String,
    val steps: String,
    val imageUri: String?

)

data class RecipeType(
    val name: String
)
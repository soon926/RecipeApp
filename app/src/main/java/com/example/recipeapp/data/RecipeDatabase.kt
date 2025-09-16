package com.example.recipeapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun  recipeDao(): RecipeDao
    companion object {
        @Volatile
        private var Instance: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, RecipeDatabase::class.java, "recipe_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

package com.example.recipeapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeDatabase
import com.example.recipeapp.data.RecipeRepository
import com.example.recipeapp.data.RecipeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: RecipeRepository
    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()


    init {
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repo = RecipeRepository(recipeDao = recipeDao, context = application)
        loadInit()
    }

    fun loadInit() {

        viewModelScope.launch {
            val types = repo.getRecipeTypes()
            _uiState.update { currentState ->
                currentState.copy(recipeTypes = types)
            }

            repo.getAllRecipes()
                .collect { recipeList ->

                    _uiState.update { currentState ->
                        currentState.copy(recipes = recipeList, isLoading = false)
                    }
                }
        }
    }

    fun onFilterChanged(recipeType: String?) {
        _uiState.update { currentState ->
            currentState.copy(selectedType = recipeType)
        }
    }

}

data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val recipeTypes: List<String> = emptyList(),
    val selectedType: String? = "",
    val isLoading: Boolean = true
)
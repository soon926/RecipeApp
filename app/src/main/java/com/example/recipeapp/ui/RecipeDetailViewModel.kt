package com.example.recipeapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeDatabase
import com.example.recipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class RecipeDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repo: RecipeRepository
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private val recipeId: Int = checkNotNull(savedStateHandle["recipeId"])

    init {
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repo = RecipeRepository(recipeDao = recipeDao, context = application)

        viewModelScope.launch {
            val recipe = repo.getRecipeById(recipeId)?.firstOrNull()

            if (recipe != null) {
                _uiState.update {
                    it.copy(
                        recipe = recipe,
                        isLoading = false
                    )
                }
            } else {

            }
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            _uiState.value.recipe?.let { recipeToDelete ->
                deleteImage(recipeToDelete.imageUri)
                repo.delete(recipeToDelete)
            }
        }
    }

    fun deleteImage(path: String?) {
        if (path == null) return
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = true
)
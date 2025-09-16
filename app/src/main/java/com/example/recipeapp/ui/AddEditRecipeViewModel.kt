package com.example.recipeapp.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeDatabase
import com.example.recipeapp.data.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID


class AddEditRecipeViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repo: RecipeRepository
    private val _uiState = MutableStateFlow(AddEditRecipeUiState())
    val uiState: StateFlow<AddEditRecipeUiState> = _uiState.asStateFlow()
    private val recipeId: Int? = savedStateHandle["recipeId"]

    init {
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repo = RecipeRepository(recipeDao = recipeDao, context = application)

        viewModelScope.launch {
            val types = repo.getRecipeTypes()
            _uiState.update { currentState ->
                currentState.copy(recipeTypes = types)
            }

            if (recipeId != null && recipeId != -1) {
                repo.getRecipeById(recipeId).firstOrNull()?.let { recipe ->
                    _uiState.update {
                        it.copy(
                            name = recipe.name,
                            ingredients = recipe.ingredients,
                            steps = recipe.steps,
                            imageUri = recipe.imageUri,
                            selectedRecipeType = recipe.recipeType,
                            isEdit = true
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isEdit = false) }
            }
        }
    }

    fun onImageSelected(uri: String?) {
        if (uri == null || uri == "null") return

        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>().applicationContext
                    val tempUri = Uri.parse(uri)

                    val inputStream = context.contentResolver.openInputStream(tempUri)

                    val imageName = "${UUID.randomUUID()}.jpg"
                    val image = File(context.filesDir, imageName)

                    inputStream?.use { input ->
                        image.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    image.absolutePath
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            if (path != null) {
                deleteImage(_uiState.value.imageUri)
                _uiState.update { it.copy(imageUri = path) }
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


    fun saveRecipe() {
        Log.d("data", "$recipeId")
        if (isValid()) {
            viewModelScope.launch {
                val currentState = _uiState.value

                val id = if (recipeId == null || recipeId == -1) 0 else recipeId

                val recipeToSave = Recipe(

                    id = id,
                    name = currentState.name,
                    ingredients = currentState.ingredients,
                    steps = currentState.steps,
                    imageUri = currentState.imageUri,
                    recipeType = currentState.selectedRecipeType
                )

                if (recipeId != null && recipeId != -1) {
                    repo.update(recipeToSave)
                } else {
                    repo.insert(recipeToSave)
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onTypeChanged(type: String) {
        _uiState.update { it.copy(selectedRecipeType = type) }
    }

    fun onIngredientsChanged(ingredients: String) {
        _uiState.update { it.copy(ingredients = ingredients) }
    }

    fun onStepsChanged(steps: String) {
        _uiState.update { it.copy(steps = steps) }
    }

    fun isValid(): Boolean {
        return _uiState.value.name.isNotBlank() &&
                _uiState.value.ingredients.isNotBlank() &&
                _uiState.value.steps.isNotBlank() &&
                _uiState.value.selectedRecipeType.isNotBlank()
    }
}

data class AddEditRecipeUiState(
    val name: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val imageUri: String? = null,
    val recipeTypes: List<String> = emptyList(),
    val selectedRecipeType: String = "",
    val isSaving: Boolean = false,
    val isEdit: Boolean = false,
)
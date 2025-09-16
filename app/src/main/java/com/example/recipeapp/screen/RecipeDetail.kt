package com.example.recipeapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.ui.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetail(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val recipe: Recipe? = uiState.recipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }, actions = {
                    IconButton(onClick = {
                        uiState.recipe?.id?.let { recipeId ->
                            navController.navigate("addEditRecipe/$recipeId")
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Recipe")
                    }
                    IconButton(onClick = {
                        viewModel.onDelete()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Recipe")
                    }
                })
        }) { innerPadding ->
        if (recipe == null) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Recipe Not Found!")
            }
        } else {
            val recipe: Recipe = recipe!!
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (recipe.imageUri != "" && recipe.imageUri != null) {
                    AsyncImage(
                        model = recipe.imageUri,
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray)
                            .fillMaxWidth()
                            .height(250.dp),
                    ) {
                        Text(
                            text = "No Image",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = recipe.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    DetailCard(
                        "Recipe Type",
                        recipe.recipeType
                    )
                    Spacer(Modifier.height(16.dp))

                    DetailCard(
                        "Ingredients",
                        recipe.ingredients
                    )
                    Spacer(Modifier.height(16.dp))

                    DetailCard(
                        "Steps",
                        recipe.steps
                    )
                }
            }
        }
    }

}

@Composable
fun DetailCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = content)
        }
    }
}
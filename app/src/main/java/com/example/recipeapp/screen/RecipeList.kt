package com.example.recipeapp.screen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.recipeapp.Navigation
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.ui.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeList(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RecipeViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var selectedId by remember { mutableStateOf<Int?>(null) }

    Scaffold(modifier = modifier, topBar = {
        TopAppBar(title = { Text("Recipe List") })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { navController.navigate("${Navigation.AddEditRecipe.name}/-1") },
        ) {
            Icon(
                imageVector = Icons.Filled.Add, contentDescription = "Add"
            )
        }
    }

    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.recipes.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = ""
                )
                Text(
                    "No recipes found!",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            if (isLandscape) {
                Row(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.4f)
                            .padding(16.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded, onExpandedChange = {
                                expanded = !expanded

                            }) {
                            OutlinedTextField(
                                value = uiState.selectedType ?: "All Types",
                                onValueChange = { },
                                label = { Text("Filter by type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text(text = "All Types") }, onClick = {
                                    viewModel.onFilterChanged(null)
                                    expanded = false
                                }

                                )
                                uiState.recipeTypes.forEach { type ->
                                    DropdownMenuItem(text = { Text(text = type) }, onClick = {
                                        viewModel.onFilterChanged(type)
                                        expanded = false
                                    })
                                }
                            }
                        }
                        Log.d("data", "type landscape ${uiState.selectedType}")
                        val filteredRecipes = if (uiState.selectedType == null) {
                            uiState.recipes
                        } else {
                            uiState.recipes.filter { it.recipeType == uiState.selectedType }
                        }
                        LazyColumn(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            items(filteredRecipes) { recipe ->
                                RecipeItemCard(
                                    recipe = recipe, onClick = {
                                        selectedId = recipe.id
                                        Log.d("data", "id ${selectedId}")
                                        Log.d("data", "list ${uiState.recipes}")


                                    })
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(0.6f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color(red = 200, green = 200, blue = 200))
                        ) {
                            val recipe = uiState.recipes.find { it.id == selectedId }
                            if (recipe != null) {
                                RecipeDetailPane(
                                    recipe = recipe, navController = navController, onDelete = {
                                        viewModel.onDelete(recipe)
                                        selectedId =
                                            null
                                    })
                            } else {
                                Text("Select a recipe", Modifier.align(Alignment.Center))
                            }
                        }

                    }
                }
            } else {

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded, onExpandedChange = {
                            expanded = !expanded

                        }) {
                        OutlinedTextField(
                            value = uiState.selectedType ?: "All Types",
                            onValueChange = { },
                            label = { Text("Filter by type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text(text = "All Types") }, onClick = {
                                viewModel.onFilterChanged(null)
                                expanded = false
                            }

                            )
                            uiState.recipeTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(text = type) }, onClick = {
                                    viewModel.onFilterChanged(type)
                                    expanded = false
                                })
                            }
                        }
                    }
                    Log.d("data", "type${uiState.selectedType}")
                    val filteredRecipes = if (uiState.selectedType == null) {
                        uiState.recipes
                    } else {
                        uiState.recipes.filter { it.recipeType == uiState.selectedType }
                    }
                    LazyColumn(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(filteredRecipes) { recipe ->
                            RecipeItemCard(
                                recipe = recipe, onClick = {
                                    navController.navigate("${Navigation.Detail.name}/${recipe.id}")
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeItemCard(
    recipe: Recipe, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.wrapContentHeight()
        ) {
            if (recipe.imageUri != "" && recipe.imageUri != null) {
                AsyncImage(
                    model = recipe.imageUri,
                    contentScale = ContentScale.Crop,
                    contentDescription = "Recipe Image",
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(96.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .background(Color(red = 200, green = 200, blue = 200))
                        .fillMaxHeight()
                        .width(96.dp)
                ) {
                    Text(
                        text = "No Image", modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                Text(text = recipe.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    text = recipe.recipeType, color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RecipeDetailPane(
    recipe: Recipe, navController: NavController, onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                navController.navigate("${Navigation.AddEditRecipe.name}/${recipe.id}")
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Recipe")
            }
            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Recipe")
            }
        }

        if (!recipe.imageUri.isNullOrEmpty()) {
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
                    .height(250.dp)
            ) {
                Text(
                    text = "No Image", modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = recipe.name, fontWeight = FontWeight.Bold, fontSize = 24.sp
        )

        Spacer(Modifier.height(16.dp))
        DetailCard("Recipe Type", recipe.recipeType)
        Spacer(Modifier.height(16.dp))
        DetailCard("Ingredients", recipe.ingredients)
        Spacer(Modifier.height(16.dp))
        DetailCard("Steps", recipe.steps)
    }
}
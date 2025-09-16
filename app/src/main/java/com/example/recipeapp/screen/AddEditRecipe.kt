package com.example.recipeapp.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.recipeapp.ui.AddEditRecipeViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipe(
    modifier: Modifier = Modifier,
    viewModel: AddEditRecipeViewModel = viewModel(),
    navController: NavController
) {

    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    var photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(), onResult = { uri: Uri? ->
            viewModel.onImageSelected(uri.toString())
        })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEdit) "Edit Recipe" else "Add Recipe") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                })
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Button(
                        colors = ButtonColors(
                            containerColor = Color.Green,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.White
                        ),
                        enabled = !uiState.isSaving && viewModel.isValid(),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.saveRecipe()
                            }.invokeOnCompletion() {
                                navController.navigateUp()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp, 0.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator()
                        } else {
                            Text(
                                text = "Save", fontSize = 16.sp, textAlign = TextAlign.Center
                            )
                        }
                    }

                }
            )
        }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.imageUri != null) {
                AsyncImage(
                    model = uiState.imageUri,
                    contentDescription = " Recipe Image",
                    modifier = Modifier
                        .height(256.dp)
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(256.dp)
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                        .drawBehind {
                            drawRoundRect(
                                color = Color.Gray, style = Stroke(
                                    width = 3f, pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(12f, 12f), 0f
                                    )
                                )
                            )
                        }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(124.dp)
                            .width(96.dp)
                            .heightIn(max = 124.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto, contentDescription = ""
                        )
                        Text(text = "Add Image")
                    }
                }
            }

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChanged(it) },
                label = { Text("Recipe Name") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = uiState.selectedRecipeType ?: "",
                onValueChange = { },
                label = { Text("Recipe Type") },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBottomSheet = true },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = uiState.ingredients,
                onValueChange = { viewModel.onIngredientsChanged(it) },
                label = { Text("Ingredients") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = uiState.steps,
                onValueChange = { viewModel.onStepsChanged(it) },
                label = { Text("Steps") },
                modifier = Modifier.fillMaxWidth(),
            )

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LazyColumn {
                            items(uiState.recipeTypes) { type ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showBottomSheet = false
                                            viewModel.onTypeChanged(type)
                                        }) {
                                    Text(
                                        type,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
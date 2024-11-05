package com.example.lab2.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lab2.CustomViewModel
import java.io.File
@Composable
fun StartScreen(
    navController: NavController,
    viewModel: CustomViewModel = viewModel(),
    onImportImageClick: () -> Unit,
    userId: String
) {
    val drawings by viewModel.getAllDrawings().observeAsState(listOf())

    var showDialog by remember { mutableStateOf(false) }
    var drawingIdInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = {
            navController.navigate("canvas_screen")
        }) {
            Text(text = "Create new canvas")
        }
        Button(onClick = onImportImageClick) {
            Text("Import Image")
        }
        Button(onClick = { showDialog = true }) {
            Text("Get Drawing by ID")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Enter Drawing ID") },
                text = {
                    TextField(
                        value = drawingIdInput,
                        onValueChange = { drawingIdInput = it },
                        label = { Text("Drawing ID") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        val drawingId = drawingIdInput.toIntOrNull()
                        if (drawingId != null) {
                            // send the ID to the server
                            viewModel.fetchDrawingFileFromServer(drawingId, onSuccess = { bitmap ->
                                // Pass the bitmap to the canvas screen
                                viewModel.saveImportedBitmap(bitmap)
                                navController.navigate("canvas_screen")

                            }, onFailure = {
                                Toast.makeText(context, "Drawing not found", Toast.LENGTH_SHORT).show()
                            })

                        } else {
                            Log.e("StartScreen", "Invalid ID")
                        }
                    }) {
                        Text("Submit")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }


        LazyColumn {
            items(drawings) { drawing ->
                var isLoading by remember { mutableStateOf(true) }
                var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }

                LaunchedEffect(drawing.filePath) {
                    isLoading = true
                    val file = File(drawing.filePath)
                    if (file.exists()) {
                        thumbnailBitmap = BitmapFactory.decodeFile(file.path)

                    } else {
                        Log.e("StartScreen", "File does not exist: ${file.path}")
                    }
                    isLoading = false
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("canvas_screen/${drawing.id}")
                        }
                ) {
                    if (isLoading) {
                        Text(text = "Loading...", modifier = Modifier.size(100.dp))
                    } else {
                        thumbnailBitmap?.let { bitmap ->
                            androidx.compose.foundation.Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Drawing Thumbnail",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = "Drawing ID: ${drawing.id}")
                        Text(text = "Server Drawing ID: ${drawing.serverDrawingId ?: "Not shared"}")
                        Text(text = "Date: ${drawing.date}")
                        val buttonText = if (drawing.isShared) "Unshare" else "Share"
                        Button(
                            onClick = {
                                if (drawing.isShared) {
                                    viewModel.unshareDrawingFromServer(drawing.serverDrawingId ?: return@Button)
                                    viewModel.updateDrawingSharedStatus(drawing.id, false)
                                    viewModel.updateDrawingServerId(drawing.id, null)
                                } else {
                                    viewModel.uploadDrawingToServer(
                                        File(drawing.filePath),
                                        drawing.color,
                                        drawing.brushSize,
//                                        "default_user_id",
                                        userId,
                                        drawing.id
                                    ) { serverDrawingId ->
                                        viewModel.updateDrawingSharedStatus(drawing.id, true)
                                        viewModel.updateDrawingServerId(drawing.id, serverDrawingId)
                                    }
                                }
                            }
                        ) {
                            Text(text = buttonText)
                        }

                    }
                }
            }
        }
    }
}

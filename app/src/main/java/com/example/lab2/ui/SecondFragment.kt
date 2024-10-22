package com.example.lab2.ui


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab2.BrushShape
import com.example.lab2.CustomView
import com.example.lab2.CustomViewModel


@Composable
fun CanvasScreen(
    navController: NavController,
    drawingId: Int?,
    importedBitmap: Bitmap?,
    viewModel: CustomViewModel = viewModel(),
    onImportImageClick: () -> Unit
) {
    var localBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var localColor by remember { mutableStateOf(Color.Black) }
    var localBrushSize by remember { mutableStateOf(10f) }
    var brushShape by remember { mutableStateOf(BrushShape.ROUND) }
    var customViewReference: CustomView? by remember { mutableStateOf(null) }

    if (drawingId != null) {
        Log.d("CanvasScreen", "Drawing ID: $drawingId")
        val drawingData by viewModel.loadDrawingFromDatabase(drawingId).collectAsState(initial = null)

        drawingData?.let {
            Log.d("CanvasScreen", "Loading bitmap from file path: ${it.filePath}")
            localBitmap = BitmapFactory.decodeFile(it.filePath)
            if (localBitmap == null) {
                Log.e("CanvasScreen", "Failed to load bitmap from path: ${it.filePath}")
            } else {
                Log.d("CanvasScreen", "Bitmap successfully loaded")
            }
        }
    } else if (importedBitmap != null) {
        localBitmap = importedBitmap
        Log.d("CanvasScreen", "Using imported bitmap")
    } else {
        localBitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
    }

    BackHandler(true) {
        if (customViewReference?.hasDrawnAnything() == true) {
            val finalBitmap = customViewReference?.getBitmap()
            finalBitmap?.let {
                viewModel.saveDrawingToDatabase(
                    navController.context,
                    it,
                    localColor.toArgb(),
                    localBrushSize
                ) { success ->
                    if (success) {
                        navController.popBackStack()
                    } else {
                        Toast.makeText(navController.context, "Failed to save drawing", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            navController.popBackStack()
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CustomView(context = context, attrs = null).apply {
                    localBitmap?.let {
                        setBitmap(it)
                        Log.d("CanvasScreen", "Bitmap set successfully in CustomView")
                    }
                    setColor(localColor.toArgb())
                    setBrushSize(localBrushSize)
                    setBrushShape(brushShape)
                    customViewReference = this
                }
            },
            update = { customView ->
                localBitmap?.let {
                    Log.d("CanvasScreen", "Updating bitmap in CustomView")
                    customView.setBitmap(it)
                } ?: Log.e("CanvasScreen", "localBitmap is null during update in CustomView")
                customView.setColor(localColor.toArgb())
                customView.setBrushSize(localBrushSize)
                customView.setBrushShape(brushShape)
                customView.invalidate() // Force view to refresh
            }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            DrawingUI(
                localColor,
                localBrushSize,
                customViewReference,
                onBrushShapeChange = { newBrushShape ->
                    brushShape = newBrushShape
                    customViewReference?.setBrushShape(newBrushShape)
                    customViewReference?.invalidate()
                },
                onBrushSizeChange = { newSize ->
                    localBrushSize = newSize
                    customViewReference?.setBrushSize(newSize)
                    customViewReference?.invalidate()
                },
                onColorChange = { newColor ->
                    localColor = newColor
                    customViewReference?.setColor(newColor.toArgb())
                    customViewReference?.invalidate()
                },
                onImportImageClick = onImportImageClick,
                onSaveToGalleryClick = {
                    customViewReference?.let {
                        val bitmapToSave = it.getBitmap()
                        viewModel.saveDrawingToGallery(navController.context, bitmapToSave) { success ->
                            if (success) {
                                Toast.makeText(navController.context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(navController.context, "Failed to save to Gallery", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }


            )

        }
    }
    LaunchedEffect(localColor, localBrushSize, brushShape) {
        customViewReference?.let {
            it.setColor(localColor.toArgb())
            it.setBrushSize(localBrushSize)
            it.setBrushShape(brushShape)
            it.invalidate()
        }
    }


}

@Composable
fun DrawingUI(
    currentColor: Color,
    currentBrushSize: Float,
    customView: CustomView?,
    onBrushShapeChange: (BrushShape) -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onColorChange: (Color) -> Unit,
    onImportImageClick: () -> Unit,
    onSaveToGalleryClick: () -> Unit

) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                onBrushShapeChange(BrushShape.ROUND)
                customView?.setBrushShape(BrushShape.ROUND)
                customView?.invalidate()
            }) {
                Text(text = "Round Brush")
            }
            Button(onClick = {
                onBrushShapeChange(BrushShape.SQUARE)
                customView?.setBrushShape(BrushShape.SQUARE)
                customView?.invalidate()
            }) {
                Text(text = "Square Brush")
            }
        }
        Slider(
            value = currentBrushSize,
            onValueChange = { newSize ->
                onBrushSizeChange(newSize)
                customView?.setBrushSize(newSize)
                customView?.invalidate()
            },
            valueRange = 1f..50f,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .semantics {
                    stateDescription = "Brush Size Slider: ${currentBrushSize.toInt()}" }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ColorButton(color = Color.Blue, contentDescription = "Blue Color") { selectedColor ->
                onColorChange(selectedColor)
                customView?.setColor(selectedColor.toArgb())
                customView?.invalidate()
            }
            ColorButton(color = Color.Red, contentDescription = "Red Color") { selectedColor ->
                onColorChange(selectedColor)
                customView?.setColor(selectedColor.toArgb())
                customView?.invalidate()
            }
            ColorButton(color = Color.Black, contentDescription = "Black Color") { selectedColor ->
                onColorChange(selectedColor)
                customView?.setColor(selectedColor.toArgb())
                customView?.invalidate()
            }
            ColorButton(color = Color.White, contentDescription = "White Color") { selectedColor ->
                onColorChange(selectedColor)
                customView?.setColor(selectedColor.toArgb())
                customView?.invalidate()
            }
        }

        Button(onClick = onImportImageClick) {
            Text(text = "Import Image")
        }

        Button(onClick = onSaveToGalleryClick) {
            Text(text = "Save to Gallery")
        }


    }
}

@Composable
fun ColorButton(color: Color, contentDescription: String, onClick: (Color) -> Unit) {
    IconButton(onClick = { onClick(color) }, modifier = Modifier.size(50.dp)) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color)
                .semantics { this.contentDescription = contentDescription }
        )
    }
}

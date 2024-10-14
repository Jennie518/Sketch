package com.example.lab2.ui


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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lab2.BrushShape
import com.example.lab2.CustomView
import com.example.lab2.CustomViewModel

@Composable
fun CanvasScreen(
    navController: NavController,
    drawingId: Int?,
    viewModel: CustomViewModel = viewModel()
) {
    var localBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var localColor by remember { mutableStateOf(Color.Black) }
    var localBrushSize by remember { mutableStateOf(10f) }
    var brushShape by remember { mutableStateOf(BrushShape.ROUND) }

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
            localColor = Color(it.color)
            localBrushSize = it.brushSize
        }
    } else {
        localBitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
    }

    BackHandler(true) {
        localBitmap?.let {
            viewModel.saveDrawingToDatabase(
                navController.context,
                it,
                localColor.toArgb(),
                localBrushSize
            )
        }
        navController.popBackStack()
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
                viewModel,
                localColor,
                localBrushSize,
                onBrushShapeChange = { newBrushShape ->
                    brushShape = newBrushShape
                },
                onBrushSizeChange = { newSize ->
                    localBrushSize = newSize
                },
                onColorChange = { newColor ->
                    localColor = newColor
                }
            )
        }
    }
}

@Composable
fun DrawingUI(
    viewModel: CustomViewModel,
    currentColor: Color,
    currentBrushSize: Float,
    onBrushShapeChange: (BrushShape) -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onColorChange: (Color) -> Unit
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
            Button(onClick = { onBrushShapeChange(BrushShape.ROUND) }) {
                Text(text = "Round Brush")
            }
            Button(onClick = { onBrushShapeChange(BrushShape.SQUARE) }) {
                Text(text = "Square Brush")
            }
        }
        Slider(
            value = currentBrushSize,
            onValueChange = { newSize ->
                onBrushSizeChange(newSize)
                viewModel.saveBrushSize(newSize)
            },
            valueRange = 1f..50f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ColorButton(color = Color.Blue) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
                onColorChange(selectedColor)
            }
            ColorButton(color = Color.Red) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
                onColorChange(selectedColor)
            }
            ColorButton(color = Color.Black) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
                onColorChange(selectedColor)
            }
            ColorButton(color = Color.White) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
                onColorChange(selectedColor)
            }
        }
    }
}


@Composable
fun ColorButton(color: Color, onClick: (Color) -> Unit) {
    IconButton(onClick = { onClick(color) }, modifier = Modifier.size(50.dp)) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color)
        )
    }
}

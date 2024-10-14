package com.example.lab2


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast


import androidx.activity.addCallback
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.PathParser
import androidx.fragment.app.activityViewModels
import com.example.lab2.databinding.FragmentSecondBinding
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun CanvasScreen(
    navController: NavController,
    drawingId: Int?,
    viewModel: CustomViewModel = viewModel()
) {
    var localBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var localColor by remember { mutableStateOf(Color.Black) }
    var localBrushSize by remember { mutableStateOf(10f) }

    // Check if drawingId is null before making a call to the ViewModel
    if (drawingId != null) {
        val drawingData by viewModel.loadDrawingFromDatabase(drawingId).collectAsState(initial = null)

        drawingData?.let {
            localBitmap = BitmapFactory.decodeFile(it.filePath)
            localColor = Color(it.color)
            localBrushSize = it.brushSize
        }
    } else {
        // Handle the case where drawingId is null (e.g., show an empty canvas)
        Log.e("CanvasScreen", "Drawing ID is null. Cannot load drawing.")
        Toast.makeText(LocalContext.current, "No drawing found", Toast.LENGTH_SHORT).show()
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
        Log.d("BACK", "OnBackPressed and Drawing Saved")
        navController.popBackStack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CustomView(context = context, attrs = null).apply {
                    localBitmap?.let { setBitmap(it) }
                    setColor(localColor.toArgb())
                    setBrushSize(localBrushSize)
                }
            },
            update = { customView ->
                customView.setColor(localColor.toArgb())
                customView.setBrushSize(localBrushSize)
            }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            DrawingUI(viewModel, localColor, localBrushSize)
        }
    }
}

@Composable
fun DrawingUI(viewModel: CustomViewModel, currentColor: Color, currentBrushSize: Float) {

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
            Button(onClick = {}) {
                Text(text = "Round Brush")
            }
            Button(onClick = {}) {
                Text(text = "Square Brush")
            }
        }
        Slider(
            value = currentBrushSize,
            onValueChange = { newSize ->
                viewModel.saveBrushSize(newSize)
            }, valueRange = 1f..50f,
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
            }
            ColorButton(color = Color.Red) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
            }
            ColorButton(color = Color.Black) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
            }
            ColorButton(color = Color.White) { selectedColor ->
                viewModel.saveColor(selectedColor.toArgb())
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

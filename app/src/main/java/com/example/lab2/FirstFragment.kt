package com.example.lab2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun StartScreen(
    viewModel: CustomViewModel = viewModel(),
    onNavigate: (Int) -> Unit
) {
    // Collecting drawingId from the ViewModel
    val context = LocalContext.current
    val drawingId by viewModel.getLastSavedDrawingId().collectAsState(initial = null)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            drawingId?.let { id ->
                onNavigate(id)
            } ?: run {
                Toast.makeText(context, "Drawing ID is not found", Toast.LENGTH_SHORT)
                    .show()
            }
        }) {
            Text(text = "Let's draw something!")
        }
    }
}


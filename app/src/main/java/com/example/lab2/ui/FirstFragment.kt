package com.example.lab2.ui

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lab2.CustomViewModel

@Composable
fun StartScreen(
    navController: NavController,
    viewModel: CustomViewModel = viewModel()
) {
    val drawings by viewModel.getAllDrawings().observeAsState(listOf())

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


        LazyColumn {
            items(drawings) { drawing ->
                val thumbnailBitmap = remember(drawing.filePath) {
                    BitmapFactory.decodeFile(drawing.filePath)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {

                            navController.navigate("canvas_screen/${drawing.id}")
                        }
                ) {
                    thumbnailBitmap?.let { bitmap ->
                        androidx.compose.foundation.Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Drawing Thumbnail",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = "Drawing ID: ${drawing.id}")
                        Text(text = "Date: ${drawing.date}")
                    }
                }
            }
        }
    }
}


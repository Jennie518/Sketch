//package com.example.lab2.ui
//
//import android.graphics.BitmapFactory
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.lab2.CustomViewModel
//
//@Composable
//fun DrawingListScreen(
//    onDrawingClick: (Int) -> Unit,
//    viewModel: CustomViewModel = viewModel()
//) {
//    val drawings by viewModel.getAllDrawings().observeAsState(listOf())
//
//    LazyColumn {
//        items(drawings) { drawing ->
//            val thumbnailBitmap = remember(drawing.filePath) {
//                BitmapFactory.decodeFile(drawing.filePath)
//            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .clickable { onDrawingClick(drawing.id) }
//            ) {
//
//                thumbnailBitmap?.let { bitmap ->
//                    androidx.compose.foundation.Image(
//                        bitmap = bitmap.asImageBitmap(),
//                        contentDescription = "Drawing Thumbnail",
//                        modifier = Modifier.size(100.dp)
//                    )
//                }
//                Text(text = "Drawing ID: ${drawing.id}")
//                Text(text = "Date: ${drawing.date}")
//            }
//        }
//    }
//}

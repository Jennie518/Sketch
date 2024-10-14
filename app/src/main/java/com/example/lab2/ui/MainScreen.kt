//package com.example.lab2.ui
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.remember
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.lab2.CustomViewModel
//
//
//@Composable
//fun MainScreen(navController: NavHostController, viewModel: CustomViewModel) {
//    NavHost(navController, startDestination = "startScreen") {
//        composable("startScreen") {
//            StartScreen(onNavigate = { navController.navigate("drawingListScreen") })
//        }
//        composable("drawingListScreen") {
//            DrawingListScreen(
//                onDrawingClick = { drawingId ->
//                    navController.navigate("drawingDetailScreen/$drawingId")
//                },
//                viewModel = viewModel
//            )
//        }
//        composable("drawingDetailScreen/{drawingId}") { backStackEntry ->
//            val drawingId = backStackEntry.arguments?.getString("drawingId")?.toInt() ?: 0
//            DrawingDetailScreen(drawingId, viewModel)
//        }
//    }
//}
//
//@Composable
//fun DrawingDetailScreen(drawingId: Int, viewModel: CustomViewModel) {
//    val drawing = viewModel.loadDrawingFromDatabase(drawingId).collectAsState(initial = null).value
//
//    if (drawing != null) {
//        // 从文件路径加载 Bitmap
//        val bitmap = remember { BitmapFactory.decodeFile(drawing.filePath) }
//
//        // 展示画布
//        Box(modifier = Modifier.fillMaxSize()) {
//            if (bitmap != null) {
//                // 使用 AndroidView 或 Compose 绘图组件来展示 Bitmap
//                AndroidView(
//                    factory = { context ->
//                        CustomView(context).apply {
//                            setBitmap(bitmap) // 自定义绘图视图
//                        }
//                    },
//                    modifier = Modifier.fillMaxSize()
//                )
//            } else {
//                // 如果 bitmap 为空，显示错误信息
//                Text(text = "无法加载绘画", modifier = Modifier.align(Alignment.Center))
//            }
//        }
//    } else {
//        // 如果 drawing 为空，显示加载提示
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text(text = "正在加载...")
//        }
//    }
//}

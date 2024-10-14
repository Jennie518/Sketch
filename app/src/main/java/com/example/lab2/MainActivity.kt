package com.example.lab2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab2.ui.CanvasScreen
import com.example.lab2.ui.DrawingListScreen
import com.example.lab2.ui.StartScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeNavigation()
        }
        // Add a `ComposeView` to the activity's layout.
//        val composeView = ComposeView(this)
//        setContent {package com.example.lab2
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            ComposeNavigation()
//        }
//        // Add a `ComposeView` to the activity's layout.
////        val composeView = ComposeView(this)
////        setContent {
////            // Composable function -> will be rendered in the `ComposeView`
////            NewScreenComposable()
////        }
////
////
//
//
////        setContentView(R.layout.activity_main)
////
////        if (savedInstanceState == null) {
////            supportFragmentManager.beginTransaction()
////                .replace(R.id.fragment_container, FirstFragment())
////                .commit()
////        }
//    }
//}
//
//@Composable
//fun ComposeNavigation() {
//
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "StartScreen") {
//        composable("StartScreen") {
//            StartScreen(onNavigate = {drawingId ->
//                navController.navigate("CanvasScreen/$drawingId")})
//        }
//        composable("CanvasScreen/{drawingId}") { backStateEntry ->
//            val drawingId = backStateEntry.arguments?.getString("drawingId")?.toIntOrNull()?:0
//            CanvasScreen(navController = navController,drawingId = drawingId)
//        }
//    }
//
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    ComposeNavigation()
//}
//            // Composable function -> will be rendered in the `ComposeView`
//            NewScreenComposable()
//        }
//
//


//        setContentView(R.layout.activity_main)
//
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, FirstFragment())
//                .commit()
//        }
    }
}

@Composable
fun ComposeNavigation() {
    val navController = rememberNavController()

//    NavHost(navController = navController, startDestination = "StartScreen") {
//        composable("StartScreen") {
//            StartScreen(onNavigate = { drawingId ->
//                navController.navigate("CanvasScreen/$drawingId")
//            })
//        }
//        composable("CanvasScreen/{drawingId}") { backStackEntry ->
//            val drawingId = backStackEntry.arguments?.getString("drawingId")?.toIntOrNull() ?: 0
//            CanvasScreen(navController = navController, drawingId = drawingId)
//        }
//    }
    NavHost(navController = navController, startDestination = "start_screen") {
        composable("start_screen") { StartScreen(navController) }
        composable("canvas_screen") { CanvasScreen(navController = navController, drawingId = null) } // 这里处理新画布
        composable("canvas_screen/{drawingId}") { backStackEntry ->
            val drawingId = backStackEntry.arguments?.getString("drawingId")?.toInt()
            CanvasScreen(navController, drawingId) // 传递drawingId
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeNavigation()
}

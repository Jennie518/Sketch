package com.example.lab2

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab2.ui.CanvasScreen
import com.example.lab2.ui.StartScreen
import com.example.lab2.ui.LoginSignupScreen
import com.example.lab2.ui.userId
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var importImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var customViewModel: CustomViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        customViewModel = CustomViewModel(application)
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: "default_user_id"
        importImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    customViewModel.importImage(this, it, onSuccess = { bitmap ->
                        customViewModel.saveImportedBitmap(bitmap)
                    }, onFailure = {
                        Toast.makeText(this, "Failed to import image", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
        setContent {
            ComposeNavigation(
                onImportImageClick = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    importImageLauncher.launch(intent)
                },
                customViewModel = customViewModel,
                auth = auth,
                userId = userId
            )
        }
    }
}


@Composable
fun ComposeNavigation(
    onImportImageClick: () -> Unit,
    customViewModel: CustomViewModel,
    auth: FirebaseAuth, // Receive Firebase Auth
    userId: String
) {
    val navController = rememberNavController()

    val importedBitmap = customViewModel.importedBitmap.collectAsState().value

    NavHost(navController = navController, startDestination = "login_signup_screen") {
        composable("login_signup_screen") {
            LoginSignupScreen(navController = navController, auth = auth) // Navigate to Login/Signup screen
        }
        composable("start_screen") { StartScreen(navController, onImportImageClick = onImportImageClick, viewModel = customViewModel,userId = userId) }
        composable("canvas_screen") {
            CanvasScreen(
                navController = navController,
                drawingId = null,
//                importedBitmap = importedBitmap,
                onImportImageClick = onImportImageClick,
                viewModel = customViewModel,
                userId = userId
            )
        }
        composable("canvas_screen/{drawingId}") { backStackEntry ->
            val drawingId = backStackEntry.arguments?.getString("drawingId")?.toInt()
            CanvasScreen(
                navController = navController,
                drawingId = drawingId,
//                importedBitmap = importedBitmap,
                onImportImageClick = onImportImageClick,
                viewModel = customViewModel,
                userId = userId
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val fakeCustomViewModel = CustomViewModel(application = Application())

    ComposeNavigation(
        onImportImageClick = {},
        customViewModel = fakeCustomViewModel,
        auth = FirebaseAuth.getInstance(), // Mock Firebase Auth for preview
        userId = userId

    )
}

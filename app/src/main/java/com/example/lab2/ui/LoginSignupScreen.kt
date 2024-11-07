package com.example.lab2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginSignupScreen(navController: NavController, auth: FirebaseAuth) {
    // Toggle state to switch between login and signup forms
    var isLoginScreen by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = if (isLoginScreen) "Login" else "Sign Up",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isLoginScreen) {
            Spacer(modifier = Modifier.height(8.dp))
            // Confirm Password field for signup
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message display
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login/Signup button
        Button(
            onClick = {
                if (isLoginScreen) {
                    // Handle login
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Navigate to the StartScreen
                                navController.navigate("start_screen") {
                                    popUpTo("login_signup_screen") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Login failed: ${task.exception?.message}"
                            }
                        }
                } else {
                    // Handle signup
                    if (password == confirmPassword) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("start_screen") {
                                        popUpTo("login_signup_screen") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = "Signup failed: ${task.exception?.message}"
                                }
                            }
                    } else {
                        errorMessage = "Passwords do not match."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLoginScreen) "Login" else "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Toggle between Login and Signup
        Text(
            text = if (isLoginScreen) "Don't have an account? Sign up" else "Already have an account? Login",
            modifier = Modifier
                .clickable { isLoginScreen = !isLoginScreen }
                .padding(8.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

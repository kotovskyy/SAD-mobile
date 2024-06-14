package com.example.sad.HomeActivity

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sad.MainActivity
import com.example.sad.auth.SecureStorage

@Composable
fun ProfileScreen(navController: NavController){
    val context = LocalContext.current

    Scaffold(
        topBar = { HomeTopBar("Profile") },
        bottomBar = { HomeBottomNavigationBar(navController = navController, selectedItem = "profile") }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Profile Screen")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    // Clear the stored token
                    SecureStorage.clearToken(context)
                    // Navigate back to the login screen
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as HomeActivity).finish()
                }
            ) {
                Text("Logout")
            }
        }
    }
}

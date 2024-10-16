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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sad.LoginSignup.BottomNavigationBar
import com.example.sad.MainActivity
import com.example.sad.SADApplication
import com.example.sad.api.auth.SecureStorage
import com.example.sad.ui.utils.homeNavItems
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController){
    val context = LocalContext.current
    val application = context.applicationContext
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { HomeTopBar("Profile") },
        bottomBar = { BottomNavigationBar(navController, "profile", homeNavItems) }
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
                    coroutineScope.launch {
                        // remove all data from the database
                        (application as SADApplication).repository.clearAllData()
                    }
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

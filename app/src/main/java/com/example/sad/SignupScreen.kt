package com.example.sad

import android.content.Context
import android.widget.Toast
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

@Composable
fun SignupScreen(navController: NavController){
    val context = LocalContext.current

    Scaffold(
        topBar = { MainTopAppBar(title = "Sign up")},
        bottomBar = { MainBottomNavigationBar(navController = navController, selectedItem = "signup") }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SignupForm(context)
        }
    }
}

@Composable
fun SignupForm(context: Context){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        EmailField()
        Spacer(modifier = Modifier.height(10.dp))
        PasswordField(placeholder = "Password")
        Spacer(modifier = Modifier.height(10.dp))
        PasswordField(placeholder = "Confirm password")
        Spacer(modifier = Modifier.height(10.dp))
        SignupButton(context)
    }
}

@Composable
fun SignupButton(context: Context){
    OutlinedButton(
        onClick = {
            Toast.makeText(context, "Sign up attempt", Toast.LENGTH_LONG).show()
        }
    ) {
        Text(text = "Sign up")
    }
}
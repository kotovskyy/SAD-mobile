package com.example.sad

import android.graphics.drawable.shapes.Shape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DevicesScreen(navController: NavController){
    Scaffold(
        topBar = { HomeTopBar("Devices") },
        bottomBar = { HomeBottomNavigationBar(navController = navController, selectedItem = "devices") },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { /*TODO*/ }
            ) {
                Text("Add")
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Devices Screen")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

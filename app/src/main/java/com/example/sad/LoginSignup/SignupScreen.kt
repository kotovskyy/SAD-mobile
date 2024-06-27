package com.example.sad.LoginSignup

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sad.Login
import com.example.sad.api.auth.AuthRetrofitInstance
import com.example.sad.api.auth.SignupRequest
import com.example.sad.api.auth.SignupResponse
import com.example.sad.navigateSingleOnTop

@Composable
fun SignupScreen(navController: NavController){
    val context = LocalContext.current

    Scaffold(
        topBar = { MainTopAppBar(title = "Sign up") },
        bottomBar = { MainBottomNavigationBar(navController = navController, selectedItem = "signup") }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SignupForm(context, navController)
        }
    }
}

@Composable
fun SignupForm(context: Context, navController: NavController){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(ScrollState(0))
    ) {
        OutlinedTextField(value = username, onValueChange = {username = it}, placeholder = {
            Text(
                "Your name",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
                )
        })
        Spacer(modifier = Modifier.height(10.dp))
        EmailField(email = email, onEmailChange = { email = it })
        Spacer(modifier = Modifier.height(10.dp))
        PasswordField(placeholder = "Password", password = password, onPasswordChange = { password = it })
        Spacer(modifier = Modifier.height(10.dp))
        PasswordField(placeholder = "Password again", password = confirmPassword, onPasswordChange = { confirmPassword = it })
        Spacer(modifier = Modifier.height(10.dp))
        SignupButton(context, username, email, password, confirmPassword, navController)
    }
}

@Composable
fun SignupButton(context: Context, username: String, email: String, password: String, password2: String, navController: NavController) {
    OutlinedButton(onClick = {
        val signupRequest = SignupRequest(username = username, email = email, password = password, password2 = password2)
        AuthRetrofitInstance.api.register(signupRequest).enqueue(object : retrofit2.Callback<SignupResponse> {
            override fun onResponse(call: retrofit2.Call<SignupResponse>, response: retrofit2.Response<SignupResponse>) {
                Log.d("SignupResponse", "Response: ${response.body()}")
                if (response.isSuccessful && response.code() == 201) {
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_LONG).show()
                    navController.navigateSingleOnTop(Login.route)
                } else {
                    val errorMessage = response.body()?.message ?: "Unknown registration error"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    Log.d("SignupError", "Failed with body: ${response.body()} and status code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<SignupResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }) {
        Text(text = "Sign Up")
    }
}
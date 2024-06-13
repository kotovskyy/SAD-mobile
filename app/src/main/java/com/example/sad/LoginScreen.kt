package com.example.sad

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController){
    val context = LocalContext.current

    Scaffold(
        topBar = { MainTopAppBar(title = "Login")},
        bottomBar = { MainBottomNavigationBar(navController = navController, selectedItem = "login") }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LoginForm(context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(title: String) {
    TopAppBar(
        title = { Text(text = title)},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

data class BottomNavItem(
    val title: String,
    @DrawableRes var iconId: Int,
    val route: String
)

@Composable
fun MainBottomNavigationBar(navController: NavController, selectedItem: String) {

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Login",
            iconId = R.drawable.login_24,
            route = "login"
        ),
        BottomNavItem(
            title = "SignUp",
            iconId = R.drawable.signup_24,
            route = "signup"
        )
    )

    BottomAppBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = item.title)
               },
                label = { Text(item.title) },
                selected = selectedItem == item.route,
                onClick = {
                    navController.navigateSingleOnTop(item.route)
                }
            )
        }
    }
}


@Composable
fun LoginForm(context: Context){
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
        LoginButton(context)
    }
}

@Composable
fun EmailField(){
    var email by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = email,
        onValueChange = {email = it},
        placeholder = {
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        visualTransformation = VisualTransformation.None
    )
}

@Composable
fun PasswordField(placeholder: String = "Password"){
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }


    val trailingIconPainter = if (isPasswordVisible){
        painterResource(id = R.drawable.visibility_on_24)
    } else {
        painterResource(id = R.drawable.visibility_off_24)
    }

    val trailingIconButton = @Composable {
        if (password.isNotEmpty()){
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    painter = trailingIconPainter,
                    contentDescription = "Password Visibility",
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
    }

    OutlinedTextField(
        value = password,
        onValueChange = {password = it},
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        },
        trailingIcon = trailingIconButton,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    )

}

@Composable
fun LoginButton(context: Context){
    OutlinedButton(
        onClick = {
            Toast.makeText(context, "Login attempt", Toast.LENGTH_LONG).show()
        }
    ) {
        Text(text = "Log in")
    }
}

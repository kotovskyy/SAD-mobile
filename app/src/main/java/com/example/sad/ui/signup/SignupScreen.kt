package com.example.sad.ui.signup

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sad.LogIn
import com.example.sad.ui.login.DontHaveAccount
import com.example.sad.ui.login.EmailField
import com.example.sad.ui.login.LoginForm
import com.example.sad.ui.login.PasswordField
import com.example.sad.ui.login.RememberMe
import com.example.sad.ui.onboarding.HeaderBox
import com.example.sad.ui.onboarding.LargeTextBody
import com.example.sad.ui.onboarding.OutlinedPrimaryButton
import com.example.sad.ui.onboarding.PrimaryButton
import com.example.sad.ui.theme.SADTheme

@Composable
fun SignupScreen(navController: NavController){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderBox(
            mainText = "Sign up",
            secondaryText = "",
            dividerThickness = (-1).dp,
        )
        Spacer(modifier = Modifier.height(0.dp))
        SignUpForm(onLoginClick = { navController.navigate(LogIn.route) })
    }
}

@Composable
fun SignUpForm(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        FormTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
        )
        Spacer(modifier = Modifier.height(22.dp))
        EmailField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
        )
        Spacer(modifier = Modifier.height(22.dp))
        PasswordField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
        )
        Spacer(modifier = Modifier.height(22.dp))
        PasswordField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(10.dp)
                ),
            placeholder = "Confirm password"
        )
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PrimaryButton(
                text = "Sign up",
                onClick = {},
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 20.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        AlreadyHaveAccount(onLoginClick = onLoginClick)
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value:String = "",
    placeholder: String = "First name"
) {
    var text by remember { mutableStateOf(value) }
    val focusManager = LocalFocusManager.current

    val leadingIcon  = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = "Name",
            tint = MaterialTheme.colorScheme.primary,
        )
    }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        },
        leadingIcon = leadingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        visualTransformation = VisualTransformation.None,
        modifier = modifier
    )
}

@Composable
fun AlreadyHaveAccount(
    onLoginClick: () -> Unit
){
    Column (
        modifier = Modifier.padding(horizontal = 22.dp)
    ){
        LargeTextBody(
            text = "Already have account? Log in:",
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedPrimaryButton(
                text = "Log in",
                onClick = onLoginClick,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 10.dp)
            )
        }
    }
}

@Preview
@Composable
fun SignupScreenPreview(){
    SADTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val navController = rememberNavController()
            SignupScreen(navController)
        }
    }
}
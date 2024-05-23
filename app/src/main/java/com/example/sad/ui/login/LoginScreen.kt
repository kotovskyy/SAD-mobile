package com.example.sad.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sad.R
import com.example.sad.SignUp
import com.example.sad.ui.onboarding.HeaderBox
import com.example.sad.ui.onboarding.LargeTextBody
import com.example.sad.ui.onboarding.OutlinedPrimaryButton
import com.example.sad.ui.onboarding.PrimaryButton
import com.example.sad.ui.theme.SADTheme

@Composable
fun LoginScreen(navController: NavController){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderBox(
            mainText = "Log in",
            secondaryText = "",
            dividerThickness = (-1).dp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        LoginForm(onSignUpClick = { navController.navigate(SignUp.route) })
    }
}

@Composable
fun LoginForm(
    onSignUpClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
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
        RememberMe()
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PrimaryButton(
                text = "Log in",
                onClick = {},
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 20.dp)
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
        DontHaveAccount(onSignUpClick = onSignUpClick)
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun DontHaveAccount(
    onSignUpClick: () -> Unit
){
    Column (
        modifier = Modifier.padding(horizontal = 22.dp)
    ){
        LargeTextBody(
            text = "Don't have account yet? Create one:",
        )
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedPrimaryButton(
                text = "Sign up",
                onClick = onSignUpClick,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
fun EmailField(
    modifier: Modifier = Modifier,
    value:String = "",
    placeholder: String = "Email address"
)
{
    var email by remember { mutableStateOf(value) }
    val focusManager = LocalFocusManager.current

    val leadingIcon  = @Composable {
        Icon(
            Icons.Default.Email,
            contentDescription = "Email",
            tint = MaterialTheme.colorScheme.primary,
        )
    }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
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
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        visualTransformation = VisualTransformation.None,
        modifier = modifier
    )
}

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    value:String = "",
    placeholder: String = "Password",
)
{
    var password by remember { mutableStateOf(value) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val leadingIcon  = @Composable {
        Icon(
            Icons.Default.Lock,
            contentDescription = "Password",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    val trailingIcon = if (isPasswordVisible){
        painterResource(id = R.drawable.visibility_on_24)
    } else {
        painterResource(id = R.drawable.visibility_off_24)
    }

    val trailingIconButton  = @Composable {
        if (password.isNotEmpty()){
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    trailingIcon,
                    contentDescription = "Password",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIconButton,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier
    )
}

@Composable
fun RememberMe(){
    var isChecked by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 35.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = !isChecked })
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "Remember me",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .clickable { isChecked = !isChecked }
        )
    }
}

//@Preview
//@Composable
//fun LoginScreenPreview(){
//    SADTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            LoginScreen()
//        }
//    }
//}
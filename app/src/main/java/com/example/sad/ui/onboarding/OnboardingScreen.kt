package com.example.sad.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.sad.ui.theme.SADTheme
import com.example.sad.ui.theme.Typography

@Composable
fun OnboardingScreen(){
    Column {

    }
}

@Composable
fun HeaderBox(){
    Surface {
        Text(
            text = "Welcome",
            fontFamily = FontFamily.
        )
    }
}


@Preview
@Composable
fun OnboardingScreenPreview(){
    SADTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OnboardingScreen()
        }
    }
}
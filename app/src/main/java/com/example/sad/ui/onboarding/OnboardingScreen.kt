package com.example.sad.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sad.Devices
import com.example.sad.LogIn
import com.example.sad.SignUp
import com.example.sad.ui.theme.SADTheme

@Composable
fun OnboardingScreen(navController: NavController){
    Column {
        HeaderBox(mainText = "Welcome", secondaryText = "to Your Sleep Companion")
        Spacer(modifier = Modifier.height(30.dp))
        OnboardingTextBoxes(
            upperText = "We're excited to have You join our community dedicated to improving your sleep quality!",
            lowerText = "With our Sleep Aid Companion App, you'll embark on a journey towards healthier, more restful nights."
        )
        Spacer(modifier = Modifier.height(30.dp))
        ReadyToBegin(
            headingText = "Ready to begin?",
            paragraphText ="Click Sign up to create a new account. Choose Log in option if you already have one",
            onLoginClick = { navController.navigate(LogIn.route) },
            onSignUpClick = { navController.navigate(SignUp.route) }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row (
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            OutlinedPrimaryButton(
                text = "Devices",
                onClick = {
                    navController.navigate(Devices.route)
                }
            )
        }
    }
}

@Composable
fun OnboardingMainHeading(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSecondary,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    overflow: TextOverflow = TextOverflow.Ellipsis
){
    Text(
        text = text,
        style = style,
        color = color,
        maxLines = 1,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun OnboardingSecondaryHeading(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = MaterialTheme.colorScheme.secondary,
    overflow: TextOverflow = TextOverflow.Ellipsis
){
    Text(
        text = text,
        style = style,
        color = color,
        maxLines = 1,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun HeaderBox(
    mainText: String,
    mainTextColor: Color = MaterialTheme.colorScheme.onSecondary,
    mainTextStyle: TextStyle = MaterialTheme.typography.headlineLarge,
    bgColor: Color = MaterialTheme.colorScheme.secondary,
    secondaryText: String,
    secondaryTextColor: Color = MaterialTheme.colorScheme.secondary,
    secondaryTextStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    dividerThickness: Dp = 1.dp,
    dividerColor: Color = MaterialTheme.colorScheme.secondary
){
    Column {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        )
        {
            OnboardingMainHeading(
                text = mainText,
                color = mainTextColor,
                style = mainTextStyle,
                modifier = Modifier
                    .padding(start = 22.dp)
                    .padding(vertical = 22.dp)
            )
        }
        OnboardingSecondaryHeading(
            text = secondaryText,
            style = secondaryTextStyle,
            color = secondaryTextColor,
            modifier = Modifier
                .padding(start = 22.dp)
                .padding(vertical = 10.dp)
        )
        Divider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun LargeTextBody(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign = TextAlign.Justify
){
    Text(
        text = text,
        style = style,
        textAlign = textAlign,
        color = color,
        modifier = modifier
    )
}
@Composable
fun OnboardingTextBoxes(
    upperText: String,
    upperTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    upperTextAlign: TextAlign = TextAlign.Justify,
    lowerText: String,
    lowerTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    lowerTextAlign: TextAlign = TextAlign.End
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            LargeTextBody(
                text = upperText,
                textAlign = upperTextAlign,
                style = upperTextStyle,
                modifier = Modifier
                    .padding(start = 22.dp, end = 22.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row (
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 22.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .wrapContentWidth()
        ){
            LargeTextBody(
                text = lowerText,
                style = lowerTextStyle,
                textAlign = lowerTextAlign,
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .padding(start = 10.dp, end = 22.dp)
                    .padding(vertical = 25.dp)
            )
        }
    }
}

@Composable
fun ReadyToBegin(
    headingText: String,
    headingColor: Color = MaterialTheme.colorScheme.primary,
    paragraphText: String,
    paragraphTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    paragraphTextAlign: TextAlign = TextAlign.Justify,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
){
    Column {
        OnboardingSecondaryHeading(
            text = headingText,
            color = headingColor,
            modifier = Modifier
                .padding(start = 22.dp)
                .padding(vertical = 10.dp)
        )
        LargeTextBody(
            text = paragraphText,
            style = paragraphTextStyle,
            textAlign = paragraphTextAlign,
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        )
        {
            PrimaryButton(
                text = "Sign up",
                onClick = onSignUpClick,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
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

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    shape: Shape = RoundedCornerShape(15.dp),
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        shape = shape
    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = modifier
        )
    }
}

@Composable
fun OutlinedPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    border: BorderStroke = BorderStroke(width = 3.dp, color = MaterialTheme.colorScheme.primary),
    shape: Shape = RoundedCornerShape(15.dp),
    onClick: () -> Unit
){
    OutlinedButton(
        onClick = onClick,
        shape = shape,
        border = border
    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = modifier
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun OnboardingScreenPreview(){
    SADTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            OnboardingScreen(navController)
        }
    }
}
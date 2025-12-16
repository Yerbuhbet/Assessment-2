package com.example.assessment_2.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Routes {
    const val Splash = "splash"
    const val Walkthrough = "walkthrough"
    const val Welcome = "welcome"
    const val SignUp = "signup"
    const val SignIn = "signin"
    const val ForgotPassword = "forgot_password"
    const val EnterOtp = "enter_otp"
    const val CreateNewPassword = "create_new_password"
    const val ResetPasswordResult = "reset_password_result"
    const val Home = "home"
}

@Composable
fun AppNavRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NavHost(navController = navController, startDestination = Routes.Splash) {
            composable(Routes.Splash) {
                SplashScreen(onFinished = { navController.navigate(Routes.Walkthrough) { popUpTo(Routes.Splash) { inclusive = true } } })
            }
            composable(Routes.Walkthrough) {
                WalkthroughScreen(onDone = { navController.navigate(Routes.Welcome) { popUpTo(Routes.Walkthrough) { inclusive = true } } })
            }
            composable(Routes.Welcome) {
                WelcomeScreen(
                    onSignUp = { navController.navigate(Routes.SignUp) },
                    onSignIn = { navController.navigate(Routes.SignIn) },
                )
            }
            composable(Routes.SignUp) {
                SignUpScreen(
                    onSuccess = { navController.navigate(Routes.Home) { popUpTo(Routes.Welcome) { inclusive = true } } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SignIn) {
                SignInScreen(
                    onForgotPassword = { navController.navigate(Routes.ForgotPassword) },
                    onSuccess = { navController.navigate(Routes.Home) { popUpTo(Routes.Welcome) { inclusive = true } } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.ForgotPassword) {
                ForgotPasswordScreen(
                    onNext = { navController.navigate(Routes.EnterOtp) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.EnterOtp) {
                EnterOtpScreen(
                    onNext = { navController.navigate(Routes.CreateNewPassword) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.CreateNewPassword) {
                CreateNewPasswordScreen(
                    onSubmit = { success ->
                        navController.navigate("${Routes.ResetPasswordResult}?success=$success")
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = "${Routes.ResetPasswordResult}?success={success}",
                arguments = listOf(navArgument("success") { type = NavType.BoolType; defaultValue = true })
            ) { backStackEntry ->
                val success = backStackEntry.arguments?.getBoolean("success") ?: true
                ResetPasswordResultScreen(
                    success = success,
                    onContinue = {
                        if (success) {
                            navController.navigate(Routes.Home) { popUpTo(Routes.Welcome) { inclusive = true } }
                        } else {
                            navController.popBackStack() // go back to fix
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.Home) {
                HomeScreen()
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1200)
        onFinished()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Your App", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalkthroughScreen(onDone: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { page ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Walkthrough ${page + 1}", style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(text = "Swipe to continue", style = MaterialTheme.typography.bodyMedium)
            }
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Get Started") }
        }
    }
}

@Composable
fun WelcomeScreen(onSignUp: () -> Unit, onSignIn: () -> Unit) {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onSignUp, modifier = Modifier.fillMaxWidth()) { Text("Sign Up") }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) { Text("Log In") }
        }
    }
}

@Composable
fun SignUpScreen(onSuccess: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        Scaffold { inner ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    loading = true
                    scope.launch {
                        delay(1200)
                        loading = false
                        onSuccess()
                    }
                }, modifier = Modifier.fillMaxWidth(), enabled = email.isNotBlank() && password.isNotBlank() && !loading) {
                    Text("Create account")
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onBack) { Text("Back") }
            }
        }
        if (loading) LoadingOverlay()
    }
}

@Composable
fun SignInScreen(onForgotPassword: () -> Unit, onSuccess: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        Scaffold { inner ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Log In", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    loading = true
                    scope.launch {
                        delay(1000)
                        loading = false
                        onSuccess()
                    }
                }, modifier = Modifier.fillMaxWidth(), enabled = email.isNotBlank() && password.isNotBlank() && !loading) {
                    Text("Sign in")
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onForgotPassword) { Text("Forgot Password?") }
                TextButton(onClick = onBack) { Text("Back") }
            }
        }
        if (loading) LoadingOverlay()
    }
}

@Composable
fun ForgotPasswordScreen(onNext: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Forgot Password", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, enabled = email.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Send OTP") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
fun EnterOtpScreen(onNext: () -> Unit, onBack: () -> Unit) {
    var otp by remember { mutableStateOf("") }
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter OTP Code", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = otp, onValueChange = { otp = it }, label = { Text("OTP Code") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, enabled = otp.length >= 4, modifier = Modifier.fillMaxWidth()) { Text("Verify") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
fun CreateNewPasswordScreen(onSubmit: (success: Boolean) -> Unit, onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val valid = password.length >= 6 && confirm == password

    Box(Modifier.fillMaxSize()) {
        Scaffold { inner ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Create New Password", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirm Password") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    loading = true
                    scope.launch {
                        delay(1000)
                        loading = false
                        onSubmit(valid)
                    }
                }, enabled = !loading && (password.isNotBlank() && confirm.isNotBlank()), modifier = Modifier.fillMaxWidth()) { Text("Reset Password") }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onBack) { Text("Back") }
            }
        }
        if (loading) LoadingOverlay()
    }
}

@Composable
fun ResetPasswordResultScreen(success: Boolean, onContinue: () -> Unit, onBack: () -> Unit) {
    val title = if (success) "Reset Password Successful" else "Reset Password Unsuccessful"
    val message = if (success) "Your password has been updated." else "There was an error resetting your password. Please try again."

    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text(if (success) "Continue" else "Try Again") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold { inner ->
        Box(modifier = Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
            Text("Home Screen", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        Modifier
            .fillMaxSize()
            .semantics { contentDescription = "loadingOverlay" },
        contentAlignment = Alignment.Center
    ) {
        // Dim background
        Box(
            Modifier
                .fillMaxSize()
                .padding(0.dp)
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(0.dp)
        )
        // Actual progress indicator
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Unspecified)
        }
    }
}

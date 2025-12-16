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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import com.example.assessment_2.R
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

    val orange = Color(0xFFFE6347)
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            // Pager content area takes most of the screen
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                WalkthroughPage(
                    pageIndex = page,
                    orange = orange,
                )
            }

            // Indicators + divider + buttons area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                PageIndicators(current = pagerState.currentPage, orange = orange)
                Spacer(Modifier.height(8.dp))
                // Thin divider line
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(top = 4.dp)
                        .semantics { }
                        .then(Modifier),
                ) {
                    // Light gray almost invisible divider
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 0.dp)
                            .align(Alignment.Center)
                            .background(Color(0xFFEAEAEA))
                    )
                }
                Spacer(Modifier.height(12.dp))

                if (pagerState.currentPage < 2) {
                    // Two buttons in one row: Skip (secondary) and Continue (primary)
                    RowButtons(
                        orange = orange,
                        onSkip = { onDone() },
                        onContinue = {
                            scope.launch {
                                val next = (pagerState.currentPage + 1).coerceAtMost(2)
                                pagerState.animateScrollToPage(next)
                            }
                        }
                    )
                } else {
                    // Single Get Started button
                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = orange,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Get Started")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowButtons(orange: Color, onSkip: () -> Unit, onContinue: () -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        // Skip - secondary emphasis
        Button(
            onClick = onSkip,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0x1AFE6347), // faded orange background
                contentColor = orange
            )
        ) { Text("Skip") }

        // Continue - primary
        Button(
            onClick = onContinue,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = orange,
                contentColor = Color.White
            )
        ) { Text("Continue") }
    }
}

@Composable
private fun WalkthroughPage(pageIndex: Int, orange: Color) {
    // Page-specific texts and inner image resources
    val (title, desc, innerRes) = when (pageIndex) {
        0 -> Triple(
            "Your Ultimate Pomodoro Productivity Assistant",
            "Focusify helps you stay on track, manage tasks, and work efficiently. Let’s get started with Focusify right now!",
            R.drawable.walkthrough_1
        )
        1 -> Triple(
            "Effortless Organization – All in One Place",
            "Easily categorize your work, stay organized, and conquer tasks with Focusify’s intuitive project and tag system.",
            R.drawable.walkthrough_2
        )
        else -> Triple(
            "Track Your Progress & Visualize Your Success",
            "Track your productivity over time, gain insights, and level up your efficiency. It’s time to achieve your goals.",
            R.drawable.walkthrough_3
        )
    }

    Box(Modifier.fillMaxSize()) {
        // Layer 1: Orange wave background (top ~50%)
        OrangeWave(
            color = orange,
            heightFraction = if (pageIndex == 0) 0.5f else 0.56f // page2/3 slightly lower
        )

        // Layer 2: Phone mockup + inner image
        PhoneMockupWithInnerImage(
            innerImageRes = innerRes,
        )

        // Layer 3: Texts on white area below
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PhoneMockupWithInnerImage(innerImageRes: Int) {
    // Sizes are approximate; adjust as needed.
    val phoneWidth = 240.dp
    val phoneHeight = 480.dp

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        // Place the phone so lower part dips into the white area
        Box(
            modifier = Modifier
                .padding(top = 64.dp)
                .size(width = phoneWidth, height = phoneHeight)
        ) {
            // Phone frame
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.android_mockup_mobile_phone_frame),
                contentDescription = null,
                modifier = Modifier.matchParentSize()
            )
            // Inner screen placeholder image centered with insets
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = innerImageRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun OrangeWave(color: Color, heightFraction: Float) {
    // Draw an orange area occupying ~heightFraction with a smooth wave at the bottom
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val orangeHeight = h * heightFraction

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, orangeHeight * 0.85f)
            // Smooth wave: down in center (valley) and up at sides
            cubicTo(
                w * 0.25f, orangeHeight * 1.05f,
                w * 0.35f, orangeHeight * 0.7f,
                w * 0.5f, orangeHeight * 0.9f
            )
            cubicTo(
                w * 0.65f, orangeHeight * 1.1f,
                w * 0.78f, orangeHeight * 0.95f,
                w, orangeHeight
            )
            lineTo(w, 0f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
private fun PageIndicators(current: Int, orange: Color) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { index ->
                if (index == current) {
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(28.dp)
                            .background(orange, shape = androidx.compose.foundation.shape.RoundedCornerShape(50))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFBDBDBD), shape = androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
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

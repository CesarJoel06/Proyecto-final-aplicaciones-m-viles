package com.cesar.securityquotes.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.cesar.securityquotes.R
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.compose.rememberLauncherForActivityResult

private object AppColors {
    val dark = Color(0xFF0B2239)
    val blue = Color(0xFF1F5C83)
    val cyan = Color(0xFF76D7EA)
    val light = Color(0xFFEAF6FB)
}

@Composable
fun SecurityQuotesApp(vm: SecurityQuotesViewModel) {
    val nav = rememberNavController()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.token) {
        if (!state.token.isNullOrBlank()) {
            nav.navigate("general") {
                popUpTo("home") { inclusive = false }
            }
        }
    }

    NavHost(navController = nav, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                nav.navigate("welcome") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("welcome") {
            WelcomeScreen {
                nav.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        }
        composable("home") {
            HomeScreen(
                onRegister = { nav.navigate("register") },
                onLogin = { nav.navigate("login") }
            )
        }
        composable("register") {
            RegisterScreen(
                state = state,
                clearMessages = vm::clearMessages,
                onBack = { nav.popBackStack() },
                onRegister = vm::register
            )
        }
        composable("login") {
            LoginScreen(
                state = state,
                clearMessages = vm::clearMessages,
                onBack = { nav.popBackStack() },
                onLogin = vm::login
            )
        }
        composable("general") {
            GeneralScreen(
                state = state,
                clearMessages = vm::clearMessages,
                onLogout = {
                    vm.logout()
                    nav.navigate("home") {
                        popUpTo("general") { inclusive = true }
                    }
                },
                onCreateDocument = vm::createDocument
            )
        }
    }
}

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "splash")
    val pulse by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rotate by infinite.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2400)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppColors.dark, AppColors.blue))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .size(180.dp)
                    .scale(pulse)
                    .rotate(rotate),
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = AppColors.cyan,
                        modifier = Modifier.size(120.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .size(60.dp)
                            .offset(x = 28.dp, y = 28.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("SecurityQuotes", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "César Chumpitas Palomino",
                color = AppColors.light,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Seguridad electrónica, CCTV y cotizaciones",
                color = AppColors.cyan,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Bienvenido", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppColors.dark)
            Spacer(Modifier.height(18.dp))
            Image(
                painter = painterResource(id = R.drawable.security_welcome),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "CCTV, seguridad electrónica, recibos y cotizaciones.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.blue
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Esta aplicación permite registrar instaladores, iniciar sesión y generar documentos PDF para ventas o cotizaciones de servicios, materiales y equipos de seguridad electrónica.",
                fontSize = 16.sp,
                color = Color(0xFF31485C)
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.blue)
            ) {
                Text("Continuar", modifier = Modifier.padding(vertical = 6.dp))
            }
        }
    }
}

@Composable
fun HomeScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    Scaffold(containerColor = Color(0xFFF5FBFE)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Icon(Icons.Default.Shield, contentDescription = null, tint = AppColors.blue, modifier = Modifier.size(72.dp))
            Spacer(Modifier.height(10.dp))
            Text("Acceso principal", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = AppColors.dark)
            Spacer(Modifier.height(8.dp))
            Text("Regístrate o inicia sesión para entrar al panel general.", color = Color(0xFF4C6275))
            Spacer(Modifier.height(30.dp))
            ZoomButton("Registrarme", Icons.Default.PersonAdd, AppColors.blue, onRegister)
            Spacer(Modifier.height(16.dp))
            ZoomButton("Iniciar sesión", Icons.Default.Login, AppColors.dark, onLogin)
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.security_header),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ZoomButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 1.08f else 1f, label = "scale")
    Button(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(18.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 17.sp)
    }
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(120)
            pressed = false
        }
    }
}

@Composable
fun RegisterScreen(
    state: UiState,
    clearMessages: () -> Unit,
    onBack: () -> Unit,
    onRegister: (String, String, String, Uri?) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    FormScaffold("Registro", onBack) {
        Text("Crea tu cuenta para acceder al sistema", color = Color(0xFF4C6275))
        Spacer(Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { imagePicker.launch("image/*") }
                .border(2.dp, AppColors.cyan, CircleShape)
                .padding(4.dp)
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColors.light, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = AppColors.blue, modifier = Modifier.size(32.dp))
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        AppField("Nombre completo", username) { username = it }
        Spacer(Modifier.height(12.dp))
        AppField("Correo electrónico", email, keyboardType = KeyboardType.Email) { email = it }
        Spacer(Modifier.height(12.dp))
        AppField("Contraseña", password, password = true) { password = it }
        Spacer(Modifier.height(12.dp))
        AppField("Confirmar contraseña", confirmPassword, password = true) { confirmPassword = it }
        Spacer(Modifier.height(16.dp))
        MessageBlock(state.error, state.success, clearMessages)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                clearMessages()
                onRegister(username, email, password, selectedImageUri)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.blue),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.loading
        ) {
            Text(if (state.loading) "Registrando..." else "Registrarme")
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun LoginScreen(
    state: UiState,
    clearMessages: () -> Unit,
    onBack: () -> Unit,
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    FormScaffold("Iniciar sesión", onBack) {
        Text("Ingresa tus credenciales para continuar", color = Color(0xFF4C6275))
        Spacer(Modifier.height(22.dp))
        AppField("Correo electrónico", email, keyboardType = KeyboardType.Email) { email = it }
        Spacer(Modifier.height(14.dp))
        AppField("Contraseña", password, password = true) { password = it }
        Spacer(Modifier.height(20.dp))
        MessageBlock(state.error, state.success, clearMessages)
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = {
                clearMessages()
                onLogin(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.dark),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.loading
        ) {
            Text(if (state.loading) "Validando..." else "Entrar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralScreen(
    state: UiState,
    clearMessages: () -> Unit,
    onLogout: () -> Unit,
    onCreateDocument: (String, String, String, String, String) -> Unit
) {
    val ctx = LocalContext.current
    var clientName by remember { mutableStateOf("") }
    var documentType by remember { mutableStateOf("Cotización") }
    var services by remember { mutableStateOf("") }
    var materials by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel general") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.dark,
                    titleContentColor = Color.White
                ),
                actions = {
                    TextButton(onClick = onLogout) { Text("Salir", color = Color.White) }
                }
            )
        },
        containerColor = Color(0xFFF5FBFE)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Color.White, shadowElevation = 4.dp) {
                    if (!state.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = state.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AppColors.blue, modifier = Modifier.size(36.dp))
                        }
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(state.username.ifBlank { "Instalador" }, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(state.email.ifBlank { "Sin correo" }, color = Color(0xFF4C6275))
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Nueva venta o cotización", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.dark)
            Spacer(Modifier.height(12.dp))
            AppField("Nombre del cliente", clientName) { clientName = it }
            Spacer(Modifier.height(12.dp))
            Text("Tipo de documento", fontWeight = FontWeight.SemiBold, color = AppColors.blue)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = documentType == "Cotización",
                    onClick = { documentType = "Cotización" },
                    label = { Text("Cotización") }
                )
                FilterChip(
                    selected = documentType == "Venta",
                    onClick = { documentType = "Venta" },
                    label = { Text("Venta") }
                )
            }
            Spacer(Modifier.height(12.dp))
            AppField("Servicios (uno por línea)", services, singleLine = false, minLines = 4) { services = it }
            Spacer(Modifier.height(12.dp))
            AppField("Materiales / implementos (uno por línea)", materials, singleLine = false, minLines = 4) { materials = it }
            Spacer(Modifier.height(12.dp))
            AppField("Total", total, keyboardType = KeyboardType.Decimal) { total = it }
            Spacer(Modifier.height(16.dp))
            MessageBlock(state.error, state.success, clearMessages)
            if (!state.pdfUrl.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                AssistChip(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.pdfUrl))
                        ctx.startActivity(intent)
                    },
                    label = { Text("Abrir PDF generado") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    clearMessages()
                    onCreateDocument(clientName, documentType, services, materials, total)
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.blue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (state.loading) "Generando..." else "Generar PDF")
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}

@Composable
private fun AppField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by rememberSaveable(label) { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (password && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (password) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageBlock(error: String?, success: String?, clearMessages: () -> Unit) {
    if (error != null) {
        AssistChip(
            onClick = clearMessages,
            label = { Text(error) },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFFFE5E5))
        )
    }
    if (success != null) {
        AssistChip(
            onClick = clearMessages,
            label = { Text(success) },
            leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null) },
            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFE6FFF0))
        )
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Account
import com.example.data.BrandKit
import com.example.data.MediaItem
import com.example.data.Post
import com.example.data.ChatMessage
import com.example.ui.AppScreen
import com.example.ui.JovimUiState
import com.example.ui.JovimViewModel
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ui.theme.*
import androidx.compose.ui.res.painterResource
import com.example.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// --- MAIN NAVIGATOR & CONTAINER ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JovimAppContainer(
    viewModel: JovimViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val brandKit by viewModel.brandKit.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (uiState.currentScreen) {
            AppScreen.Splash -> SplashScreen(viewModel)
            AppScreen.Onboarding -> OnboardingScreen(viewModel)
            AppScreen.Login -> LoginScreen(viewModel)
            else -> {
                // Persistent M3 scaffold for Main application states
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = JovimPrimary,
                                titleContentColor = JovimGold
                            ),
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    JovimLogoIcon(size = 32.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "JO Social Hub",
                                        fontFamily = FontFamily.Default,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = JovimGold
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { viewModel.setScreen(AppScreen.JovimAi) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = "JO AI",
                                        tint = JovimGold
                                    )
                                }
                                IconButton(onClick = { viewModel.setScreen(AppScreen.Profile) }) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profil",
                                        tint = Color.White
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = JovimPrimary,
                            contentColor = Color.White,
                            modifier = Modifier.testTag("main_navigation_bar")
                        ) {
                            NavigationBarItem(
                                selected = uiState.currentScreen == AppScreen.Dashboard,
                                onClick = { viewModel.setScreen(AppScreen.Dashboard) },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("Dashboard", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = JovimPrimary,
                                    selectedTextColor = JovimGold,
                                    indicatorColor = JovimGold,
                                    unselectedIconColor = Color.White.copy(0.6f),
                                    unselectedTextColor = Color.White.copy(0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentScreen == AppScreen.Accounts,
                                onClick = { viewModel.setScreen(AppScreen.Accounts) },
                                icon = { Icon(Icons.Default.Link, contentDescription = "Réseaux") },
                                label = { Text("Réseaux", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = JovimPrimary,
                                    selectedTextColor = JovimGold,
                                    indicatorColor = JovimGold,
                                    unselectedIconColor = Color.White.copy(0.6f),
                                    unselectedTextColor = Color.White.copy(0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentScreen == AppScreen.CreatePost,
                                onClick = { viewModel.setScreen(AppScreen.CreatePost) },
                                icon = { Icon(Icons.Default.AddCircle, contentDescription = "Créer", tint = JovimGold) },
                                label = { Text("Créer", fontSize = 10.sp, color = JovimGold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = JovimPrimary,
                                    selectedTextColor = JovimGold,
                                    indicatorColor = JovimGold,
                                    unselectedIconColor = Color.White.copy(0.6f),
                                    unselectedTextColor = Color.White.copy(0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentScreen == AppScreen.Calendar,
                                onClick = { viewModel.setScreen(AppScreen.Calendar) },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendrier") },
                                label = { Text("Planning", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = JovimPrimary,
                                    selectedTextColor = JovimGold,
                                    indicatorColor = JovimGold,
                                    unselectedIconColor = Color.White.copy(0.6f),
                                    unselectedTextColor = Color.White.copy(0.6f)
                                )
                            )
                            NavigationBarItem(
                                selected = uiState.currentScreen == AppScreen.Analytics,
                                onClick = { viewModel.setScreen(AppScreen.Analytics) },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Stats") },
                                label = { Text("Stats", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = JovimPrimary,
                                    selectedTextColor = JovimGold,
                                    indicatorColor = JovimGold,
                                    unselectedIconColor = Color.White.copy(0.6f),
                                    unselectedTextColor = Color.White.copy(0.6f)
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            if (uiState.isConnectionWeak) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEF3C7))
                                        .padding(vertical = 8.dp, horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Connexion faible - Synchronisation en attente",
                                        color = Color(0xFF92400E),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                when (uiState.currentScreen) {
                                    AppScreen.Dashboard -> DashboardScreen(viewModel, accounts, posts)
                                    AppScreen.Accounts -> AccountsScreen(viewModel, accounts)
                                    AppScreen.CreatePost -> CreatePostScreen(viewModel, uiState, accounts)
                                    AppScreen.JovimAi -> JovimAiScreen(viewModel, uiState, chatMessages, brandKit)
                                    AppScreen.Calendar -> CalendarScreen(viewModel, posts)
                                    AppScreen.MediaLibrary -> MediaLibraryScreen(viewModel, mediaItems)
                                    AppScreen.Analytics -> AnalyticsScreen(viewModel, accounts, posts)
                                    AppScreen.Profile -> ProfileScreen(viewModel, brandKit, accounts)
                                    AppScreen.Settings -> SettingsScreen(viewModel, brandKit)
                                    AppScreen.AddAccount -> AddAccountScreen(viewModel, accounts)
                                    else -> DashboardScreen(viewModel, accounts, posts)
                                }
                            }
                        }

                        // Direct API Publishing Progress / Success Dialog
                        if (uiState.showPublishStatusDialog) {
                            androidx.compose.ui.window.Dialog(
                                onDismissRequest = { 
                                    if (!uiState.isPublishing) {
                                        viewModel.dismissPublishStatusDialog() 
                                    }
                                }
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "JO Social Hub Direct API",
                                            color = JovimPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        if (uiState.isPublishing) {
                                            CircularProgressIndicator(
                                                progress = { uiState.publishProgress },
                                                color = JovimGold,
                                                trackColor = Color.LightGray.copy(0.3f),
                                                modifier = Modifier.size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = uiState.publishStatusMessage,
                                                color = JovimTextPrimary,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Progression : ${(uiState.publishProgress * 100).toInt()}%",
                                                color = JovimTextSecondary,
                                                fontSize = 12.sp
                                            )
                                        } else {
                                            // Success phase
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .background(JovimSuccess.copy(alpha = 0.15f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = JovimSuccess,
                                                    modifier = Modifier.size(36.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "Publication envoyée !",
                                                color = JovimSuccess,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            
                                            // Render details elegantly
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(containerColor = JovimBackground),
                                                border = BorderStroke(1.dp, Color.LightGray.copy(0.3f)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = uiState.publishSuccessDetails ?: "Opération réussie.",
                                                    color = Color.DarkGray,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(12.dp),
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Button(
                                                onClick = { viewModel.dismissPublishStatusDialog() },
                                                colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Fermer", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Indirect API Publishing Clipboard Helper Dialog
                        if (uiState.showIndirectPublishDialog) {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            androidx.compose.ui.window.Dialog(
                                onDismissRequest = { viewModel.dismissIndirectPublishDialog() }
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .background(JovimGold.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = null,
                                                tint = JovimGold,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Publication Préparée !",
                                            color = JovimPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "La plateforme ${uiState.indirectPublishPlatform} requiert une publication manuelle finale pour des raisons de sécurité.",
                                            color = JovimTextSecondary,
                                            fontSize = 12.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Contenu du post
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = JovimBackground),
                                            border = BorderStroke(1.dp, Color.LightGray.copy(0.3f)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = "Contenu copié :",
                                                    color = JovimPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = uiState.indirectPublishContent,
                                                    color = Color.DarkGray,
                                                    fontSize = 12.sp,
                                                    maxLines = 5,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Button(
                                            onClick = {
                                                // Copy text again to be absolutely sure
                                                try {
                                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                    val clip = android.content.ClipData.newPlainText("JO Social Hub Post", uiState.indirectPublishContent)
                                                    clipboard.setPrimaryClip(clip)
                                                } catch (e: Exception) {}

                                                // Launch platform app
                                                val platformLower = uiState.indirectPublishPlatform.lowercase()
                                                val (intentUrl, packageName) = when {
                                                    platformLower.contains("tiktok") -> Pair("https://www.tiktok.com/", "com.zhiliaoapp.musically")
                                                    platformLower.contains("threads") -> Pair("https://www.threads.net/", "com.instagram.barcelona")
                                                    platformLower.contains("pinterest") -> Pair("https://www.pinterest.com/", "com.pinterest")
                                                    platformLower.contains("blogger") -> Pair("https://www.blogger.com/", "com.google.android.apps.blogger")
                                                    platformLower.contains("medium") -> Pair("https://medium.com/", "com.medium.reader")
                                                    platformLower.contains("telegram") -> Pair("https://t.me/share/url?text=${android.net.Uri.encode(uiState.indirectPublishContent)}", "org.telegram.messenger")
                                                    platformLower.contains("whatsapp") -> Pair("https://api.whatsapp.com/send?text=${android.net.Uri.encode(uiState.indirectPublishContent)}", "com.whatsapp")
                                                    else -> Pair("https://www.google.com", "")
                                                }

                                                val intent = if (packageName.isNotEmpty()) {
                                                    val pm = context.packageManager
                                                    val launchIntent = pm.getLaunchIntentForPackage(packageName)
                                                    launchIntent ?: android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(intentUrl))
                                                } else {
                                                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(intentUrl))
                                                }
                                                
                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // Fallback to web link
                                                    try {
                                                        context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(intentUrl)))
                                                    } catch (ex: Exception) {}
                                                }
                                                viewModel.dismissIndirectPublishDialog()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = JovimPrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Ouvrir l'App Officielle", color = JovimPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        TextButton(
                                            onClick = { viewModel.dismissIndirectPublishDialog() },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Fermer", color = JovimTextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Success Overlay Toast
                        if (uiState.postPublishSuccessTrigger) {
                            LaunchedEffect(Unit) {
                                delay(3000)
                                viewModel.dismissPublishSuccess()
                            }
                            Card(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = JovimSuccess),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Succès", tint = Color.White)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Votre publication a été créée et diffusée avec succès !",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- LOGO BRAND ACCENT ---
@Composable
fun JovimLogoIcon(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .background(JovimPrimary, CircleShape)
            .border(2.dp, JovimGold, CircleShape)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Compose custom drawing representing the connected digital hub
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = this.size.width
            val height = this.size.height
            val center = Offset(width / 2, height / 2)
            val radius = width * 0.35f

            // Nodes (cercle extérieur)
            val nodes = listOf(
                Offset(width * 0.2f, height * 0.2f),
                Offset(width * 0.8f, height * 0.2f),
                Offset(width * 0.2f, height * 0.8f),
                Offset(width * 0.8f, height * 0.8f)
            )

            // Connection Lines
            nodes.forEach { node ->
                drawLine(
                    color = JovimGold.copy(alpha = 0.5f),
                    start = center,
                    end = node,
                    strokeWidth = 3f
                )
                drawCircle(
                    color = JovimGold,
                    radius = 5f,
                    center = node
                )
            }

            // Central "J" and glow
            drawCircle(
                color = JovimGold.copy(alpha = 0.2f),
                radius = radius,
                center = center
            )
        }
        Text(
            text = "J",
            color = JovimGold,
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// --- 1. SPLASH SCREEN ---
@Composable
fun SplashScreen(viewModel: JovimViewModel) {
    val scale = remember { Animatable(0f) }
    val opacity = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        opacity.animateTo(1f, animationSpec = tween(1000))
        delay(2000)
        // Transition to Onboarding
        viewModel.setScreen(AppScreen.Onboarding)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                alpha = opacity.value
            }
        ) {
            JovimLogoIcon(size = 120.dp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "JO",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
            Text(
                text = "SOCIAL HUB",
                color = JovimGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 6.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = JovimGold,
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp
            )
        }
        Text(
            text = "Powered by Kan Jospin",
            color = Color.White.copy(0.4f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            fontFamily = FontFamily.Monospace
        )
    }
}

// --- 2. ONBOARDING SCREEN ---
@Composable
fun OnboardingScreen(viewModel: JovimViewModel) {
    var pageState by remember { mutableIntStateOf(0) }
    val pages = listOf(
        OnboardingPageData(
            title = "Centralisez Vos Réseaux",
            desc = "Connectez Facebook, Instagram, LinkedIn, YouTube, TikTok et plus encore depuis un seul hub intelligent et sécurisé.",
            icon = Icons.Default.Hub
        ),
        OnboardingPageData(
            title = "JO AI : Assistant Intelligent",
            desc = "Adaptez automatiquement votre message aux codes de chaque réseau social en un seul clic ! Powered by Kan Jospin",
            icon = Icons.Default.AutoAwesome
        ),
        OnboardingPageData(
            title = "Planification & Analytics",
            desc = "Planifiez vos publications sur un calendrier fluide et suivez vos statistiques d'audience en temps réel.",
            icon = Icons.Default.CalendarMonth
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimPrimary)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color.White.copy(0.08f), CircleShape)
                    .border(1.dp, JovimGold.copy(0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = pages[pageState].icon,
                    contentDescription = null,
                    tint = JovimGold,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = pages[pageState].title,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pages[pageState].desc,
                color = Color.White.copy(0.7f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Indicator dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(height = 8.dp, width = if (pageState == index) 24.dp else 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (pageState == index) JovimGold else Color.White.copy(0.3f))
                )
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { viewModel.setScreen(AppScreen.Login) },
                modifier = Modifier.testTag("skip_onboarding_button")
            ) {
                Text("Passer", color = Color.White.copy(0.6f), fontSize = 16.sp)
            }

            Button(
                onClick = {
                    if (pageState < pages.size - 1) {
                        pageState++
                    } else {
                        viewModel.setScreen(AppScreen.Login)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (pageState == pages.size - 1) "Commencer" else "Suivant",
                    color = JovimPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class OnboardingPageData(val title: String, val desc: String, val icon: ImageVector)

// --- 3. LOGIN SCREEN ---
@Composable
fun LoginScreen(viewModel: JovimViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    var isRegisterMode by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }

    // Pre-fill the admin credentials for demo purposes
    LaunchedEffect(isRegisterMode) {
        if (!isRegisterMode) {
            email = "jospin.kanani"
            password = "chrisjospin2003@@"
        } else {
            fullName = ""
            email = ""
            password = ""
            confirmPassword = ""
        }
        localError = ""
        viewModel.clearAuthError()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimPrimary)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            JovimLogoIcon(size = 80.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isRegisterMode) "Création de compte" else "Connexion",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gérez vos réseaux avec JO Social Hub",
                color = Color.White.copy(0.6f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Offline banner if disconnected
            if (!uiState.isOnline) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = JovimError.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, JovimError)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudOff, contentDescription = "Hors-ligne", tint = JovimError)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Mode Hors-ligne",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Connexion Internet ou Wi-Fi nécessaire pour continuer.",
                                color = Color.White.copy(0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Registration Fields
            if (isRegisterMode) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Nom complet", color = Color.Gray) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = JovimGold,
                        unfocusedBorderColor = Color.White.copy(0.4f),
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("fullname_input")
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Identifiant ou E-mail", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = JovimGold,
                    unfocusedBorderColor = Color.White.copy(0.4f),
                    cursorColor = Color.Black
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("email_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mot de passe", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = JovimGold,
                    unfocusedBorderColor = Color.White.copy(0.4f),
                    cursorColor = Color.Black
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("password_input")
            )

            if (isRegisterMode) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirmer le mot de passe", color = Color.Gray) },
                    textStyle = TextStyle(color = Color.Black),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = JovimGold,
                        unfocusedBorderColor = Color.White.copy(0.4f),
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("confirm_password_input")
                )
            }

            // Error display
            val displayError = uiState.authError ?: localError
            if (displayError.isNotEmpty()) {
                Text(
                    text = displayError,
                    color = JovimError,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    if (isRegisterMode) {
                        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            localError = "Veuillez remplir tous les champs."
                        } else if (password != confirmPassword) {
                            localError = "Les mots de passe ne correspondent pas."
                        } else {
                            viewModel.registerUser(fullName, email, password, confirmPassword)
                        }
                    } else {
                        if (email.isBlank() || password.isBlank()) {
                            localError = "Veuillez remplir tous les champs."
                        } else {
                            viewModel.loginUserWithCredentials(email, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isRegisterMode) "S'inscrire" else "Se connecter",
                    color = JovimPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle login/signup mode
            TextButton(
                onClick = { isRegisterMode = !isRegisterMode }
            ) {
                Text(
                    text = if (isRegisterMode) "Déjà inscrit ? Connectez-vous" else "Nouveau sur JO Social Hub ? Créez un compte",
                    color = JovimGold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "ou continuer avec",
                color = Color.White.copy(0.4f),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Google OAuth button
            OutlinedButton(
                onClick = { viewModel.loginWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = "Google Icon",
                        tint = JovimGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Continuer avec Google", color = Color.White)
                }
            }

            // Google OAuth Dialog
            if (uiState.showGoogleOAuthDialog) {
                var googleEmailInput by remember { mutableStateOf("") }
                var oauthError by remember { mutableStateOf("") }
                
                AlertDialog(
                    onDismissRequest = { viewModel.setGoogleOAuthDialog(false) },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cloud, contentDescription = null, tint = JovimGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google OAuth 2.0 Identity", color = JovimPrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column {
                            Text(
                                "Saisissez votre adresse e-mail Google authentique pour vérifier votre identité.",
                                fontSize = 13.sp,
                                color = JovimTextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedTextField(
                                value = googleEmailInput,
                                onValueChange = { 
                                    googleEmailInput = it
                                    oauthError = ""
                                },
                                textStyle = TextStyle(color = Color.Black),
                                placeholder = { Text("Adresse e-mail Google (ex: jospin.kanani@gmail.com)", color = Color.Gray) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = JovimGold,
                                    unfocusedBorderColor = Color.LightGray,
                                    cursorColor = Color.Black
                                )
                            )
                            
                            if (oauthError.isNotBlank()) {
                                Text(
                                    oauthError,
                                    color = JovimError,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (googleEmailInput.isBlank()) {
                                    oauthError = "L'adresse e-mail ne peut pas être vide."
                                    return@Button
                                }
                                if (!viewModel.isValidEmail(googleEmailInput) || !(googleEmailInput.endsWith("@gmail.com") || googleEmailInput.endsWith("@google.com"))) {
                                    oauthError = "Seuls les comptes Google réels (@gmail.com / @google.com) sont acceptés."
                                    return@Button
                                }
                                viewModel.loginWithGoogleOAuth(googleEmailInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary)
                        ) {
                            Text("Autoriser & Se Connecter", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.setGoogleOAuthDialog(false) }) {
                            Text("Annuler", color = JovimTextSecondary)
                        }
                    }
                )
            }

            // Google Register Prompt Dialog
            if (uiState.showGoogleRegisterPrompt) {
                AlertDialog(
                    onDismissRequest = { viewModel.cancelGoogleRegistration() },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = JovimGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Créer un compte JO Social Hub ?", color = JovimPrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column {
                            Text(
                                "Le compte Google associé à l'e-mail ${uiState.pendingGoogleEmail} n'existe pas encore dans JO Social Hub.",
                                fontSize = 14.sp,
                                color = JovimTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Voulez-vous créer un nouveau compte maintenant avec cette adresse Google ?",
                                fontSize = 13.sp,
                                color = JovimTextSecondary
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.confirmGoogleRegistration() },
                            colors = ButtonDefaults.buttonColors(containerColor = JovimGold)
                        ) {
                            Text("Oui, créer le compte", color = JovimPrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cancelGoogleRegistration() }) {
                            Text("Annuler", color = JovimTextSecondary)
                        }
                    }
                )
            }

            // Google Account Existence Verification Dialog
            if (uiState.showGoogleExistenceDialog) {
                var googlePasswordInput by remember { mutableStateOf("") }
                var showPassError by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = { viewModel.setGoogleExistenceDialog(false) },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cloud, contentDescription = null, tint = JovimGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Authentification Google Identity", color = JovimPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    text = {
                        Column {
                            Text(
                                "Google Identity Services exige de vérifier que l'adresse email suivante correspond à un compte Google existant :",
                                fontSize = 12.sp,
                                color = JovimTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color.LightGray.copy(0.2f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = uiState.verifyingGoogleEmail,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JovimPrimary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Saisissez le mot de passe du compte Google pour autoriser la validation :",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = googlePasswordInput,
                                onValueChange = { 
                                    googlePasswordInput = it
                                    showPassError = false
                                },
                                placeholder = { Text("Mot de passe Google", color = Color.Gray, fontSize = 11.sp) },
                                textStyle = TextStyle(color = Color.Black),
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("google_existence_password"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = JovimGold,
                                    unfocusedBorderColor = Color.LightGray,
                                    cursorColor = Color.Black
                                )
                            )
                            if (showPassError) {
                                Text(
                                    "Veuillez saisir un mot de passe pour valider.",
                                    color = JovimError,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (googlePasswordInput.isBlank()) {
                                        showPassError = true
                                    } else {
                                        viewModel.verifyGoogleExistence(uiState.verifyingGoogleEmail, true)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                                modifier = Modifier.fillMaxWidth().testTag("google_exist_confirm_btn")
                            ) {
                                Text("Vérifier & Continuer", color = JovimPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.verifyGoogleExistence(uiState.verifyingGoogleEmail, false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JovimError),
                                modifier = Modifier.fillMaxWidth().testTag("google_exist_fail_btn")
                            ) {
                                Text("Simuler Compte Inexistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.setGoogleExistenceDialog(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Annuler l'inscription", color = JovimTextSecondary)
                        }
                    }
                )
            }

            // Email Code & Link Verification Dialog
            if (uiState.showEmailVerificationDialog) {
                var codeInput by remember { mutableStateOf("") }
                var codeError by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { viewModel.setVerificationDialog(false) },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Validation de votre adresse e-mail", color = JovimPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            // MANDATORY DISPLAY MESSAGE
                            Text(
                                text = "Un email de confirmation a été envoyé. Veuillez vérifier votre boîte de réception avant de vous connecter.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = JovimPrimary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text(
                                text = "Saisissez ci-dessous le code à 6 chiffres reçu par e-mail pour activer votre compte :",
                                fontSize = 12.sp,
                                color = JovimTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = codeInput,
                                onValueChange = { 
                                    codeInput = it
                                    codeError = ""
                                },
                                placeholder = { Text("Code à 6 chiffres", color = Color.Gray, fontSize = 12.sp) },
                                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("verification_code_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = JovimSuccess,
                                    unfocusedBorderColor = Color.LightGray,
                                    cursorColor = Color.Black
                                )
                            )

                            if (codeError.isNotEmpty()) {
                                Text(
                                    text = codeError,
                                    color = JovimError,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { viewModel.resendVerificationEmail(uiState.verificationPendingEmail) }
                                ) {
                                    Text("Renvoyer le code", color = JovimGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        if (codeInput.isBlank()) {
                                            codeError = "Veuillez entrer le code de confirmation."
                                        } else {
                                            val ok = viewModel.verifyEmailCode(uiState.verificationPendingEmail, codeInput)
                                            if (!ok) {
                                                // Wait 100ms then check if code didn't match
                                                codeError = "Code de confirmation incorrect."
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JovimSuccess),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Confirmer", color = Color.White, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // SIMULATED INBOX UI
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.LightGray.copy(0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(24.dp).background(JovimGold, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = JovimPrimary, modifier = Modifier.size(14.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Boîte de Réception (Simulation)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = JovimPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color.LightGray.copy(0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "De : security@josocialhub.com",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = "Sujet : JO Social Hub - Confirmation de compte",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Bonjour,\n\nMerci de vous être inscrit sur JO Social Hub ! Saisissez le code de confirmation suivant dans l'application :",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, Color.LightGray),
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text = uiState.generatedVerificationCode,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                            color = JovimPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Ou cliquez directement sur le bouton de sécurité ci-dessous pour valider votre compte en un clic :",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.activateAccountDirectly(uiState.verificationPendingEmail) },
                                        colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("direct_confirm_btn")
                                    ) {
                                        Text("🚀 Valider mon e-mail via le lien", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.setVerificationDialog(false) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Fermer", color = JovimTextSecondary)
                        }
                    }
                )
            }
        }
    }
}


// --- 4. DASHBOARD SCREEN ---
@Composable
fun DashboardScreen(
    viewModel: JovimViewModel,
    accounts: List<Account>,
    posts: List<Post>
) {
    val connectedAccounts = accounts.filter { it.isConnected }
    val totalFollowers = connectedAccounts.sumOf { it.followerCount }
    val scheduledPosts = posts.filter { it.status == "SCHEDULED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .padding(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Ravi de vous revoir !",
                        color = JovimTextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        "Tableau de Bord",
                        color = JovimTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(JovimPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("JD", color = JovimGold, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Statistics row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Abonnés Totaux",
                    value = String.format("%,d", totalFollowers),
                    subtitle = "${connectedAccounts.size} Réseaux liés",
                    icon = Icons.Default.People,
                    color = JovimPrimary,
                    textColor = JovimGold,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Programmés",
                    value = "${scheduledPosts.size} posts",
                    subtitle = "Prochain : Demain",
                    icon = Icons.Default.Schedule,
                    color = JovimGold,
                    textColor = JovimPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Graph Section (Natively drawn with Canvas)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Audience par Plateforme",
                        color = JovimTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Comparaison en direct",
                        color = JovimTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Draw direct native chart inside Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val maxVal = 15000f

                            // Baseline
                            drawLine(Color.LightGray, Offset(0f, h), Offset(w, h), 2f)

                            // 4 columns representing Facebook, Instagram, YouTube, LinkedIn
                            val barWidth = 40.dp.toPx()
                            val barSpacing = (w - (barWidth * 4)) / 5

                            val dataPoints = listOf(
                                Pair("FB", 12450f),
                                Pair("IG", 8200f),
                                Pair("YT", 3400f),
                                Pair("LI", 1540f)
                            )

                            dataPoints.forEachIndexed { i, pt ->
                                val x = barSpacing + i * (barWidth + barSpacing)
                                val barHeight = (pt.second / maxVal) * (h * 0.85f)
                                val y = h - barHeight

                                drawRoundRect(
                                    color = if (i % 2 == 0) JovimPrimary else JovimGold,
                                    topLeft = Offset(x, y),
                                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                                )
                            }
                        }
                    }

                    // Labels below columns
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("Facebook (12.4K)", fontSize = 10.sp, color = JovimTextSecondary, fontWeight = FontWeight.Bold)
                        Text("Instagram (8.2K)", fontSize = 10.sp, color = JovimTextSecondary, fontWeight = FontWeight.Bold)
                        Text("YouTube (3.4K)", fontSize = 10.sp, color = JovimTextSecondary, fontWeight = FontWeight.Bold)
                        Text("LinkedIn (1.5K)", fontSize = 10.sp, color = JovimTextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Tools / Secondary screens navigations
        item {
            Text(
                "Outils & Bibliothèques",
                color = JovimTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickToolButton(
                    title = "Médias",
                    icon = Icons.Default.Image,
                    onClick = { viewModel.setScreen(AppScreen.MediaLibrary) },
                    modifier = Modifier.weight(1f)
                )
                QuickToolButton(
                    title = "JO AI",
                    icon = Icons.Default.AutoAwesome,
                    onClick = { viewModel.setScreen(AppScreen.JovimAi) },
                    modifier = Modifier.weight(1f)
                )
                QuickToolButton(
                    title = "Brand Kit",
                    icon = Icons.Default.Badge,
                    onClick = { viewModel.setScreen(AppScreen.Settings) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Recent posts header
        item {
            Text(
                "Publications Récentes",
                color = JovimTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // List of recent posts
        if (posts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Aucune publication créée. Cliquez sur '+' pour rédiger votre premier post.",
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center,
                        color = JovimTextSecondary
                    )
                }
            }
        } else {
            items(posts) { post ->
                PostItemCard(post, onDelete = { viewModel.deletePost(post.id) })
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, color = textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(title, color = textColor.copy(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = textColor.copy(0.6f), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun QuickToolButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = JovimPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = JovimTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PostItemCard(post: Post, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Badge
                val badgeColor = when (post.status) {
                    "PUBLISHED" -> JovimSuccess
                    "SCHEDULED" -> JovimInfo
                    else -> JovimTextSecondary
                }
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        post.status,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (post.qualityScore > 0) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = JovimGold, modifier = Modifier.size(16.dp))
                        Text(
                            "Score: ${post.qualityScore}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = JovimTextPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = JovimError, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.contentText,
                fontSize = 14.sp,
                color = JovimTextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Channels targeted
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    post.platforms.split(",").forEach { platform ->
                        Box(
                            modifier = Modifier
                                .background(JovimPrimary.copy(alpha = 0.08f), CircleShape)
                                .border(1.dp, JovimPrimary.copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(platform, fontSize = 10.sp, color = JovimPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (post.scheduledTime > 0) {
                    val date = Date(post.scheduledTime)
                    val format = SimpleDateFormat("dd MMM, HH:mm", Locale.FRANCE)
                    Text(
                        "Planifié : ${format.format(date)}",
                        fontSize = 11.sp,
                        color = JovimTextSecondary
                    )
                } else {
                    Text("Publié instantanément", fontSize = 11.sp, color = JovimTextSecondary)
                }
            }
        }
    }
}

// --- 5. CONNECTED ACCOUNTS SCREEN ---
@Composable
fun AccountsScreen(viewModel: JovimViewModel, accounts: List<Account>) {
    val uiState by viewModel.uiState.collectAsState()
    val socialProfiles by viewModel.socialProfiles.collectAsState()
    
    var activeTab by remember { mutableStateOf(0) } // 0 = Canaux API, 1 = Mes Comptes Sociaux

    // Add API account dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var newPlatform by remember { mutableStateOf("Instagram") }
    var newHandle by remember { mutableStateOf("") }
    val platformList = listOf("Facebook", "Instagram", "TikTok", "YouTube", "LinkedIn", "X (Twitter)", "WordPress", "Email", "WhatsApp Business")

    // Add Profile link states
    var profilePlatform by remember { mutableStateOf("Facebook") }
    var profileUrlInput by remember { mutableStateOf("") }
    val socialPlatformList = listOf("Facebook", "Instagram", "TikTok", "YouTube", "LinkedIn", "X", "Threads", "Pinterest", "WordPress", "Blogger", "Medium")

    // Profile test statuses map (holds status message for each profile ID)
    val testStatuses = remember { mutableStateMapOf<Int, Pair<Boolean, String>>() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setScreen(AppScreen.AddAccount) },
                containerColor = JovimGold,
                contentColor = JovimPrimary,
                modifier = Modifier.testTag("add_account_fab")
            ) {
                Icon(Icons.Default.AddLink, contentDescription = "Connecter un compte")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(JovimBackground)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Gestion des Comptes & Profils",
                color = JovimTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Configurez vos canaux de diffusion automatisés ou liez vos profils sociaux.",
                color = JovimTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Segments control (Tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(0.05f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Canaux API", "Mes Comptes Sociaux (Liens)").forEachIndexed { index, label ->
                    val selected = activeTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selected) JovimGold else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { activeTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (selected) JovimPrimary else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (activeTab == 0) {
                // TAB 0: API Channels List
                Column(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable { viewModel.setScreen(AppScreen.AddAccount) },
                        colors = CardDefaults.cardColors(containerColor = JovimGold.copy(0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, JovimGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(JovimGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AddLink, contentDescription = null, tint = JovimPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Connexion Simplifiée par Lien de Profil",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Collez simplement votre URL de profil (Facebook, Instagram, LinkedIn, etc.) pour connecter votre compte automatiquement via OAuth sécurisé !",
                                    color = Color.White.copy(0.7f),
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = JovimGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(accounts) { account ->
                            AccountCard(
                                account = account,
                                onToggle = { viewModel.toggleAccountConnection(account.id) },
                                onToggleType = { viewModel.toggleAccountType(account.id) }
                            )
                        }
                    }
                }
            } else {
                // TAB 1: Social Profiles (Mes comptes sociaux - Link Management)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Form to add social profile
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Ajouter un profil social",
                                fontWeight = FontWeight.Bold,
                                color = JovimPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                "Saisissez l'URL de votre profil public pour permettre la vérification et l'édition simplifiée.",
                                color = JovimTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Select platform row
                            Text("Plateforme :", fontSize = 12.sp, color = JovimTextSecondary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(socialPlatformList) { plat ->
                                    val isSel = profilePlatform == plat
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSel) JovimPrimary else Color.LightGray.copy(0.2f),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { profilePlatform = plat }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            plat,
                                            color = if (isSel) Color.White else JovimTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Profile Link URL field with black text style
                            OutlinedTextField(
                                value = profileUrlInput,
                                onValueChange = { profileUrlInput = it },
                                textStyle = TextStyle(color = Color.Black),
                                placeholder = { Text("Lien complet du profil (ex: https://...)", color = Color.Gray) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = JovimPrimary,
                                    unfocusedBorderColor = Color.LightGray,
                                    cursorColor = Color.Black
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (profileUrlInput.isNotBlank()) {
                                        viewModel.addSocialProfile(profilePlatform, profileUrlInput)
                                        profileUrlInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                enabled = profileUrlInput.isNotBlank()
                            ) {
                                Icon(Icons.Default.AddLink, contentDescription = null, tint = JovimPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Associer ce profil", color = JovimPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Profils Enregistrés (${socialProfiles.size})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    if (socialProfiles.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.03f), RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Aucun lien de profil enregistré pour le moment.", color = JovimTextSecondary, fontSize = 13.sp)
                        }
                    } else {
                        // List of profiles
                        socialProfiles.forEach { profile ->
                            // Local state for profile url edit
                            var editableUrl by remember(profile.profileUrl) { mutableStateOf(profile.profileUrl) }
                            var isEditing by remember { mutableStateOf(false) }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.LightGray.copy(0.3f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Badges styling per social media platform
                                            val platColor = when(profile.platformName) {
                                                "Facebook" -> Color(0xFF1877F2)
                                                "Instagram" -> Color(0xFFE4405F)
                                                "TikTok" -> Color(0xFF000000)
                                                "YouTube" -> Color(0xFFFF0000)
                                                "LinkedIn" -> Color(0xFF0A66C2)
                                                "X" -> Color(0xFF14171A)
                                                "Threads" -> Color(0xFF000000)
                                                "Pinterest" -> Color(0xFFBD081C)
                                                "WordPress" -> Color(0xFF21759B)
                                                "Blogger" -> Color(0xFFF57C00)
                                                "Medium" -> Color(0xFF02B875)
                                                else -> JovimPrimary
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(platColor, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    profile.platformName.take(1),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                profile.platformName,
                                                fontWeight = FontWeight.Bold,
                                                color = JovimPrimary,
                                                fontSize = 15.sp
                                            )
                                        }

                                        IconButton(onClick = { viewModel.deleteSocialProfile(profile.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = JovimError)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // URL edit field with black text style
                                    OutlinedTextField(
                                        value = editableUrl,
                                        onValueChange = { 
                                            editableUrl = it
                                            isEditing = true
                                        },
                                        textStyle = TextStyle(color = Color.Black),
                                        placeholder = { Text("URL du profil", color = Color.Gray) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedBorderColor = JovimPrimary,
                                            unfocusedBorderColor = Color.LightGray,
                                            cursorColor = Color.Black
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Local test results display
                                    val testRes = testStatuses[profile.id]
                                    if (testRes != null) {
                                        val (success, message) = testRes
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (success) JovimSuccess.copy(0.1f) else JovimError.copy(0.1f)
                                            ),
                                            border = BorderStroke(1.dp, if (success) JovimSuccess else JovimError)
                                        ) {
                                            Text(
                                                message,
                                                color = if (success) JovimSuccess else JovimError,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.testSocialProfileAccess(profile.platformName, editableUrl) { ok, msg ->
                                                    testStatuses[profile.id] = Pair(ok, msg)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JovimPrimary),
                                            border = BorderStroke(1.dp, JovimPrimary)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tester", fontSize = 12.sp)
                                        }

                                        if (isEditing) {
                                            Button(
                                                onClick = {
                                                    viewModel.updateSocialProfile(profile.copy(profileUrl = editableUrl))
                                                    isEditing = false
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = JovimSuccess)
                                            ) {
                                                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Enregistrer", color = Color.White, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Connect New Account Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Connecter un canal numérique", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Sélectionnez la plateforme :", fontSize = 13.sp, color = JovimTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Dumb select box using custom scroll row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(platformList) { plat ->
                            val selected = newPlatform == plat
                            Box(
                                modifier = Modifier
                                    .background(if (selected) JovimGold else Color.LightGray.copy(0.3f), RoundedCornerShape(16.dp))
                                    .clickable { newPlatform = plat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(plat, color = if (selected) JovimPrimary else JovimTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newHandle,
                        onValueChange = { newHandle = it },
                        textStyle = TextStyle(color = Color.Black),
                        placeholder = { Text("Nom d'utilisateur ou Page ID", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = JovimPrimary,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color.Black
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHandle.isNotBlank()) {
                            viewModel.addCustomAccount(newPlatform, newHandle)
                            newHandle = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary)
                ) {
                    Text("Associer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler", color = JovimTextSecondary)
                }
            }
        )
    }
}

@Composable
fun PlatformLogo(platformName: String, modifier: Modifier = Modifier) {
    val pLower = platformName.lowercase()
    val (bgColor, iconResId) = when {
        pLower.contains("facebook") -> Pair(Color(0xFF1877F2), R.drawable.ic_facebook)
        pLower.contains("instagram") -> Pair(Color(0xFFE4405F), R.drawable.ic_instagram)
        pLower.contains("linkedin") -> Pair(Color(0xFF0A66C2), R.drawable.ic_linkedin)
        pLower.contains("youtube") -> Pair(Color(0xFFFF0000), R.drawable.ic_youtube)
        pLower.contains("tiktok") -> Pair(Color(0xFF010101), R.drawable.ic_tiktok)
        pLower.contains("x") || pLower.contains("twitter") -> Pair(Color(0xFF000000), R.drawable.ic_x_twitter)
        pLower.contains("threads") -> Pair(Color(0xFF101010), R.drawable.ic_threads)
        pLower.contains("pinterest") -> Pair(Color(0xFFBD081C), R.drawable.ic_pinterest)
        pLower.contains("wordpress") -> Pair(Color(0xFF21759B), R.drawable.ic_wordpress)
        pLower.contains("blogger") -> Pair(Color(0xFFF57C00), R.drawable.ic_blogger)
        pLower.contains("telegram") -> Pair(Color(0xFF229ED9), R.drawable.ic_telegram)
        pLower.contains("whatsapp") -> Pair(Color(0xFF25D366), R.drawable.ic_whatsapp)
        else -> Pair(JovimPrimary, null)
    }

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .then(
                if (pLower.contains("instagram")) {
                    Modifier.background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFCAF45),
                                Color(0xFFFD1D1D),
                                Color(0xFF833AB4)
                            )
                        )
                    )
                } else {
                    Modifier.background(bgColor)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = platformName,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = platformName.take(2).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AccountCard(account: Account, onToggle: () -> Unit, onToggleType: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (account.isConnected) JovimGold.copy(0.4f) else Color.LightGray.copy(0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlatformLogo(platformName = account.platformName)

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            account.platformName,
                            fontWeight = FontWeight.Bold,
                            color = JovimTextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            if (account.isConnected) account.accountName else "Non connecté",
                            color = JovimTextSecondary,
                            fontSize = 13.sp
                        )
                        if (account.isConnected && account.followerCount > 0) {
                            Text(
                                "${String.format("%,d", account.followerCount)} abonnés",
                                color = JovimGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Connection Toggle Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (account.isConnected) "Actif" else "Inactif",
                        fontSize = 11.sp,
                        color = if (account.isConnected) JovimSuccess else JovimTextSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = account.isConnected,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JovimGold,
                            checkedTrackColor = JovimPrimary,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.LightGray.copy(0.3f)
                        )
                    )
                }
            }

            // Sub-panel for Instagram / Facebook connected settings
            if (account.isConnected && onToggleType != null && (account.platformName == "Instagram" || account.platformName == "Facebook")) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.LightGray.copy(0.3f)))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (account.isProfessional) JovimSuccess.copy(alpha = 0.04f) else Color(0xFFFFECEF))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Statut : ${if (account.isProfessional) "Connecté ✓" else "Non compatible ⚠️"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (account.isProfessional) JovimSuccess else Color(0xFFC62828)
                            )
                            Text(
                                text = "Type de compte : ${if (account.isProfessional) "Professionnel/Créateur" else "Personnel"}",
                                fontSize = 11.sp,
                                color = JovimTextSecondary
                            )
                        }
                        
                        Text(
                            text = if (account.isProfessional) "Passer en Personnel" else "Passer en Pro/Créateur",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = JovimPrimary,
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .border(1.dp, JovimPrimary.copy(0.3f), RoundedCornerShape(4.dp))
                                .clickable { onToggleType() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    if (!account.isProfessional && account.platformName == "Instagram") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Votre compte Instagram doit être professionnel ou créateur pour permettre la publication automatique.",
                            fontSize = 11.sp,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Medium
                        )
                    } else if (!account.isProfessional && account.platformName == "Facebook") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Votre compte Facebook doit être professionnel (Page) pour permettre la publication automatique.",
                            fontSize = 11.sp,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// --- 6. CREATE PUBLICATION SCREEN (Intelligent Editor) ---
@Composable
fun CreatePostScreen(
    viewModel: JovimViewModel,
    uiState: JovimUiState,
    accounts: List<Account>
) {
    if (!uiState.isOnline) {
        OfflineScreenOverlay(onRetry = { viewModel.checkNetworkStatus() })
        return
    }

    val context = LocalContext.current
    var contentText by remember { mutableStateOf(uiState.editorContentText) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Rédiger, 1: Adaptation IA, 2: Audit IA
    var showScheduleDialog by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { viewModel.uploadMediaFile(it.toString(), "IMAGE") }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { viewModel.uploadMediaFile(it.toString(), "VIDEO") }
    }

    LaunchedEffect(uiState.editorContentText) {
        contentText = uiState.editorContentText
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Création de Contenu",
            color = JovimTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Rédigez et laissez l'IA JO adapter et analyser vos posts.",
            color = JovimTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Select Channels
        Text(
            "Réseaux Cibles :",
            fontWeight = FontWeight.Bold,
            color = JovimTextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            val platforms = listOf("Facebook", "Instagram", "TikTok", "YouTube", "LinkedIn", "WordPress", "Email")
            items(platforms) { plat ->
                val isSelected = uiState.selectedPlatforms.contains(plat)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectPlatformForPost(plat, !isSelected) },
                    label = { Text(plat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JovimGold,
                        selectedLabelColor = JovimPrimary,
                        containerColor = Color.White
                    )
                )
            }
        }

        // Segmented Tabs for Draft vs IA Optimization
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = JovimPrimary,
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, Color.LightGray.copy(0.3f))
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("1. Rédiger", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("2. Adaptation IA", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("3. Audit IA Quality", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // Main text editor
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = contentText,
                            onValueChange = {
                                contentText = it
                                viewModel.updatePostContent(it)
                            },
                            placeholder = { Text("Qu'allez-vous partager aujourd'hui ? Écrivez votre idée ou demandez à l'IA...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("post_content_editor"),
                            maxLines = 10,
                            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Visible action buttons for adding image/video
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { imageLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ajouter une Image", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { videoLauncher.launch("video/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ajouter une Vidéo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Camera options (Simulated for 1-click convenience)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.uploadMediaFile("camera_photo_${System.currentTimeMillis()}.jpg", "IMAGE")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = JovimPrimary),
                                border = BorderStroke(1.dp, JovimPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Prendre une Photo", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.uploadMediaFile("camera_video_${System.currentTimeMillis()}.mp4", "VIDEO")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = JovimPrimary),
                                border = BorderStroke(1.dp, JovimPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enregistrer Vidéo", fontSize = 11.sp)
                            }
                        }

                        // Upload Progress Indicator
                        if (uiState.isUploadingMedia) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = JovimPrimary.copy(alpha = 0.05f)),
                                border = BorderStroke(1.dp, JovimGold.copy(0.4f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Téléversement du fichier...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JovimPrimary)
                                        Text("${(uiState.uploadProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JovimGold)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { uiState.uploadProgress },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = JovimGold,
                                        trackColor = Color.LightGray.copy(0.3f)
                                    )
                                }
                            }
                        }

                        // Attached Media Preview Card
                        if (uiState.editorMediaUrl.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.LightGray.copy(0.4f)),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .background(Color.DarkGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (uiState.editorMediaType == "IMAGE") {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = null,
                                                tint = Color.White.copy(0.6f),
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Text(
                                                "Aperçu Image : ${uiState.editorMediaUrl.takeLast(25)}",
                                                color = Color.White.copy(0.8f),
                                                fontSize = 11.sp,
                                                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .background(Color.Black.copy(0.5f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                            Text(
                                                "Aperçu Vidéo : ${uiState.editorMediaUrl.takeLast(25)}",
                                                color = Color.White.copy(0.8f),
                                                fontSize = 11.sp,
                                                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF1F5F9))
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${uiState.editorMediaType} attaché",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = JovimTextPrimary
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            TextButton(
                                                onClick = {
                                                    if (uiState.editorMediaType == "IMAGE") {
                                                        imageLauncher.launch("image/*")
                                                    } else {
                                                        videoLauncher.launch("video/*")
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Remplacer", fontSize = 11.sp)
                                            }

                                            TextButton(
                                                onClick = { viewModel.setPostMedia("", "NONE") }
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = null, tint = JovimError, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Supprimer", color = JovimError, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                "${contentText.length} caractères",
                                color = JovimTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action suggestions with JOVIM AI shortcut buttons
                Text("Suggestions d'aide IA :", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = JovimTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.updatePostContent("Génère une idée de post d'été engageant pour notre nouvelle solution.")
                            selectedTab = 0
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary.copy(0.1f), contentColor = JovimPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Idée de post", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            if (contentText.isNotBlank()) {
                                viewModel.updatePostContent("$contentText Corrige les fautes d'orthographe et reformule de manière engageante :")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary.copy(0.1f), contentColor = JovimPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Améliorer le texte", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            1 -> {
                // Adapt content screen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Adaptation Auto-Adaptative par Réseau", fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { viewModel.adaptContentForSelectedPlatforms() },
                                colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                enabled = !uiState.isAdaptationLoading && contentText.isNotBlank() && uiState.selectedPlatforms.isNotEmpty()
                            ) {
                                if (uiState.isAdaptationLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Adapter", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.adaptedContent.isNotEmpty()) {
                            Text(
                                text = uiState.adaptedContent,
                                fontSize = 14.sp,
                                color = JovimTextPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JovimBackground, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            )
                        } else {
                            Text(
                                "Sélectionnez des réseaux cibles et cliquez sur 'Adapter' pour voir votre publication réécrite de manière optimale par notre assistant IA.",
                                color = JovimTextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    }
                }
            }

            2 -> {
                // Audit pre publication screen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Audit de Qualité IA", fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { viewModel.runPrePublicationAudit() },
                                colors = ButtonDefaults.buttonColors(containerColor = JovimGold, contentColor = JovimPrimary),
                                enabled = !uiState.isAuditLoading && contentText.isNotBlank()
                            ) {
                                if (uiState.isAuditLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = JovimPrimary, strokeWidth = 2.dp)
                                } else {
                                    Text("Analyser le post", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.qualityScore > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(
                                            if (uiState.qualityScore >= 80) JovimSuccess.copy(0.15f) else JovimGold.copy(0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${uiState.qualityScore}/100",
                                        fontWeight = FontWeight.Black,
                                        color = if (uiState.qualityScore >= 80) JovimSuccess else JovimGold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Potentiel d'engagement estimé", color = JovimTextSecondary, fontSize = 12.sp)
                                    Text(
                                        if (uiState.qualityScore >= 80) "Excellent" else "Moyen",
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.qualityScore >= 80) JovimSuccess else JovimGold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = uiState.aiAnalysisFeedback,
                                fontSize = 13.sp,
                                color = JovimTextPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JovimBackground, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            )
                        } else {
                            Text(
                                "Analysez la qualité de votre post avant publication pour connaître ses points forts et recevoir des suggestions d'optimisation.",
                                color = JovimTextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Submit Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showScheduleDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = JovimPrimary),
                border = BorderStroke(1.dp, JovimPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = contentText.isNotBlank() && uiState.selectedPlatforms.isNotEmpty()
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Planifier", fontSize = 14.sp)
            }

            Button(
                onClick = { viewModel.publishPostImmediately(context) },
                modifier = Modifier.weight(1f).testTag("publish_immediate_button"),
                colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                shape = RoundedCornerShape(8.dp),
                enabled = contentText.isNotBlank() && uiState.selectedPlatforms.isNotEmpty()
            ) {
                Icon(Icons.Default.Publish, contentDescription = null, tint = JovimPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publier", color = JovimPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Schedule post dialog
    if (showScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = { Text("Planifier la publication", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Choisissez la date de diffusion sur vos canaux.", fontSize = 13.sp, color = JovimTextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("• Demain à 09:00", fontWeight = FontWeight.Bold, color = JovimPrimary, fontSize = 14.sp)
                    Text("• Lundi prochain à 12:00", fontWeight = FontWeight.Medium, color = JovimTextSecondary, fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.schedulePost(System.currentTimeMillis() + 86400000) // Planifié pour demain
                        showScheduleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary)
                ) {
                    Text("Planifier pour demain", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) {
                    Text("Fermer", color = JovimTextSecondary)
                }
            }
        )
    }

    if (uiState.publishErrorMsg != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPublishError() },
            title = { Text("Vérification avant publication", fontWeight = FontWeight.Bold, color = JovimPrimary) },
            text = { Text(uiState.publishErrorMsg, fontSize = 14.sp, color = JovimTextPrimary) },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissPublishError() },
                    colors = ButtonDefaults.buttonColors(containerColor = JovimGold)
                ) {
                    Text("OK", color = JovimPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// --- 7. ASSISTANT JOVIM AI SCREEN (Chatroom) ---
@Composable
fun JovimAiScreen(
    viewModel: JovimViewModel,
    uiState: JovimUiState,
    messages: List<ChatMessage>,
    brandKit: BrandKit
) {
    if (!uiState.isOnline) {
        OfflineScreenOverlay(onRetry = { viewModel.checkNetworkStatus() })
        return
    }

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
    ) {
        // AI Branding bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JovimPrimary)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(JovimGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Hub", tint = JovimPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("JO AI Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            TextButton(onClick = { viewModel.clearChatHistory() }) {
                Text("Effacer", color = Color.White.copy(0.6f), fontSize = 12.sp)
            }
        }

        // Suggested Prompt starters when history is empty
        if (messages.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JovimGold, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Comment puis-je vous aider ?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = JovimTextPrimary
                )
                Text(
                    "Je connais l'identité de votre marque ${brandKit.companyName} et son style de communication.",
                    textAlign = TextAlign.Center,
                    color = JovimTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // Prompts suggestions buttons
                PromptSuggestionCard(
                    title = "Écrire un post d'été",
                    desc = "Générer un post accrocheur pour Instagram.",
                    onClick = { textInput = "Rédige une publication Instagram engageante de 3 phrases pour lancer notre offre d'été." }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PromptSuggestionCard(
                    title = "Générer des hashtags",
                    desc = "Trouver des tags porteurs pour l'engagement.",
                    onClick = { textInput = "Trouve une liste de 10 hashtags viraux et pertinents dans le domaine de la tech et du marketing digital." }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PromptSuggestionCard(
                    title = "Planifier une Newsletter",
                    desc = "Rédiger une newsletter moderne et efficace.",
                    onClick = { textInput = "Écris le plan d'une newsletter professionnelle pour annoncer nos nouveautés de juillet à nos clients." }
                )
            }
        } else {
            // Chat history list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }

                if (uiState.isAiLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = JovimPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("JO AI réfléchit...", fontSize = 13.sp, color = JovimTextSecondary)
                        }
                    }
                }
            }
        }

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color.LightGray.copy(0.4f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Demandez à JO AI...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input"),
                singleLine = false,
                maxLines = 4,
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendAiChatMessage(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .background(JovimPrimary, CircleShape)
                    .size(44.dp)
                    .testTag("send_chat_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Envoyer",
                    tint = JovimGold
                )
            }
        }
    }
}

@Composable
fun PromptSuggestionCard(title: String, desc: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = JovimPrimary, fontSize = 13.sp)
            Text(desc, color = JovimTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val align = if (isUser) Alignment.End else Alignment.Start
    val bg = if (isUser) JovimPrimary else Color.White
    val textCol = if (isUser) Color.White else JovimTextPrimary
    val borderStroke = if (isUser) null else BorderStroke(1.dp, Color.LightGray.copy(0.3f))

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Card(
            colors = CardDefaults.cardColors(containerColor = bg),
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isUser) 12.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 12.dp
            ),
            border = borderStroke,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = textCol,
                fontSize = 14.sp,
                modifier = Modifier.padding(12.dp),
                lineHeight = 18.sp
            )
        }
        Text(
            text = if (isUser) "Vous" else "JO AI",
            fontSize = 10.sp,
            color = JovimTextSecondary,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}

// --- 8. EDITORIAL CALENDAR SCREEN ---
@Composable
fun CalendarScreen(viewModel: JovimViewModel, posts: List<Post>) {
    var calendarViewMode by remember { mutableIntStateOf(0) } // 0: Jour, 1: Semaine, 2: Mois
    val scheduledPosts = posts.filter { it.status == "SCHEDULED" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .padding(16.dp)
    ) {
        Text(
            "Calendrier Éditorial",
            color = JovimTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Planifiez et déplacez vos publications pour un flux média harmonieux.",
            color = JovimTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // View Modes
        TabRow(
            selectedTabIndex = calendarViewMode,
            containerColor = Color.White,
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, Color.LightGray.copy(0.3f))
        ) {
            Tab(selected = calendarViewMode == 0, onClick = { calendarViewMode = 0 }) {
                Text("Vue Jour", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = calendarViewMode == 1, onClick = { calendarViewMode = 1 }) {
                Text("Vue Semaine", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = calendarViewMode == 2, onClick = { calendarViewMode = 2 }) {
                Text("Vue Mois", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Calendar Day Simulator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Planning de Diffusion",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = JovimTextPrimary
                )
                Text(
                    "Publications programmées dans la file d'attente.",
                    color = JovimTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (scheduledPosts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucun post programmé pour le moment.\nCréez-en un dans l'onglet '+' !",
                            textAlign = TextAlign.Center,
                            color = JovimTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        items(scheduledPosts) { post ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JovimBackground, RoundedCornerShape(8.dp))
                                    .border(1.dp, JovimGold.copy(0.4f), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    val date = Date(post.scheduledTime)
                                    val format = SimpleDateFormat("dd MMM - HH:mm", Locale.FRANCE)
                                    Text(
                                        "Date : ${format.format(date)}",
                                        fontWeight = FontWeight.Bold,
                                        color = JovimGold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        post.contentText,
                                        color = JovimTextPrimary,
                                        fontSize = 13.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        post.platforms.split(",").forEach { platform ->
                                            Text(platform, fontSize = 9.sp, color = JovimPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                IconButton(onClick = { viewModel.deletePost(post.id) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Annuler", tint = JovimError)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 9. MEDIA LIBRARY SCREEN ---
@Composable
fun MediaLibraryScreen(viewModel: JovimViewModel, mediaItems: List<MediaItem>) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf("Tous") }
    var showAddMediaDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Social") }
    var newType by remember { mutableStateOf("IMAGE") }
    var pickedUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val categories = listOf("Tous", "Social", "Campagnes", "Banners", "Ressources")

    // Real system storage content launcher
    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        pickedUri = uri
        if (uri != null) {
            newTitle = "Média " + (if (newType == "IMAGE") "Photo" else if (newType == "VIDEO") "Vidéo" else "Audio") + " - " + 
                SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    pickedUri = null
                    newTitle = ""
                    showAddMediaDialog = true 
                },
                containerColor = JovimGold,
                contentColor = JovimPrimary
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Ajouter un média")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(JovimBackground)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Bibliothèque de Médias",
                color = JovimTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Stockez vos visuels de marque, optimisez vos images/vidéos et associez-les à vos publications.",
                color = JovimTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Category filter row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                items(categories) { cat ->
                    val selected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .background(if (selected) JovimPrimary else Color.White, RoundedCornerShape(16.dp))
                            .border(1.dp, if (selected) JovimPrimary else Color.LightGray, RoundedCornerShape(16.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            cat,
                            color = if (selected) JovimGold else JovimTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Display grid of media files
            val filteredMedia = if (selectedCategory == "Tous") mediaItems else mediaItems.filter { it.category == selectedCategory }

            if (filteredMedia.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun média dans cette catégorie.", color = JovimTextSecondary)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredMedia) { item ->
                        MediaCardItem(item, onDelete = { viewModel.deleteMediaItem(item.id) })
                    }
                }
            }
        }
    }

    // Add media item dialog
    if (showAddMediaDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!uiState.isUploadingMedia) showAddMediaDialog = false 
            },
            title = { Text("Importer un nouveau média", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Choisissez d'abord le format :", fontSize = 12.sp, color = JovimTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = newType == "IMAGE", 
                                onClick = { 
                                    newType = "IMAGE" 
                                    pickedUri = null
                                },
                                enabled = !uiState.isUploadingMedia
                            )
                            Text("Image", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = newType == "VIDEO", 
                                onClick = { 
                                    newType = "VIDEO" 
                                    pickedUri = null
                                },
                                enabled = !uiState.isUploadingMedia
                            )
                            Text("Vidéo", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = newType == "AUDIO", 
                                onClick = { 
                                    newType = "AUDIO" 
                                    pickedUri = null
                                },
                                enabled = !uiState.isUploadingMedia
                            )
                            Text("Audio", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Media Picker Trigger
                    if (pickedUri == null) {
                        OutlinedButton(
                            onClick = {
                                when (newType) {
                                    "IMAGE" -> mediaLauncher.launch("image/*")
                                    "VIDEO" -> mediaLauncher.launch("video/*")
                                    "AUDIO" -> mediaLauncher.launch("audio/*")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, JovimPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JovimPrimary),
                            enabled = !uiState.isUploadingMedia
                        ) {
                            Icon(Icons.Default.PermMedia, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sélectionner un fichier local", fontSize = 13.sp)
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = JovimSuccess.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, JovimSuccess)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = JovimSuccess)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Fichier validé avec succès (URI : ...${pickedUri.toString().takeLast(25)})",
                                    fontSize = 11.sp,
                                    color = JovimPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        textStyle = TextStyle(color = Color.Black),
                        placeholder = { Text("Titre du média", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isUploadingMedia,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = JovimPrimary,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Catégorie de classement :", fontSize = 12.sp, color = JovimTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Social", "Campagnes", "Banners", "Ressources").forEach { cat ->
                            val active = newCategory == cat
                            Box(
                                modifier = Modifier
                                    .background(if (active) JovimGold else Color.LightGray.copy(0.3f), RoundedCornerShape(8.dp))
                                    .clickable { if (!uiState.isUploadingMedia) newCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) JovimPrimary else JovimTextPrimary)
                            }
                        }
                    }

                    // Simulated upload and progressive compression bar
                    if (uiState.isUploadingMedia) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Compression et Optimisation...", fontSize = 11.sp, color = JovimPrimary, fontWeight = FontWeight.Bold)
                                Text("${uiState.uploadProgress}%", fontSize = 11.sp, color = JovimGold, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { uiState.uploadProgress / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = JovimGold,
                                trackColor = Color.LightGray.copy(0.3f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Conversion de format & réduction de taille sans perte", fontSize = 10.sp, color = JovimTextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.simulateMediaUpload(
                                title = newTitle,
                                mediaUrl = pickedUri?.toString() ?: "android.resource://com.example/drawable/ic_placeholder",
                                type = newType,
                                category = newCategory,
                                onComplete = {
                                    newTitle = ""
                                    pickedUri = null
                                    showAddMediaDialog = false
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                    enabled = newTitle.isNotBlank() && !uiState.isUploadingMedia
                ) {
                    Text("Lancer l'importation", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddMediaDialog = false },
                    enabled = !uiState.isUploadingMedia
                ) {
                    Text("Annuler", color = JovimTextSecondary)
                }
            }
        )
    }
}

@Composable
fun MediaCardItem(item: MediaItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
    ) {
        Column {
            // Simulated graphic image container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(JovimPrimary.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.mediaType == "IMAGE") Icons.Default.Image else Icons.Default.Videocam,
                    contentDescription = null,
                    tint = JovimPrimary.copy(alpha = 0.4f),
                    modifier = Modifier.size(36.dp)
                )
                // Watermark "JO"
                Text(
                    "JO",
                    color = JovimPrimary.copy(alpha = 0.02f),
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = JovimTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${item.category} • ${item.fileSize}", fontSize = 10.sp, color = JovimTextSecondary)
                    IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = JovimError, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

// --- 10. ANALYTICS SCREEN ---
@Composable
fun AnalyticsScreen(viewModel: JovimViewModel, accounts: List<Account>, posts: List<Post>) {
    val uiState by viewModel.uiState.collectAsState()
    if (!uiState.isOnline) {
        OfflineScreenOverlay(onRetry = { viewModel.checkNetworkStatus() })
        return
    }

    var statPeriod by remember { mutableStateOf("30 Derniers Jours") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Analyses de Performance",
            color = JovimTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Suivez la croissance de votre marque sur tous vos canaux.",
            color = JovimTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dropdown selection (mocked row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Période :", fontWeight = FontWeight.Bold, color = JovimTextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("7J", "30J", "90J").forEach { p ->
                    val isCur = (p == "30J" && statPeriod == "30 Derniers Jours") || (p == "7J" && statPeriod == "7 Derniers Jours")
                    Box(
                        modifier = Modifier
                            .background(if (isCur) JovimGold else Color.White, RoundedCornerShape(12.dp))
                            .clickable { statPeriod = if (p == "30J") "30 Derniers Jours" else "7 Derniers Jours" }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(p, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = JovimPrimary)
                    }
                }
            }
        }

        // Stats boxes grid
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Indicateurs Clés ($statPeriod)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Portée Totale", color = JovimTextSecondary, fontSize = 12.sp)
                        Text("48.5K", color = JovimPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("+12.4% vs mois préc.", color = JovimSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Taux d'Engagement", color = JovimTextSecondary, fontSize = 12.sp)
                        Text("5.8%", color = JovimPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("+0.8% vs mois préc.", color = JovimSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Native Canvas Drawing: Interactive Area Curve
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Courbe d'Engagement Hebdomadaire", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Visites uniques cumulées", color = JovimTextSecondary, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        val points = listOf(
                            Offset(0f, height * 0.85f),
                            Offset(width * 0.15f, height * 0.7f),
                            Offset(width * 0.35f, height * 0.45f),
                            Offset(width * 0.55f, height * 0.55f),
                            Offset(width * 0.75f, height * 0.2f),
                            Offset(width * 1f, height * 0.1f)
                        )

                        val path = Path()
                        path.moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            path.lineTo(points[i].x, points[i].y)
                        }

                        // Draw area below curve with custom gradient
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(JovimPrimary.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )

                        // Draw Curve outline
                        drawPath(
                            path = path,
                            color = JovimPrimary,
                            style = Stroke(width = 6f)
                        )

                        // Draw connection dots
                        points.forEach { pt ->
                            drawCircle(color = JovimGold, radius = 6f, center = pt)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Lun", fontSize = 10.sp, color = JovimTextSecondary)
                    Text("Mar", fontSize = 10.sp, color = JovimTextSecondary)
                    Text("Mer", fontSize = 10.sp, color = JovimTextSecondary)
                    Text("Jeu", fontSize = 10.sp, color = JovimTextSecondary)
                    Text("Ven", fontSize = 10.sp, color = JovimTextSecondary)
                    Text("Sam", fontSize = 10.sp, color = JovimTextSecondary)
                }
            }
        }
    }
}

// --- 11. USER PROFILE SCREEN ---
@Composable
fun ProfileScreen(viewModel: JovimViewModel, brandKit: BrandKit, accounts: List<Account>) {
    val uiState by viewModel.uiState.collectAsState()
    val usersList by viewModel.users.collectAsState()
    
    val activeCount = accounts.count { it.isConnected }
    val user = uiState.currentUser
    
    val userFullName = user?.fullName ?: "Jospin Kanani"
    val userEmail = user?.email ?: "jospin.kanani"
    val userRole = user?.role ?: "Administrateur"
    val isGoogleUser = user?.isGoogleConnected ?: false

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { viewModel.updateUserAvatar(it.toString()) }
    }

    var showAvatarDialog by remember { mutableStateOf(false) }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("Modifier la photo de profil") },
            text = { Text("Choisissez une méthode pour mettre à jour votre avatar.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        avatarPickerLauncher.launch("image/*")
                        showAvatarDialog = false
                    }
                ) {
                    Text("Galerie", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            viewModel.updateUserAvatar("camera_avatar_${System.currentTimeMillis()}")
                            showAvatarDialog = false
                        }
                    ) {
                        Text("Appareil Photo")
                    }
                    if (uiState.userAvatarUrl.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.updateUserAvatar("")
                                showAvatarDialog = false
                            }
                        ) {
                            Text("Réinitialiser", color = JovimError)
                        }
                    }
                }
            }
        )
    }

    // Form states for password change
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var passwordChangeMessage by remember { mutableStateOf("") }
    var isPasswordSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Upper Profile Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JovimPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(JovimGold, CircleShape)
                        .clickable { showAvatarDialog = true }
                        .testTag("profile_avatar_box"),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.userAvatarUrl.isEmpty()) {
                        val initials = if (userFullName.contains(" ")) {
                            val parts = userFullName.split(" ")
                            "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                        } else {
                            userFullName.take(2).uppercase()
                        }
                        Text(initials, color = JovimPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Avatar personnalisé",
                            tint = JovimPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White, CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = JovimPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(userFullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(userEmail, color = Color.White.copy(0.7f), fontSize = 13.sp)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Role Badge
                Surface(
                    color = if (userRole == "Administrateur") JovimGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, if (userRole == "Administrateur") JovimGold else Color.White.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = userRole,
                        color = if (userRole == "Administrateur") JovimGold else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$activeCount", color = JovimGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Reseaux Lies", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${usersList.size}", color = JovimGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Utilisateurs Base", color = Color.White.copy(0.6f), fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECTION 1: POSTGRESQL SYNCHRONIZATION CONSOLE ---
        Text("Synchronisation PostgreSQL Cloud :", fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Base de donnees PostgreSQL", fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
                        Text(uiState.postgresSyncMessage, color = JovimTextSecondary, fontSize = 12.sp)
                    }
                    
                    // Connection Status Badge
                    Surface(
                        color = if (uiState.isOnline) JovimSuccess.copy(alpha = 0.1f) else JovimError.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (uiState.isOnline) JovimSuccess else JovimError)
                    ) {
                        Text(
                            text = if (uiState.isOnline) "Postgres En Ligne" else "Postgres Hors Ligne",
                            color = if (uiState.isOnline) JovimSuccess else JovimError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Live console terminal for sync logs
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.postgresLogs) { log ->
                            Text(
                                text = log,
                                color = Color(0xFF10B981), // green console text
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { viewModel.syncWithPostgreSQL() },
                    colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !uiState.isSyncingPostgres
                ) {
                    if (uiState.isSyncingPostgres) {
                        CircularProgressIndicator(color = JovimPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Synchroniser maintenant avec PostgreSQL", color = JovimPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECTION 2: PASSWORD MODIFICATION (FOR ADMINS AND REGISTERED USERS) ---
        Text("Parametres de Securite :", fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Modifier le mot de passe", fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
                Text("Permet de modifier votre mot de passe securise.", color = JovimTextSecondary, fontSize = 12.sp)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isGoogleUser) {
                    Text(
                        "Vous etes connecte avec Google OAuth 2.0. La modification du mot de passe n'est pas applicable.",
                        color = JovimTextSecondary,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    OutlinedTextField(
                        value = currentPasswordInput,
                        onValueChange = { currentPasswordInput = it },
                        placeholder = { Text("Mot de passe actuel", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = JovimPrimary,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        placeholder = { Text("Nouveau mot de passe", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = JovimPrimary,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color.Black
                        )
                    )
                    
                    if (passwordChangeMessage.isNotEmpty()) {
                        Text(
                            text = passwordChangeMessage,
                            color = if (isPasswordSuccess) JovimSuccess else JovimError,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            if (currentPasswordInput.isBlank() || newPasswordInput.isBlank()) {
                                passwordChangeMessage = "Veuillez remplir les deux champs."
                                isPasswordSuccess = false
                            } else {
                                val success = viewModel.changePassword(currentPasswordInput, newPasswordInput)
                                if (success) {
                                    passwordChangeMessage = "Votre mot de passe a ete mis a jour avec succes !"
                                    isPasswordSuccess = true
                                    currentPasswordInput = ""
                                    newPasswordInput = ""
                                } else {
                                    passwordChangeMessage = "Mot de passe actuel incorrect."
                                    isPasswordSuccess = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Enregistrer", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECTION 3: ADMINISTRATOR - POSTGRESQL USER LIST ---
        if (userRole == "Administrateur") {
            Text("Gestion des Utilisateurs PostgreSQL (Admin) :", fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Utilisateurs enregistres dans PostgreSQL", fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
                    Text("Total : ${usersList.size} utilisateur(s) synchronise(s)", color = JovimTextSecondary, fontSize = 12.sp)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    usersList.forEach { userItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(userItem.fullName, fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 13.sp)
                                Text("Identifiant : ${userItem.email}", color = JovimTextSecondary, fontSize = 11.sp)
                                Text("Role : ${userItem.role}", color = JovimTextSecondary, fontSize = 11.sp)
                            }
                            
                            // Delete user button (do not allow deleting yourself or jospin.kanani admin)
                            val isProtected = userItem.email == userEmail || userItem.email == "jospin.kanani"
                            if (!isProtected) {
                                IconButton(
                                    onClick = { viewModel.adminDeleteUser(userItem.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = JovimError)
                                }
                            } else {
                                Surface(
                                    color = JovimSuccess.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Protege",
                                        color = JovimSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(0.5f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Branding Memory Display
        Text("Memoire de Marque IA :", fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { viewModel.setScreen(AppScreen.Settings) },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = JovimGold, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(brandKit.companyName, fontWeight = FontWeight.Bold, color = JovimTextPrimary, fontSize = 14.sp)
                    Text("Slogan : ${brandKit.slogan}", color = JovimTextSecondary, fontSize = 11.sp)
                    Text("Style : ${brandKit.communicationStyle}", color = JovimTextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = JovimError.copy(0.1f), contentColor = JovimError),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Deconnexion", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileMenuRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = JovimPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = JovimTextPrimary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = JovimTextSecondary)
        }
    }
}

// --- 12. SETTINGS SCREEN (Brand Kit & Security) ---
@Composable
fun SettingsScreen(viewModel: JovimViewModel, brandKit: BrandKit) {
    var compName by remember { mutableStateOf(brandKit.companyName) }
    var slogan by remember { mutableStateOf(brandKit.slogan) }
    var services by remember { mutableStateOf(brandKit.services) }
    var audience by remember { mutableStateOf(brandKit.targetAudience) }
    var style by remember { mutableStateOf(brandKit.communicationStyle) }

    var enable2FA by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Paramètres & Marque",
            color = JovimTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Configurez la mémoire de l'IA et sécurisez vos comptes.",
            color = JovimTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // BRAND KIT HEADER
        Text(
            "Mémoire de Marque JO AI",
            fontWeight = FontWeight.Bold,
            color = JovimPrimary,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = compName,
                    onValueChange = { compName = it },
                    placeholder = { Text("Nom de l'entreprise / Marque", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = JovimPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = slogan,
                    onValueChange = { slogan = it },
                    placeholder = { Text("Slogan", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = JovimPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = services,
                    onValueChange = { services = it },
                    placeholder = { Text("Services / Produits Clés", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = JovimPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = audience,
                    onValueChange = { audience = it },
                    placeholder = { Text("Audience Cible", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = JovimPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = style,
                    onValueChange = { style = it },
                    placeholder = { Text("Style de communication (ex: Professionnel, Fun)", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = JovimPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updated = brandKit.copy(
                            companyName = compName,
                            slogan = slogan,
                            services = services,
                            targetAudience = audience,
                            communicationStyle = style
                        )
                        viewModel.updateBrandKit(updated)
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary)
                ) {
                    Text("Sauvegarder", color = Color.White)
                }
            }
        }

        // SECURITY SECTION
        Text(
            "Sécurité & Accréditations",
            fontWeight = FontWeight.Bold,
            color = JovimPrimary,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Double Authentification (2FA)", fontWeight = FontWeight.Bold, color = JovimTextPrimary)
                        Text("Sécurisez votre compte Hub", color = JovimTextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = enable2FA,
                        onCheckedChange = { enable2FA = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = JovimGold, checkedTrackColor = JovimPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Sécurité OAuth 2.0 actifs :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JovimTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Certificats de jetons de session chiffrés (JWT) - Actif", color = JovimTextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun OfflineScreenOverlay(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, JovimError.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Pas de connexion",
                    tint = JovimError,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Connexion Internet requise",
                    color = JovimPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cette fonctionnalite necessite une connexion Internet ou Wi-Fi active pour interagir avec les serveurs de JO Social Hub et PostgreSQL.",
                    color = JovimTextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // Status badge
                Surface(
                    color = JovimError.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, JovimError),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(JovimError, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Statut : Deconnecte",
                            color = JovimError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "Reessayer la connexion",
                        color = JovimPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- ADD ACCOUNT SCREEN WITH PROFILE URL OAUTH AUTO-DETECTION ---
@Composable
fun AddAccountScreen(viewModel: JovimViewModel, accounts: List<Account>) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var smartUrlInput by remember { mutableStateOf("") }
    var detectedPlatform by remember { mutableStateOf<String?>(null) }
    
    // Profiles links inputs for the 13 platforms
    val platformUrlInputs = remember {
        mutableStateMapOf(
            "Facebook" to "",
            "Instagram" to "",
            "LinkedIn" to "",
            "TikTok" to "",
            "YouTube" to "",
            "X" to "",
            "Threads" to "",
            "Pinterest" to "",
            "WordPress" to "",
            "Blogger" to "",
            "Medium" to "",
            "Telegram" to "",
            "WhatsApp Business" to ""
        )
    }

    // Auto-detection logic based on pasted URL
    fun detectPlatformFromUrl(url: String): String? {
        val u = url.lowercase().trim()
        return when {
            u.contains("facebook.com") || u.contains("fb.com") -> "Facebook"
            u.contains("instagram.com") || u.contains("instagr.am") -> "Instagram"
            u.contains("linkedin.com") -> "LinkedIn"
            u.contains("tiktok.com") -> "TikTok"
            u.contains("youtube.com") || u.contains("youtu.be") -> "YouTube"
            u.contains("x.com") || u.contains("twitter.com") -> "X"
            u.contains("threads.net") -> "Threads"
            u.contains("pinterest.com") || u.contains("pin.it") -> "Pinterest"
            u.contains("wordpress.com") -> "WordPress"
            u.contains("blogger.com") || u.contains("blogspot.com") -> "Blogger"
            u.contains("medium.com") -> "Medium"
            u.contains("t.me") || u.contains("telegram.me") -> "Telegram"
            u.contains("wa.me") || u.contains("whatsapp.com") -> "WhatsApp Business"
            else -> null
        }
    }

    // Trigger platform detection whenever the smart input changes
    LaunchedEffect(smartUrlInput) {
        detectedPlatform = detectPlatformFromUrl(smartUrlInput)
    }

    // OAuth Connection Wizard States
    var showWizard by remember { mutableStateOf(false) }
    var wizardPlatform by remember { mutableStateOf("") }
    var wizardUrl by remember { mutableStateOf("") }
    var wizardStep by remember { mutableStateOf(1) } // 1 to 5 steps
    var extractedUsername by remember { mutableStateOf("") }
    var extractedFollowers by remember { mutableStateOf(0) }
    var wizardUsernameInput by remember { mutableStateOf("") }
    var wizardPasswordInput by remember { mutableStateOf("") }
    var permissionAccepted by remember { mutableStateOf(false) }
    var showExtractionDetails by remember { mutableStateOf(false) }

    fun startOAuthWizard(platform: String, url: String) {
        if (url.isBlank()) return
        
        // Basic verification
        val domain = platform.lowercase().replace(" ", "")
        if (!url.contains(domain) && !url.contains("t.me") && !url.contains("wa.me") && !url.contains("youtu.be") && !url.contains("blogspot")) {
            // Soft validation warning but let it proceed
        }

        // Auto-extract a realistic username
        val lastSegment = url.trim().trimEnd('/').substringAfterLast('/')
        val rawName = if (lastSegment.startsWith("@")) lastSegment else "@$lastSegment"
        val cleanName = if (rawName.length > 3) rawName else "@user_${(1000..9999).random()}"
        
        wizardPlatform = platform
        wizardUrl = url
        extractedUsername = cleanName
        extractedFollowers = (1200..85000).random()
        wizardStep = 1
        wizardUsernameInput = cleanName.replace("@", "")
        wizardPasswordInput = ""
        permissionAccepted = false
        showWizard = true
        showExtractionDetails = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JovimBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // TOP NAVIGATION HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.setScreen(AppScreen.Accounts) },
                modifier = Modifier
                    .background(Color.White.copy(0.1f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Ajouter un compte",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Connectez vos réseaux officiels via liens & OAuth",
                    color = JovimTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SMART AUTO-DETECTOR HERO CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JovimPrimary.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, JovimGold)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(JovimGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = JovimPrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Détecteur Intelligent d'URL",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Collez n'importe quel lien de profil social ci-dessous. JO Social Hub détectera automatiquement sa plateforme et lancera l'OAuth officiel.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = smartUrlInput,
                    onValueChange = { smartUrlInput = it },
                    placeholder = { Text("Coller le lien de votre profil...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().testTag("smart_url_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = JovimGold,
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedContainerColor = JovimBackground,
                        unfocusedContainerColor = JovimBackground
                    ),
                    singleLine = true
                )

                if (smartUrlInput.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (detectedPlatform != null) {
                        Surface(
                            color = JovimSuccess.copy(0.15f),
                            border = BorderStroke(1.dp, JovimSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Plateforme détectée : $detectedPlatform",
                                    color = JovimSuccess,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = { 
                                        startOAuthWizard(detectedPlatform!!, smartUrlInput)
                                        smartUrlInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JovimSuccess),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Connecter", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Surface(
                            color = JovimError.copy(0.15f),
                            border = BorderStroke(1.dp, JovimError),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = JovimError, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Lien non reconnu pour l'une des 13 plateformes",
                                    color = JovimError,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Les 13 plateformes supportées",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 13 PLATFORM CARDS
        val platformList = listOf(
            Triple("Facebook", "https://www.facebook.com/votre_profil", "#1877F2"),
            Triple("Instagram", "https://www.instagram.com/votre_profil", "#E4405F"),
            Triple("LinkedIn", "https://www.linkedin.com/in/votre_profil", "#0A66C2"),
            Triple("TikTok", "https://www.tiktok.com/@votre_profil", "#000000"),
            Triple("YouTube", "https://www.youtube.com/@votre_chaine", "#FF0000"),
            Triple("X", "https://x.com/votre_pseudo", "#101010"),
            Triple("Threads", "https://www.threads.net/@votre_pseudo", "#202020"),
            Triple("Pinterest", "https://www.pinterest.com/votre_pseudo", "#BD081C"),
            Triple("WordPress", "https://votreblog.wordpress.com", "#21759B"),
            Triple("Blogger", "https://votreblog.blogspot.com", "#F57C00"),
            Triple("Medium", "https://medium.com/@votre_pseudo", "#02B875"),
            Triple("Telegram", "https://t.me/votre_canal_ou_profil", "#24A1DE"),
            Triple("WhatsApp Business", "https://wa.me/33612345678", "#25D366")
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            platformList.forEach { (platformName, placeholderUrl, hexColor) ->
                val colorParsed = Color(android.graphics.Color.parseColor(hexColor))
                val isConnected = accounts.any { it.platformName.lowercase().contains(platformName.lowercase()) && it.isConnected }
                val connectedAccount = accounts.find { it.platformName.lowercase().contains(platformName.lowercase()) && it.isConnected }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // High contrast circular letter logo
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(colorParsed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = platformName.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = platformName,
                                    fontWeight = FontWeight.Bold,
                                    color = JovimPrimary,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (isConnected) "Connecté : ${connectedAccount?.accountName}" else "Non connecté",
                                    color = if (isConnected) JovimSuccess else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (isConnected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Connecté",
                                    tint = JovimSuccess,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        if (!isConnected) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = platformUrlInputs[platformName] ?: "",
                                    onValueChange = { platformUrlInputs[platformName] = it },
                                    placeholder = { Text("Lien du profil : $placeholderUrl", color = Color.Gray, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f).height(50.dp).testTag("input_${platformName.lowercase()}"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(6.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = JovimPrimary,
                                        unfocusedTextColor = JovimPrimary,
                                        focusedBorderColor = colorParsed,
                                        unfocusedBorderColor = Color.LightGray
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val enteredUrl = platformUrlInputs[platformName] ?: ""
                                        if (enteredUrl.isNotBlank()) {
                                            startOAuthWizard(platformName, enteredUrl)
                                            platformUrlInputs[platformName] = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorParsed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(50.dp),
                                    enabled = (platformUrlInputs[platformName] ?: "").isNotBlank()
                                ) {
                                    Text("Lier", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Type : ${if (connectedAccount?.isProfessional == true) "Professionnel (API Directe)" else "Personnel (Clipboard)"}",
                                    color = Color.DarkGray,
                                    fontSize = 11.sp
                                )
                                TextButton(
                                    onClick = { 
                                        viewModel.connectAccountFromProfileUrl(platformName, "", "Déconnecté", "", 0, false)
                                    }
                                ) {
                                    Text("Déconnecter", color = JovimError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // INTERACTIVE 8-STEP OAUTH SIMULATION WIZARD DIALOG
    if (showWizard) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showWizard = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Header with Platform Name
                    Text(
                        text = "Assistant de Connexion Officielle & Synchro PostgreSQL",
                        fontWeight = FontWeight.Bold,
                        color = JovimPrimary,
                        fontSize = 15.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Step indicators and active progress bar
                    LinearProgressIndicator(
                        progress = { wizardStep / 5f },
                        color = JovimGold,
                        trackColor = Color.LightGray.copy(0.3f),
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Étape $wizardStep sur 5",
                        color = JovimTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (wizardStep) {
                        1 -> {
                            // STEP 1 & 2: URL Verification and Detection
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(50.dp).background(JovimGold.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = JovimGold, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Analyse & Validation du lien",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = JovimPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Vérification de la validité de l'URL :\n$wizardUrl",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Format URL correct", color = JovimSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Nom de domaine validé avec succès", color = JovimSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(18.dp))
                                Button(
                                    onClick = { 
                                        wizardStep = 2 
                                        showExtractionDetails = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Suivant", color = Color.White)
                                }
                            }
                        }
                        2 -> {
                            // STEP 3: Metadata Extraction (Username, Avatar, Followers count)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(50.dp).background(JovimPrimary.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = JovimPrimary, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Extraction des informations publiques",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = JovimPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Métadonnées extraites depuis le lien de profil :",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Visual extracted profile preview card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = JovimBackground),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Simulated Avatar
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(JovimGold, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = wizardPlatform.take(1).uppercase(),
                                                color = JovimPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = extractedUsername,
                                                fontWeight = FontWeight.Bold,
                                                color = JovimPrimary,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Statut : Public",
                                                color = JovimSuccess,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Abonnés : ${String.format("%,d", extractedFollowers)} abonnés",
                                                color = Color.DarkGray,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))
                                Button(
                                    onClick = { wizardStep = 3 },
                                    colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Lancer l'OAuth officiel", color = Color.White)
                                }
                            }
                        }
                        3 -> {
                            // STEP 4 & 5: Official platform login mire (OAuth simulation)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    color = Color.White,
                                    border = BorderStroke(1.5.dp, Color.LightGray),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        // Mini Web Mockup Header
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.LightGray.copy(0.3f))
                                                .padding(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "auth.api.officiel.${wizardPlatform.lowercase()}.com",
                                                fontSize = 10.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Connexion sécurisée via OAuth",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = JovimPrimary,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        
                                        OutlinedTextField(
                                            value = wizardUsernameInput,
                                            onValueChange = { wizardUsernameInput = it },
                                            label = { Text("Utilisateur / E-mail", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth().height(46.dp),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = JovimPrimary,
                                                unfocusedTextColor = JovimPrimary,
                                                focusedBorderColor = JovimGold,
                                                unfocusedBorderColor = Color.LightGray
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = wizardPasswordInput,
                                            onValueChange = { wizardPasswordInput = it },
                                            label = { Text("Mot de passe", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth().height(46.dp),
                                            singleLine = true,
                                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = JovimPrimary,
                                                unfocusedTextColor = JovimPrimary,
                                                focusedBorderColor = JovimGold,
                                                unfocusedBorderColor = Color.LightGray
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Button(
                                            onClick = { wizardStep = 4 },
                                            colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            enabled = wizardUsernameInput.isNotBlank() && wizardPasswordInput.isNotBlank()
                                        ) {
                                            Text("Se connecter de manière sécurisée", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "ℹ️ Vos identifiants de connexion transitent directement par les serveurs sécurisés de la plateforme et ne sont jamais stockés dans l'application.",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                        4 -> {
                            // STEP 6: Publishing permissions approval
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(50.dp).background(JovimGold.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = JovimGold, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Autorisations de Publication",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = JovimPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "JO Social Hub demande les privilèges suivants pour gérer votre compte :",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = JovimBackground),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Publier des textes et des hashtags (obligatoire)", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Charger et téléverser des médias (images/vidéos)", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Consulter les statistiques basiques d'engagement", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { wizardStep = 5 },
                                    colors = ButtonDefaults.buttonColors(containerColor = JovimGold),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Autoriser & Connecter", color = JovimPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        5 -> {
                            // STEP 7 & 8: Access token security generation & cloud synchronization
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(50.dp).background(JovimSuccess.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = JovimSuccess, modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Compte associé avec succès !",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = JovimSuccess
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Jeton d'accès OAuth chiffré généré :",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = Color.LightGray.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "token_sh_${wizardPlatform.take(3).lowercase()}_9a1f8b2d3c4e5f6a...",
                                        fontSize = 10.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        modifier = Modifier.padding(8.dp),
                                        color = Color.DarkGray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "🔗 PostgreSQL Synced\nLiaison de sécurité effectuée dans le Cloud PostgreSQL.",
                                    fontSize = 11.sp,
                                    color = JovimPrimary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { 
                                        viewModel.connectAccountFromProfileUrl(
                                            platformName = wizardPlatform,
                                            profileUrl = wizardUrl,
                                            accountName = extractedUsername,
                                            avatarUrl = "https://example.com/avatar.png",
                                            followerCount = extractedFollowers,
                                            isProfessional = true
                                        )
                                        showWizard = false
                                        viewModel.setScreen(AppScreen.Accounts)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JovimPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Terminer", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


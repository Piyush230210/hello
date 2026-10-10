package com.example.neongamingcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Bg = Color(0xFF080B12)
private val Panel = Color(0xFF121927)
private val Neon = Color(0xFF35E7FF)
private val Purple = Color(0xFF9B75FF)
private val Muted = Color(0xFF9BA9BC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("companion_settings", MODE_PRIVATE)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Neon, secondary = Purple, background = Bg, surface = Panel)) {
                CompanionApp(
                    initialSensitivity = prefs.getFloat("sensitivity", 50f),
                    initialFov = prefs.getFloat("fov", 90f),
                    initialEffects = prefs.getBoolean("effects", true),
                    saveSetting = { key, value ->
                        prefs.edit().apply {
                            when (value) {
                                is Float -> putFloat(key, value)
                                is Boolean -> putBoolean(key, value)
                            }
                        }.apply()
                    }
                )
            }
        }
    }
}

@Composable
private fun CompanionApp(
    initialSensitivity: Float,
    initialFov: Float,
    initialEffects: Boolean,
    saveSetting: (String, Any) -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    var sensitivity by remember { mutableFloatStateOf(initialSensitivity) }
    var fov by remember { mutableFloatStateOf(initialFov) }
    var effects by remember { mutableStateOf(initialEffects) }
    var loading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            NavigationBar(containerColor = Panel, contentColor = Neon) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.MyLocation, null) }, label = { Text("Trainer") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
            }
        }
    ) { padding ->
        when (tab) {
            0 -> HomeScreen(
                modifier = Modifier.padding(padding), loading = loading, progress = progress, effects = effects,
                onToggleEffects = { effects = it; saveSetting("effects", it) },
                onLoad = {
                    if (!loading) scope.launch {
                        loading = true; progress = 0f
                        repeat(40) { delay(45); progress = (it + 1) / 40f }
                        loading = false
                        Toast.makeText(context, "Companion ready. No game files were modified.", Toast.LENGTH_SHORT).show()
                    }
                },
                onLaunch = {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.pubg.imobile")
                    if (intent != null) context.startActivity(intent)
                    else Toast.makeText(context, "BGMI wasn't found. Install it from its official store first.", Toast.LENGTH_LONG).show()
                }
            )
            1 -> TrainerScreen(Modifier.padding(padding))
            else -> SettingsScreen(
                Modifier.padding(padding), sensitivity, fov, effects,
                { sensitivity = it; saveSetting("sensitivity", it) },
                { fov = it; saveSetting("fov", it) },
                { effects = it; saveSetting("effects", it) }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier, loading: Boolean, progress: Float, effects: Boolean,
    onToggleEffects: (Boolean) -> Unit, onLoad: () -> Unit, onLaunch: () -> Unit
) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(46.dp).background(Neon.copy(alpha = .12f), RoundedCornerShape(14.dp)).border(1.dp, Neon.copy(alpha = .45f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Bolt, null, tint = Neon, modifier = Modifier.size(28.dp)) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("NEON / COMPANION", color = Neon, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Gaming Control Center", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Box(Modifier.fillMaxWidth().height(175.dp).background(Panel, RoundedCornerShape(24.dp)).border(1.dp, Purple.copy(alpha = .4f), RoundedCornerShape(24.dp)).padding(20.dp)) {
                Column(Modifier.align(Alignment.CenterStart), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("READY WHEN YOU ARE", color = Muted, fontSize = 11.sp, letterSpacing = 1.6.sp)
                    Text("PLAY SMART.\nSTAY IN CONTROL.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, lineHeight = 29.sp)
                    Text("Tools for your practice routine", color = Muted, fontSize = 13.sp)
                }
                Box(Modifier.align(Alignment.CenterEnd).size(84.dp).border(2.dp, Neon.copy(alpha = .65f), CircleShape), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(58.dp).border(1.dp, Purple, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Bolt, null, tint = Neon, modifier = Modifier.size(36.dp)) }
                }
            }
        }
        item {
            SectionCard("SESSION INITIALIZER", "Runs a local setup animation; it does not inject code or alter BGMI.") {
                Button(onClick = onLoad, enabled = !loading, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Neon, contentColor = Bg)) {
                    Text(if (loading) "INITIALIZING…" else "INITIALIZE COMPANION", fontWeight = FontWeight.Bold)
                }
                if (loading || progress > 0f) {
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(), color = Neon, trackColor = Bg)
                    Text("${(progress * 100).toInt()}%", color = Muted, fontSize = 12.sp)
                }
            }
        }
        item {
            SectionCard("QUICK LAUNCH", "Open BGMI if the official app is installed on this device.") {
                OutlinedButton(onClick = onLaunch, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = Neon)) { Text("OPEN BGMI") }
            }
        }
        item {
            SectionCard("STREAM-FRIENDLY DISPLAY", "Controls this companion app's optional visual effects only.") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Neon UI effects", color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Does not hide or manipulate game capture", color = Muted, fontSize = 12.sp)
                    }
                    Switch(checked = effects, onCheckedChange = onToggleEffects)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, description: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(20.dp)).border(1.dp, Color.White.copy(alpha = .06f), RoundedCornerShape(20.dp)).padding(17.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        Text(title, color = Neon, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Text(description, color = Muted, fontSize = 13.sp, lineHeight = 18.sp)
        content()
    }
}

@Composable
private fun TrainerScreen(modifier: Modifier) {
    var score by remember { mutableIntStateOf(0) }
    var attempts by remember { mutableIntStateOf(0) }
    var target by remember { mutableStateOf(Offset(.5f, .5f)) }
    var radius by remember { mutableFloatStateOf(.075f) }
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("AIM TRAINER", color = Neon, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Text("Tap the targets", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("A standalone practice mini-game. Tap inside the glowing circle to score.", color = Muted, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("SCORE", score.toString(), Modifier.weight(1f))
            StatCard("TAPS", attempts.toString(), Modifier.weight(1f))
            StatCard("HIT RATE", if (attempts == 0) "—" else "${(score * 100 / attempts)}%", Modifier.weight(1f))
        }
        Canvas(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF0D1420), RoundedCornerShape(22.dp)).border(1.dp, Neon.copy(alpha = .35f), RoundedCornerShape(22.dp)).pointerInput(target) {
            detectTapGestures { tap ->
                attempts++
                val center = Offset(target.x * size.width, target.y * size.height)
                val dist = (tap - center).getDistance()
                if (dist <= size.width * radius) score++
                target = Offset(Random.nextFloat().coerceIn(.12f, .88f), Random.nextFloat().coerceIn(.12f, .88f))
                radius = Random.nextFloat() * .035f + .045f
            }
        }) {
            val center = Offset(target.x * size.width, target.y * size.height)
            val r = size.width * radius
            drawCircle(color = Purple.copy(alpha = .22f), radius = r * 1.8f, center = center)
            drawCircle(color = Neon, radius = r, center = center)
            drawCircle(color = Bg, radius = r * .62f, center = center)
            drawCircle(color = Neon, radius = r * .25f, center = center)
            for (i in 1..4) {
                val x = size.width * i / 5f
                drawLine(Color.White.copy(alpha = .035f), Offset(x, 0f), Offset(x, size.height), 1f)
                val y = size.height * i / 5f
                drawLine(Color.White.copy(alpha = .035f), Offset(0f, y), Offset(size.width, y), 1f)
            }
        }
        OutlinedButton(onClick = { score = 0; attempts = 0; target = Offset(.5f, .5f) }, modifier = Modifier.fillMaxWidth()) { Text("RESET SESSION") }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(14.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Neon, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsScreen(
    modifier: Modifier, sensitivity: Float, fov: Float, effects: Boolean,
    onSensitivity: (Float) -> Unit, onFov: (Float) -> Unit, onEffects: (Boolean) -> Unit
) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(17.dp)) {
        Text("CONFIGURATION", color = Neon, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Text("Personalize your setup", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("These are reference values saved on this device. They do not automatically change in-game settings.", color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
        SectionCard("SENSITIVITY PROFILE", "Use this slider to record your preferred practice sensitivity.") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Practice sensitivity", color = Color.White); Text(sensitivity.toInt().toString(), color = Neon, fontWeight = FontWeight.Bold) }
            Slider(value = sensitivity, onValueChange = onSensitivity, valueRange = 1f..100f)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Low", color = Muted, fontSize = 11.sp); Text("High", color = Muted, fontSize = 11.sp) }
        }
        SectionCard("FIELD-OF-VIEW PREVIEW", "Preview preference only; the app cannot force an iPad view in BGMI.") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("FOV reference", color = Color.White); Text("${fov.toInt()}°", color = Neon, fontWeight = FontWeight.Bold) }
            Slider(value = fov, onValueChange = onFov, valueRange = 70f..120f)
            Canvas(Modifier.fillMaxWidth().height(110.dp).background(Bg, RoundedCornerShape(12.dp)).padding(8.dp)) {
                val spread = (fov - 70f) / 50f
                val half = size.width * (.22f + spread * .24f)
                val bottom = Offset(size.width / 2f, size.height * .9f)
                drawLine(Neon.copy(alpha = .75f), bottom, Offset(size.width / 2f - half, size.height * .12f), 3f)
                drawLine(Neon.copy(alpha = .75f), bottom, Offset(size.width / 2f + half, size.height * .12f), 3f)
                drawLine(Purple.copy(alpha = .6f), Offset(size.width / 2f, size.height * .9f), Offset(size.width / 2f, size.height * .12f), 1f)
            }
        }
        SectionCard("INTERFACE", "Choose whether decorative neon effects are enabled.") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Neon effects", color = Color.White, modifier = Modifier.weight(1f))
                Switch(checked = effects, onCheckedChange = onEffects)
            }
        }
    }
}

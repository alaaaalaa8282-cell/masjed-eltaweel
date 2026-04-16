package com.mohamedabdelazeim.islamicapp.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedabdelazeim.islamicapp.data.ZekrData
import com.mohamedabdelazeim.islamicapp.data.ZekrPrefs
import com.mohamedabdelazeim.islamicapp.service.ZekrService
import com.mohamedabdelazeim.islamicapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToAdhkar: () -> Unit) {
    val ctx = LocalContext.current

    // State
    var isEnabled by remember { mutableStateOf(ZekrPrefs.isEnabled(ctx)) }
    var intervalMinutes by remember { mutableIntStateOf(ZekrPrefs.getIntervalMinutes(ctx)) }
    var playbackMode by remember { mutableStateOf(ZekrPrefs.getPlaybackMode(ctx)) }
    var repeatIndex by remember { mutableIntStateOf(ZekrPrefs.getRepeatIndex(ctx)) }
    var showIntervalMenu by remember { mutableStateOf(false) }
    var showZekrMenu by remember { mutableStateOf(false) }

    val allAdhkar = remember { ZekrData.loadAllAdhkar(ctx) }
    val intervalOptions = listOf(15, 30, 60, 120)

    val gradient = Brush.verticalGradient(listOf(DeepBlue, SurfaceBlue, Color(0xFF0A1628)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Header ────────────────────────────────────────────────────────
            Text(
                text = "☪️ صلاتي",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Gold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "أذكار وأدعية إسلامية",
                fontSize = 14.sp,
                color = Color(0xFFAAAAAA),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Settings Card ─────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2133))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "⚙️ إعدادات التذكير",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Enable/Disable Switch
                    SettingRow(
                        icon = Icons.Default.Notifications,
                        label = "تفعيل التذكير"
                    ) {
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { v ->
                                isEnabled = v
                                ZekrPrefs.setEnabled(ctx, v)
                                if (v) startService(ctx, intervalMinutes)
                                else stopService(ctx)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Gold
                            )
                        )
                    }

                    HorizontalDivider(color = Color(0xFF1A2940), modifier = Modifier.padding(vertical = 12.dp))

                    // Interval Dropdown
                    SettingRow(
                        icon = Icons.Default.Timer,
                        label = "الفترة الزمنية"
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = showIntervalMenu,
                            onExpandedChange = { showIntervalMenu = it }
                        ) {
                            OutlinedButton(
                                onClick = { showIntervalMenu = true },
                                modifier = Modifier.menuAnchor(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
                                border = BorderStroke(1.dp, Gold)
                            ) {
                                Text("$intervalMinutes دقيقة", fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp))
                            }
                            ExposedDropdownMenu(
                                expanded = showIntervalMenu,
                                onDismissRequest = { showIntervalMenu = false }
                            ) {
                                intervalOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text("$opt دقيقة") },
                                        onClick = {
                                            intervalMinutes = opt
                                            ZekrPrefs.setIntervalMinutes(ctx, opt)
                                            if (isEnabled) startService(ctx, opt)
                                            showIntervalMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1A2940), modifier = Modifier.padding(vertical = 12.dp))

                    // Playback Mode Toggle
                    SettingRow(
                        icon = Icons.Default.Repeat,
                        label = "وضع التشغيل"
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (playbackMode == ZekrPrefs.MODE_SEQUENTIAL) "تسلسلي" else "تكرار",
                                fontSize = 13.sp,
                                color = Gold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Switch(
                                checked = playbackMode == ZekrPrefs.MODE_REPEAT,
                                onCheckedChange = { v ->
                                    playbackMode = if (v) ZekrPrefs.MODE_REPEAT else ZekrPrefs.MODE_SEQUENTIAL
                                    ZekrPrefs.setPlaybackMode(ctx, playbackMode)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = Gold
                                )
                            )
                        }
                    }

                    // Select Zekr Dropdown (visible only in Repeat mode)
                    AnimatedVisibility(visible = playbackMode == ZekrPrefs.MODE_REPEAT) {
                        Column {
                            HorizontalDivider(color = Color(0xFF1A2940), modifier = Modifier.padding(vertical = 12.dp))
                            SettingRow(
                                icon = Icons.Default.Book,
                                label = "اختر الذكر"
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = showZekrMenu,
                                    onExpandedChange = { showZekrMenu = it }
                                ) {
                                    OutlinedButton(
                                        onClick = { showZekrMenu = true },
                                        modifier = Modifier.menuAnchor(),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
                                        border = BorderStroke(1.dp, Gold)
                                    ) {
                                        Text(
                                            text = if (allAdhkar.isNotEmpty())
                                                allAdhkar[repeatIndex.coerceIn(0, allAdhkar.size - 1)].title.take(15)
                                            else "اختر...",
                                            fontSize = 12.sp
                                        )
                                        Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp))
                                    }
                                    ExposedDropdownMenu(
                                        expanded = showZekrMenu,
                                        onDismissRequest = { showZekrMenu = false }
                                    ) {
                                        allAdhkar.forEachIndexed { idx, item ->
                                            DropdownMenuItem(
                                                text = { Text(item.title.take(30), fontSize = 13.sp) },
                                                onClick = {
                                                    repeatIndex = idx
                                                    ZekrPrefs.setRepeatIndex(ctx, idx)
                                                    showZekrMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Navigate to Adhkar ─────────────────────────────────────────────
            Button(
                onClick = onNavigateToAdhkar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    contentColor = Gold
                )
            ) {
                Icon(Icons.Default.MenuBook, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("📖 الأذكار الصباحية والمسائية", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Battery optimization hint
            if (isEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2940))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.BatteryAlert, null, tint = Color(0xFFFFAA00), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تأكد من استثناء التطبيق من توفير الطاقة لضمان عمل التذكير",
                            fontSize = 12.sp,
                            color = Color(0xFFAAAAAA),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Gold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = Color.White, fontSize = 15.sp)
        }
        content()
    }
}

private fun startService(ctx: Context, intervalMinutes: Int) {
    val intervalMs = intervalMinutes.toLong() * 60 * 1000
    val triggerAt = System.currentTimeMillis() + intervalMs
    val intent = Intent(ctx, ZekrService::class.java)
    val pending = PendingIntent.getService(
        ctx, 0, intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
}

private fun stopService(ctx: Context) {
    val intent = Intent(ctx, ZekrService::class.java)
    val pending = PendingIntent.getService(
        ctx, 0, intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pending)
    ctx.stopService(intent)
}

package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudySession
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudyScreen(
    studySessions: List<StudySession>,
    timerSecondsLeft: Int,
    isTimerRunning: Boolean,
    selectedSubject: String,
    onSelectSubject: (String) -> Unit,
    onSetDuration: (Int) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onDeleteSession: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects = listOf("Computer Science", "Mathematics", "Design & UX", "Literature", "General Study")
    val presets = listOf(10, 25, 45, 60)

    val minutes = timerSecondsLeft / 60
    val seconds = timerSecondsLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Study Pomodoro Countdown Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Subject Picker Row
                    Text(
                        text = "Focus Subject",
                        style = MaterialTheme.typography.labelMedium.copy(color = SupportText)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            var expanded by remember { mutableStateOf(false) }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderDivider),
                                color = WarmBg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = selectedSubject, style = MaterialTheme.typography.bodyMedium)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(SurfaceCard)
                            ) {
                                subjects.forEach { sub ->
                                    DropdownMenuItem(
                                        text = { Text(sub) },
                                        onClick = {
                                            onSelectSubject(sub)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Big Circular Timer Visual representation
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .border(
                                width = 6.dp,
                                color = if (isTimerRunning) PolishPrimary else BorderDivider,
                                shape = CircleShape
                            )
                            .background(WarmBg)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeFormatted,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnBgCharcoal
                                )
                            )
                            Text(
                                text = if (isTimerRunning) "STAY FOCUSED" else "READY TO FOCUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.2.sp,
                                    color = SupportText,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Preset Chips Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presets.forEach { min ->
                            val isSelected = (timerSecondsLeft == min * 60) && !isTimerRunning
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetDuration(min) },
                                label = { Text("${min}m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PolishPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Start/Pause/Reset Control Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset button
                        IconButton(
                            onClick = onResetTimer,
                            modifier = Modifier
                                .background(Color(0xFFE0E0E0), CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Timer",
                                tint = OnBgCharcoal
                            )
                        }

                        // Play / Pause FAB
                        Button(
                            onClick = onToggleTimer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) Color(0xFFBA1A1A) else PolishPrimary
                            ),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .widthIn(min = 140.dp)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle Timer"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (isTimerRunning) "Pause" else "Start Session")
                        }
                    }
                }
            }
        }

        // Section Title: Study History
        item {
            Text(
                text = "Today's Study Log",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (studySessions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No study sessions logged today.\nComplete a countdown focus block to save data!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SupportText),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(studySessions) { session ->
                val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
                val timeString = formatter.format(Date(session.timestamp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFF3E0),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Book,
                                        contentDescription = "Session",
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = session.subject,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$timeString • ${session.durationMinutes} minutes focus",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SupportText)
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDeleteSession(session.id) },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red.copy(alpha = 0.7f))
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Session")
                        }
                    }
                }
            }
        }
    }
}

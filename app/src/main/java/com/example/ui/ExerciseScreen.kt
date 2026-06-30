package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.ExerciseSession
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExerciseScreen(
    exerciseSessions: List<ExerciseSession>,
    onAddSession: (String, String, Int) -> Unit,
    onDeleteSession: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddForm by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Cardio") }
    var durationMinutes by remember { mutableStateOf(30) }

    val categories = listOf("Cardio", "Strength", "Flexibility", "Other")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Expandable Log Form
        item {
            AnimatedVisibility(
                visible = showAddForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, PolishPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Log A Workout Session",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PolishPrimary
                            )
                        )

                        // Name Field
                        OutlinedTextField(
                            value = exerciseName,
                            onValueChange = { exerciseName = it },
                            label = { Text("Exercise Name") },
                            placeholder = { Text("e.g. Evening Jog, Pushups") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PolishPrimary,
                                unfocusedBorderColor = BorderDivider
                            )
                        )

                        // Category Picker
                        Text(text = "Category", style = MaterialTheme.typography.labelMedium.copy(color = SupportText))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PolishPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Duration Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Duration: ",
                                style = MaterialTheme.typography.labelMedium.copy(color = SupportText)
                            )
                            Text(
                                text = "$durationMinutes minutes",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PolishPrimary
                                )
                            )
                        }
                        Slider(
                            value = durationMinutes.toFloat(),
                            onValueChange = { durationMinutes = it.toInt() },
                            valueRange = 5f..120f,
                            steps = 23, // increments of 5 mins
                            colors = SliderDefaults.colors(
                                thumbColor = PolishPrimary,
                                activeTrackColor = PolishPrimary
                            )
                        )

                        // Save Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showAddForm = false }) {
                                Text("Cancel", color = Color.Red)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (exerciseName.isNotBlank()) {
                                        onAddSession(exerciseName, selectedCategory, durationMinutes)
                                        exerciseName = ""
                                        showAddForm = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                                enabled = exerciseName.isNotBlank()
                            ) {
                                Text("Save Workout")
                            }
                        }
                    }
                }
            }
        }

        // Section header and control button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exercise Tracker",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (!showAddForm) {
                    Button(
                        onClick = { showAddForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Log Workout")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Workout")
                    }
                }
            }
        }

        // Log list
        if (exerciseSessions.isEmpty()) {
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
                            text = "No workouts logged today.\nLog your first session above to track progress!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SupportText),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(exerciseSessions) { session ->
                val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
                val timeString = formatter.format(Date(session.timestamp))

                val badgeColor = when (session.category) {
                    "Cardio" -> Color(0xFFE8F5E9)
                    "Strength" -> Color(0xFFEDE7F6)
                    "Flexibility" -> Color(0xFFFCE4EC)
                    else -> Color(0xFFECEFF1)
                }
                val iconColor = when (session.category) {
                    "Cardio" -> Color(0xFF2E7D32)
                    "Strength" -> Color(0xFF5E35B1)
                    "Flexibility" -> Color(0xFFD81B60)
                    else -> SupportText
                }

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
                                color = badgeColor,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = "Workout",
                                        tint = iconColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = session.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = badgeColor.copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = session.category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = iconColor,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "$timeString • ${session.durationMinutes} mins completed",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SupportText),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDeleteSession(session.id) },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red.copy(alpha = 0.7f))
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Workout")
                        }
                    }
                }
            }
        }
    }
}

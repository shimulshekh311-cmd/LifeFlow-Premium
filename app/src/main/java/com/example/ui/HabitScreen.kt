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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Habit
import com.example.ui.theme.*

@Composable
fun HabitScreen(
    habits: List<Habit>,
    onAddHabit: (String, String) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddForm by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }
    var habitDesc by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        if (habits.isEmpty() && !showAddForm) {
            // Beautiful Empty State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = SecondaryBadge,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = "Empty habits",
                            tint = PolishPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No habits tracked yet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Habits help form automatic daily success. Tap below to begin your first streak!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SupportText),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showAddForm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Your First Habit")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Expandable Add Habit Form Card
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Create New Habit",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PolishPrimary
                                    )
                                )
                                OutlinedTextField(
                                    value = habitName,
                                    onValueChange = { habitName = it },
                                    label = { Text("Habit Name") },
                                    placeholder = { Text("e.g. Drink 8 glasses of water") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PolishPrimary,
                                        unfocusedBorderColor = BorderDivider
                                    )
                                )
                                OutlinedTextField(
                                    value = habitDesc,
                                    onValueChange = { habitDesc = it },
                                    label = { Text("Description (Optional)") },
                                    placeholder = { Text("e.g. Keep a bottle on my desk") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PolishPrimary,
                                        unfocusedBorderColor = BorderDivider
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            showAddForm = false
                                            habitName = ""
                                            habitDesc = ""
                                        }
                                    ) {
                                        Text("Cancel", color = Color.Red)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (habitName.isNotBlank()) {
                                                onAddHabit(habitName, habitDesc)
                                                habitName = ""
                                                habitDesc = ""
                                                showAddForm = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                                        enabled = habitName.isNotBlank()
                                    ) {
                                        Text("Save Habit")
                                    }
                                }
                            }
                        }
                    }
                }

                // Header for List of Habits
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (!showAddForm) {
                            IconButton(
                                onClick = { showAddForm = true },
                                modifier = Modifier
                                    .background(SecondaryBadge, CircleShape)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Show Form",
                                    tint = OnSecondaryBadge,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // List items
                items(habits) { habit ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (habit.isCompletedToday) SecondaryBadge.copy(alpha = 0.3f) else SurfaceCard
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (habit.isCompletedToday) PolishPrimary.copy(alpha = 0.5f) else BorderLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Check button and titles
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Rounded custom Checkbox Button
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (habit.isCompletedToday) PolishPrimary else Color.Transparent
                                        )
                                        .border(2.dp, PolishPrimary, CircleShape)
                                        .clickable { onToggleHabit(habit) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (habit.isCompletedToday) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = habit.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else null,
                                            color = if (habit.isCompletedToday) SupportText.copy(alpha = 0.6f) else OnBgCharcoal
                                        )
                                    )
                                    if (habit.description.isNotBlank()) {
                                        Text(
                                            text = habit.description,
                                            style = MaterialTheme.typography.bodySmall.copy(color = SupportText),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Streak information and delete
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Streak Badge
                                if (habit.streak > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFEECEB),
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Whatshot,
                                                contentDescription = "Streak",
                                                tint = Color(0xFFE53935),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${habit.streak}d",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFC62828)
                                                )
                                            )
                                        }
                                    }
                                }

                                // Delete Habit
                                IconButton(
                                    onClick = { onDeleteHabit(habit.id) },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red.copy(alpha = 0.7f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Habit",
                                        modifier = Modifier.size(20.dp)
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

package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseSession
import com.example.data.Habit
import com.example.data.StudySession
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun StatisticsScreen(
    habits: List<Habit>,
    studySessions: List<StudySession>,
    exerciseSessions: List<ExerciseSession>,
    modifier: Modifier = Modifier
) {
    val totalHabitsCount = habits.size
    val completedHabitsCount = habits.count { it.isCompletedToday }
    val totalStudyMinutes = studySessions.sumOf { it.durationMinutes }
    val totalWorkoutsCount = exerciseSessions.size

    // Determine milestones status
    val hasActiveStreak = habits.any { it.streak > 0 }
    val hasDeepFocus = studySessions.any { it.durationMinutes >= 25 }
    val hasLoggedWorkout = totalWorkoutsCount > 0
    val completedAllToday = totalHabitsCount > 0 && completedHabitsCount == totalHabitsCount

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Core Statistics summary cards row
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Weekly Highlights",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$completedHabitsCount/$totalHabitsCount",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PolishPrimary
                            )
                        )
                        Text(
                            text = "Habits Today",
                            style = MaterialTheme.typography.labelSmall.copy(color = SupportText)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${totalStudyMinutes}m",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        )
                        Text(
                            text = "Total Focus",
                            style = MaterialTheme.typography.labelSmall.copy(color = SupportText)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$totalWorkoutsCount",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        )
                        Text(
                            text = "Workouts",
                            style = MaterialTheme.typography.labelSmall.copy(color = SupportText)
                        )
                    }
                }
            }
        }

        // Custom drawn Activity Bar Chart (Canvas)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Daily Flow Engagement",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Overview of combined habit, study, and physical activities",
                    style = MaterialTheme.typography.bodySmall.copy(color = SupportText),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Define weekly scores mock (with real variations based on logs)
                val baseScores = remember(completedHabitsCount, totalStudyMinutes, totalWorkoutsCount) {
                    val sc = floatArrayOf(25f, 40f, 65f, 30f, 50f, 15f, 10f)
                    // Inject today's live activity
                    val todayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7 // Map Mon=0 ... Sun=6
                    val liveActivityScore = (completedHabitsCount * 15f) + (totalStudyMinutes * 0.8f) + (totalWorkoutsCount * 20f)
                    sc[todayIndex] = liveActivityScore.coerceAtMost(100f).coerceAtLeast(15f)
                    sc
                }

                val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(top = 8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val barCount = baseScores.size
                        val barSpacing = 24.dp.toPx()
                        val barWidth = (width - (barSpacing * (barCount + 1))) / barCount

                        // Draw Grid lines
                        val gridLineCount = 4
                        for (i in 0 until gridLineCount) {
                            val y = height * (i.toFloat() / (gridLineCount - 1))
                            drawLine(
                                color = Color(0xFFECEFF1),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Draw Bars representing weekly activity scores
                        for (index in 0 until barCount) {
                            val score = baseScores[index]
                            val barHeight = height * (score / 100f)
                            val x = barSpacing + index * (barWidth + barSpacing)
                            val y = height - barHeight

                            // Gradient simulation or color based on activity intensity
                            val color = when {
                                score > 60f -> PolishPrimary
                                score > 35f -> SecondaryBadge
                                else -> BorderLight
                            }

                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                    }
                }

                // Days Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SupportText
                            ),
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Milestones & Achievement Badges Grid
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Aesthetic Flow Milestones",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MilestoneBadge(
                        title = "Streak Starter",
                        desc = "Formed an active habit streak",
                        icon = Icons.Default.Whatshot,
                        isUnlocked = hasActiveStreak,
                        color = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )

                    MilestoneBadge(
                        title = "Consistent Flow",
                        desc = "Completed all habits today",
                        icon = Icons.Default.EmojiEvents,
                        isUnlocked = completedAllToday,
                        color = Color(0xFFFBC02D),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MilestoneBadge(
                        title = "Deep Focus",
                        desc = "Logged 25m study block",
                        icon = Icons.Default.MenuBook,
                        isUnlocked = hasDeepFocus,
                        color = Color(0xFFFB8C00),
                        modifier = Modifier.weight(1f)
                    )

                    MilestoneBadge(
                        title = "Active Body",
                        desc = "Completed first physical log",
                        icon = Icons.Default.DirectionsRun,
                        isUnlocked = hasLoggedWorkout,
                        color = Color(0xFF43A047),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MilestoneBadge(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isUnlocked: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isUnlocked) color.copy(alpha = 0.08f) else Color(0xFFF5F5F5),
        border = BorderStroke(
            1.dp,
            if (isUnlocked) color.copy(alpha = 0.3f) else Color(0xFFE0E0E0)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = if (isUnlocked) color.copy(alpha = 0.15f) else Color(0xFFE0E0E0),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isUnlocked) color else SupportText.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) OnBgCharcoal else SupportText.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = SupportText.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    habitsCompletedCount: Int,
    habitsTotalCount: Int,
    totalStudyMinutes: Int,
    exerciseSessionsCount: Int,
    modifier: Modifier = Modifier
) {
    var isBreathingActive by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("Inhale") }

    // Automated breathing cycle
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            while (true) {
                breathPhase = "Inhale"
                delay(4000)
                breathPhase = "Hold"
                delay(2000)
                breathPhase = "Exhale"
                delay(4000)
                breathPhase = "Rest"
                delay(2000)
            }
        } else {
            breathPhase = "Inhale"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Welcome Back,",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SupportText)
                        )
                        Text(
                            text = "Your Life Flow",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PolishPrimary
                            )
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = SecondaryBadge,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Profile",
                                tint = OnSecondaryBadge,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderDivider)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "“The secret of your future is hidden in your daily routine.”",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = SupportText
                    )
                )
            }
        }

        // Stats Overview Grid Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Habits Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Habits Status",
                        tint = PolishPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "$habitsCompletedCount/$habitsTotalCount completed",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Daily Habits",
                            style = MaterialTheme.typography.labelSmall.copy(color = SupportText)
                        )
                    }
                }
            }

            // Study Timer Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Study Status",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "${totalStudyMinutes} mins today",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Focus Study",
                            style = MaterialTheme.typography.labelSmall.copy(color = SupportText)
                        )
                    }
                }
            }

            // Exercise Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Exercise Status",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "$exerciseSessionsCount workouts",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Exercises Logged",
                            style = MaterialTheme.typography.labelSmall.copy(color = SupportText)
                        )
                    }
                }
            }
        }

        // Mindfulness Breathing pacing Widget
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Mindfulness Pacer",
                        tint = PolishPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mindful Breath Flow",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isBreathingActive,
                    enter = fadeIn(animationSpec = tween(1500)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    val breathScale by animateFloatAsState(
                        targetValue = when (breathPhase) {
                            "Inhale" -> 1.5f
                            "Hold" -> 1.5f
                            "Exhale" -> 1.0f
                            else -> 1.0f
                        },
                        animationSpec = tween(
                            durationMillis = if (breathPhase == "Inhale" || breathPhase == "Exhale") 4000 else 2000,
                            easing = LinearEasing
                        ),
                        label = "breath_scale"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .scale(breathScale)
                                    .border(1.5.dp, PolishPrimary, CircleShape)
                                    .background(SecondaryBadge.copy(alpha = 0.4f), CircleShape)
                            )

                            AnimatedContent(
                                targetState = breathPhase,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                                },
                                label = "breath_phase"
                            ) { phase ->
                                Text(
                                    text = phase,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSecondaryBadge
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { isBreathingActive = !isBreathingActive },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBreathingActive) Color(0xFFBA1A1A) else PolishPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .testTag("breathe_button")
                        .height(44.dp)
                        .widthIn(min = 180.dp)
                ) {
                    Text(
                        text = if (isBreathingActive) "Stop Breathing" else "Start Flow Breathing",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

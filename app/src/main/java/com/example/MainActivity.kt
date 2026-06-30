package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WarmBg
import com.example.ui.theme.OnBgCharcoal
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.SecondaryBadge
import com.example.ui.theme.OnSecondaryBadge
import com.example.ui.theme.SupportText
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.BottomNavBg
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
          contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
          LifeFlowScreen(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun LifeFlowScreen(modifier: Modifier = Modifier) {
  var isBreathingActive by remember { mutableStateOf(false) }
  var breathPhase by remember { mutableStateOf("Inhale") }
  
  // Manage the automated breathing cycle when active
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

  Box(
    modifier = modifier
      .background(WarmBg)
      .testTag("main_container")
  ) {
    // Top Content Layout
    Column(
      modifier = Modifier
        .fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top section: Top App Bar conforming to Professional Polish
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
          .fillMaxWidth()
          .height(64.dp)
          .padding(horizontal = 16.dp)
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable { /* Handle click navigation */ }
            .testTag("top_bar_menu"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Navigation Menu",
            tint = OnBgCharcoal,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "LifeFlow",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Normal,
            color = OnBgCharcoal
          ),
          modifier = Modifier.testTag("top_bar_title")
        )
      }

      // Middle section: Central Professional Polish card
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(
            containerColor = SurfaceCard
          ),
          border = BorderStroke(1.dp, BorderLight),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 380.dp)
            .testTag("hero_card")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // AndroidIDE 2.7.1 Beta Build Indicator Badge
            Surface(
              shape = RoundedCornerShape(100.dp),
              color = SecondaryBadge,
              modifier = Modifier.testTag("build_badge")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(PolishPrimary, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "BUILD: 2.7.1 BETA",
                  style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSecondaryBadge
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Typographic Greetings: Light "Hello", Medium "World"
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "Hello ",
                style = MaterialTheme.typography.headlineLarge.copy(
                  fontSize = 44.sp,
                  fontWeight = FontWeight.Light,
                  color = PolishPrimary
                ),
                modifier = Modifier.testTag("hello_text")
              )
              Text(
                text = "World",
                style = MaterialTheme.typography.headlineLarge.copy(
                  fontSize = 44.sp,
                  fontWeight = FontWeight.Medium,
                  color = OnBgCharcoal
                ),
                modifier = Modifier.testTag("world_text")
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Divider
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderDivider)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Elegant Supportive Text
            Text(
              text = "Step 1: Initialization complete.\nAwaiting feature implementation.",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = SupportText,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("support_instructions")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Interactive Flow Breathing feature (Preserved & Polished)
            AnimatedVisibility(
              visible = isBreathingActive,
              enter = fadeIn(animationSpec = tween(1500)),
              exit = fadeOut(animationSpec = tween(500))
            ) {
              val breathScale by animateFloatAsState(
                targetValue = when (breathPhase) {
                  "Inhale" -> 1.4f
                  "Hold" -> 1.4f
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
                  modifier = Modifier.size(100.dp)
                ) {
                  // Breathing Circle
                  Box(
                    modifier = Modifier
                      .size(54.dp)
                      .scale(breathScale)
                      .border(1.5.dp, PolishPrimary, CircleShape)
                      .background(SecondaryBadge.copy(alpha = 0.4f), CircleShape)
                  )

                  // Text inside
                  AnimatedContent(
                    targetState = breathPhase,
                    transitionSpec = {
                      fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "breath_phase"
                  ) { phase ->
                    Text(
                      text = phase,
                      style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSecondaryBadge
                      )
                    )
                  }
                }
              }
            }

            // Elegant Button to trigger breathing
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
                .widthIn(min = 160.dp)
            ) {
              Text(
                text = if (isBreathingActive) "Stop Breathing" else "Start Flow Breathing",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        }
      }

      // Bottom section: Mantra Quote
      Text(
        text = "“Let your life flow with mindfulness and modern simplicity.”",
        style = MaterialTheme.typography.labelMedium.copy(
          color = SupportText,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
          .padding(horizontal = 24.dp, vertical = 8.dp)
          .alpha(0.7f)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Navigation Bar from Professional Polish
      Surface(
        color = BottomNavBg,
        modifier = Modifier
          .fillMaxWidth()
          .height(80.dp)
          .testTag("bottom_nav")
      ) {
        Row(
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Home (Active)
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clickable { /* Home tab */ }
          ) {
            Box(
              modifier = Modifier
                .background(SecondaryBadge, RoundedCornerShape(100.dp))
                .padding(horizontal = 20.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = OnSecondaryBadge,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Home",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = OnBgCharcoal
              )
            )
          }

          // Tasks (Inactive)
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
              .alpha(0.6f)
              .clickable { /* Tasks tab */ }
          ) {
            Box(
              modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Tasks",
                tint = OnBgCharcoal,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tasks",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = OnBgCharcoal
              )
            )
          }

          // Profile (Inactive)
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
              .alpha(0.6f)
              .clickable { /* Profile tab */ }
          ) {
            Box(
              modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = OnBgCharcoal,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Profile",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = OnBgCharcoal
              )
            )
          }
        }
      }
    }

    // Floating Action Button (M3 format)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 96.dp, end = 16.dp),
      contentAlignment = Alignment.BottomEnd
    ) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = SecondaryBadge,
        shadowElevation = 4.dp,
        modifier = Modifier
          .size(56.dp)
          .clickable { /* FAB Action */ }
          .testTag("fab")
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Item",
            tint = OnSecondaryBadge,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun LifeFlowScreenPreview() {
  MyApplicationTheme {
    LifeFlowScreen()
  }
}



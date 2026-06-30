package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Habit
import com.example.ui.theme.*
import java.util.Calendar

// Predefined template habits for quick-adding
data class HabitTemplate(
    val name: String,
    val description: String,
    val category: String,
    val emoji: String
)

val habitTemplates = listOf(
    HabitTemplate("Drink 8 Glasses of Water", "Keep hydrated for energy and glowing skin", "Health", "💧"),
    HabitTemplate("15 Min Meditation", "Calm the mind, focus on your breathing", "Mind", "🧘"),
    HabitTemplate("Read 10 Pages", "Expand knowledge and learn something new", "Mind", "📚"),
    HabitTemplate("30 Min Workout", "Get active, energize your body, and build strength", "Fitness", "💪"),
    HabitTemplate("Review Daily Budget", "Track expenses and build financial awareness", "Finance", "💰"),
    HabitTemplate("Sleep by 11 PM", "Get 8 hours of restorative sleep tonight", "Health", "😴"),
    HabitTemplate("Plan Next Day Priorities", "Write down top 3 goals before bed", "Work", "📝")
)

data class CategoryStyle(
    val emoji: String,
    val iconColor: Color,
    val bgColor: Color
)

fun getCategoryStyle(category: String): CategoryStyle {
    return when (category.lowercase()) {
        "health" -> CategoryStyle("🥦", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        "mind" -> CategoryStyle("🧠", Color(0xFF0288D1), Color(0xFFE1F5FE))
        "fitness" -> CategoryStyle("💪", Color(0xFFE65100), Color(0xFFFFF3E0))
        "work" -> CategoryStyle("💼", Color(0xFF7B1FA2), Color(0xFFF3E5F5))
        "finance" -> CategoryStyle("💰", Color(0xFF00796B), Color(0xFFE0F2F1))
        else -> CategoryStyle("✨", Color(0xFF6750A4), Color(0xFFE8DEF8))
    }
}

@Composable
fun HabitScreen(
    habits: List<Habit>,
    onAddHabit: (String, String, String) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddForm by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }
    var habitDesc by remember { mutableStateOf("") }
    var habitCategory by remember { mutableStateOf("Health") }

    // Search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf("All") } // "All", "Pending", "Completed"

    // Today's completion stats
    val totalHabitsCount = habits.size
    val completedHabitsCount = habits.count { it.isCompletedToday }
    val completionProgress = if (totalHabitsCount > 0) completedHabitsCount.toFloat() / totalHabitsCount else 0f

    // Filters implementation
    val filteredHabits = habits.filter { habit ->
        val matchesSearch = habit.name.contains(searchQuery, ignoreCase = true) ||
                habit.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "All" || habit.category.equals(selectedCategoryFilter, ignoreCase = true)
        val matchesStatus = when (selectedStatusFilter) {
            "Completed" -> habit.isCompletedToday
            "Pending" -> !habit.isCompletedToday
            else -> true
        }
        matchesSearch && matchesCategory && matchesStatus
    }

    val categories = listOf("All", "Health", "Mind", "Fitness", "Work", "Finance", "General")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBg)
    ) {
        // Search bar & add toggle header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search habits...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("habit_search_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PolishPrimary,
                    unfocusedBorderColor = BorderDivider,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { 
                    showAddForm = !showAddForm
                    if (showAddForm) {
                        // Reset form fields
                        habitName = ""
                        habitDesc = ""
                        habitCategory = "Health"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("add_habit_toggle_btn"),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Toggle Habit Form"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showAddForm) "Close" else "Create", fontWeight = FontWeight.SemiBold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Stats & Progress Widget
            if (totalHabitsCount > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("habit_stats_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Today's Success",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = OnBgCharcoal)
                                    )
                                    Text(
                                        text = "$completedHabitsCount of $totalHabitsCount habits completed",
                                        style = MaterialTheme.typography.bodySmall.copy(color = SupportText)
                                    )
                                }
                                Surface(
                                    color = SecondaryBadge,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${(completionProgress * 100).toInt()}%",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OnSecondaryBadge
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { completionProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = PolishPrimary,
                                trackColor = SecondaryBadge
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = when {
                                    completionProgress == 1f -> "🏆 All habits checked! You are unstoppable today!"
                                    completionProgress >= 0.5f -> "✨ Over halfway there! Keep this beautiful momentum!"
                                    completionProgress > 0f -> "⚡ Great start! Every small win builds a massive streak."
                                    else -> "🎯 Ready for action? Tap checkboxes below to check-off habits!"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = PolishPrimary
                                )
                            )
                        }
                    }
                }
            }

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
                        border = BorderStroke(1.5.dp, PolishPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("create_habit_form")
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

                            // Predefined suggestions quick row
                            Text(
                                text = "Quick Templates (Tap to fill):",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = SupportText)
                            )
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(habitTemplates) { template ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (habitName == template.name) PolishPrimary.copy(alpha = 0.15f) else SecondaryBadge.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp, 
                                            if (habitName == template.name) PolishPrimary else Color.Transparent
                                        ),
                                        modifier = Modifier.clickable {
                                            habitName = template.name
                                            habitDesc = template.description
                                            habitCategory = template.category
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = template.emoji, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = template.name.take(15) + if (template.name.length > 15) ".." else "",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Medium,
                                                    color = OnSecondaryBadge
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            OutlinedTextField(
                                value = habitName,
                                onValueChange = { habitName = it },
                                label = { Text("Habit Name") },
                                placeholder = { Text("e.g. Read 15 minutes") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("habit_name_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PolishPrimary,
                                    unfocusedBorderColor = BorderDivider
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = habitDesc,
                                onValueChange = { habitDesc = it },
                                label = { Text("Description (Optional)") },
                                placeholder = { Text("e.g. Right after morning coffee") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("habit_desc_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PolishPrimary,
                                    unfocusedBorderColor = BorderDivider
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Select Category Chips
                            Text(
                                text = "Category:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = SupportText)
                            )
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val categoryOptions = listOf("Health", "Mind", "Fitness", "Work", "Finance", "General")
                                items(categoryOptions) { cat ->
                                    val isSelected = habitCategory.equals(cat, ignoreCase = true)
                                    val style = getCategoryStyle(cat)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) style.bgColor else Color.Transparent,
                                        border = BorderStroke(1.5.dp, if (isSelected) style.iconColor else BorderDivider.copy(alpha = 0.5f)),
                                        modifier = Modifier.clickable { habitCategory = cat }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = style.emoji, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = cat,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) style.iconColor else OnBgCharcoal
                                                )
                                            )
                                        }
                                    }
                                }
                            }

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
                                    Text("Cancel", color = Color.Red, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (habitName.isNotBlank()) {
                                            onAddHabit(habitName, habitDesc, habitCategory)
                                            habitName = ""
                                            habitDesc = ""
                                            showAddForm = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = habitName.isNotBlank(),
                                    modifier = Modifier.testTag("save_habit_btn")
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = "Save")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save Habit")
                                }
                            }
                        }
                    }
                }
            }

            // Filters matrix (Status Filters & Category Filters)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Filters Row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            val style = getCategoryStyle(cat)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = cat },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (cat != "All") {
                                            Text(style.emoji)
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(cat)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (cat != "All") style.bgColor else PolishPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = if (cat != "All") style.iconColor else PolishPrimary
                                )
                            )
                        }
                    }

                    // Status Filters Row (All, Pending, Completed)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statusFilters = listOf("All", "Pending", "Completed")
                        statusFilters.forEach { status ->
                            val isSelected = selectedStatusFilter == status
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PolishPrimary else SecondaryBadge.copy(alpha = 0.3f))
                                    .clickable { selectedStatusFilter = status }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = status,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else OnBgCharcoal
                                )
                            }
                        }
                    }
                }
            }

            // Habits List Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (filteredHabits.isEmpty()) "Daily Habits" else "Habits (${filteredHabits.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = OnBgCharcoal)
                    )
                }
            }

            // List items
            if (filteredHabits.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.EventNote,
                            contentDescription = "No habits match",
                            tint = SupportText.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching habits found",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SupportText)
                        )
                        Text(
                            text = "Try adjusting your filters or search query.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SupportText.copy(alpha = 0.7f)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                items(filteredHabits, key = { it.id }) { habit ->
                    HabitListItemCard(
                        habit = habit,
                        onToggleHabit = onToggleHabit,
                        onDeleteHabit = onDeleteHabit
                    )
                }
            }
        }
    }
}

@Composable
fun HabitListItemCard(
    habit: Habit,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Int) -> Unit
) {
    val catStyle = getCategoryStyle(habit.category)

    // Calculate weekly completion checkmark array dynamically based on real streak & today status
    val calendar = Calendar.getInstance()
    val todayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // adjust Mon=0 .. Sun=6
    val daysOfWeekLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    val weeklyCompletion = remember(habit.streak, habit.isCompletedToday) {
        BooleanArray(7) { index ->
            if (index == todayIndex) {
                habit.isCompletedToday
            } else if (index < todayIndex) {
                val daysBack = todayIndex - index
                val activeStreak = if (habit.isCompletedToday) habit.streak else habit.streak
                daysBack < activeStreak
            } else {
                false
            }
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isCompletedToday) catStyle.bgColor.copy(alpha = 0.4f) else SurfaceCard
        ),
        border = BorderStroke(
            1.dp,
            if (habit.isCompletedToday) catStyle.iconColor.copy(alpha = 0.4f) else BorderLight
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("habit_card_${habit.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Checkbox, Name, Streak, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side: Check & title
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Check button custom styled
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (habit.isCompletedToday) catStyle.iconColor else Color.Transparent
                            )
                            .border(2.dp, catStyle.iconColor, CircleShape)
                            .clickable { onToggleHabit(habit) }
                            .testTag("habit_checkbox_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (habit.isCompletedToday) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = catStyle.emoji,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = habit.name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else null,
                                    color = if (habit.isCompletedToday) SupportText.copy(alpha = 0.5f) else OnBgCharcoal
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (habit.description.isNotBlank()) {
                            Text(
                                text = habit.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = SupportText),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }
                }

                // Right side: Streak Badge & Delete icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Streak Badge
                    if (habit.streak > 0) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEECEB),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
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

                    IconButton(
                        onClick = { onDeleteHabit(habit.id) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_habit_${habit.id}"),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Habit",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Divider
            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)

            // Weekly Check-in Grid: showing Mon-Sun progress visual matrix
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Track",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SupportText.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = if (habit.isCompletedToday) "Completed for today!" else "Pending today",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (habit.isCompletedToday) catStyle.iconColor else SupportText,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    daysOfWeekLabels.forEachIndexed { index, dayLabel ->
                        val isDayCompleted = weeklyCompletion[index]
                        val isToday = index == todayIndex

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Circle dot
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDayCompleted -> catStyle.iconColor
                                            isToday -> catStyle.iconColor.copy(alpha = 0.15f)
                                            else -> SecondaryBadge.copy(alpha = 0.3f)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when {
                                            isToday -> catStyle.iconColor
                                            isDayCompleted -> Color.Transparent
                                            else -> BorderLight
                                        },
                                        CircleShape
                                    )
                                    .clickable {
                                        if (isToday) {
                                            onToggleHabit(habit)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDayCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else if (isToday) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(catStyle.iconColor)
                                    )
                                }
                            }

                            // Day Label text
                            Text(
                                text = dayLabel,
                                fontSize = 10.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                color = if (isToday) catStyle.iconColor else SupportText.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

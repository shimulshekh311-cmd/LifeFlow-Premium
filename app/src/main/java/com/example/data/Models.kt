package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val streak: Int = 0,
    val lastCompletedTimestamp: Long = 0L,
    val isCompletedToday: Boolean = false,
    val category: String = "General"
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exercise_sessions")
data class ExerciseSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "Cardio", "Strength", "Flexibility", "Other"
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

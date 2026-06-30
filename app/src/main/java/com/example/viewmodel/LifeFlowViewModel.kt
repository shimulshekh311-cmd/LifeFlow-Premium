package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExerciseSession
import com.example.data.Habit
import com.example.data.LifeFlowRepository
import com.example.data.StudySession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LifeFlowViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LifeFlowRepository

    val habits: StateFlow<List<Habit>>
    val studySessions: StateFlow<List<StudySession>>
    val exerciseSessions: StateFlow<List<ExerciseSession>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LifeFlowRepository(
            database.habitDao(),
            database.studySessionDao(),
            database.exerciseSessionDao()
        )
        habits = repository.allHabits.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        studySessions = repository.allStudySessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        exerciseSessions = repository.allExerciseSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Active Study Focus Timer State
    private val _studyTimerSecondsLeft = MutableStateFlow(1500) // Default 25 minutes (Pomodoro)
    val studyTimerSecondsLeft: StateFlow<Int> = _studyTimerSecondsLeft.asStateFlow()

    private val _isStudyTimerRunning = MutableStateFlow(false)
    val isStudyTimerRunning: StateFlow<Boolean> = _isStudyTimerRunning.asStateFlow()

    private val _selectedStudySubject = MutableStateFlow("Computer Science")
    val selectedStudySubject: StateFlow<String> = _selectedStudySubject.asStateFlow()

    private var timerJob: Job? = null
    private var initialTimerDurationSeconds = 1500

    fun selectStudySubject(subject: String) {
        _selectedStudySubject.value = subject
    }

    fun setStudyTimerDuration(minutes: Int) {
        val seconds = minutes * 60
        _studyTimerSecondsLeft.value = seconds
        initialTimerDurationSeconds = seconds
        _isStudyTimerRunning.value = false
        timerJob?.cancel()
    }

    fun toggleStudyTimer() {
        if (_isStudyTimerRunning.value) {
            pauseStudyTimer()
        } else {
            startStudyTimer()
        }
    }

    private fun startStudyTimer() {
        _isStudyTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_studyTimerSecondsLeft.value > 0) {
                delay(1000)
                _studyTimerSecondsLeft.value -= 1
            }
            _isStudyTimerRunning.value = false
            // Save completed session to local database
            val durationMin = initialTimerDurationSeconds / 60
            if (durationMin > 0) {
                addStudySession(_selectedStudySubject.value, durationMin)
            }
            resetStudyTimer()
        }
    }

    private fun pauseStudyTimer() {
        _isStudyTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetStudyTimer() {
        _isStudyTimerRunning.value = false
        timerJob?.cancel()
        _studyTimerSecondsLeft.value = initialTimerDurationSeconds
    }

    // Habits Operations
    fun addHabit(name: String, description: String) {
        viewModelScope.launch {
            repository.insertHabit(Habit(name = name, description = description))
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habit)
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            repository.deleteHabit(id)
        }
    }

    // Study Sessions Operations
    fun addStudySession(subject: String, durationMinutes: Int) {
        viewModelScope.launch {
            repository.insertStudySession(StudySession(subject = subject, durationMinutes = durationMinutes))
        }
    }

    fun deleteStudySession(id: Int) {
        viewModelScope.launch {
            repository.deleteStudySession(id)
        }
    }

    // Exercise Sessions Operations
    fun addExerciseSession(name: String, category: String, durationMinutes: Int) {
        viewModelScope.launch {
            repository.insertExerciseSession(
                ExerciseSession(name = name, category = category, durationMinutes = durationMinutes)
            )
        }
    }

    fun deleteExerciseSession(id: Int) {
        viewModelScope.launch {
            repository.deleteExerciseSession(id)
        }
    }
}

package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Int)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)
}

@Dao
interface ExerciseSessionDao {
    @Query("SELECT * FROM exercise_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ExerciseSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ExerciseSession)

    @Query("DELETE FROM exercise_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)
}

@Database(entities = [Habit::class, StudySession::class, ExerciseSession::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun exerciseSessionDao(): ExerciseSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifeflow_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class LifeFlowRepository(
    private val habitDao: HabitDao,
    private val studySessionDao: StudySessionDao,
    private val exerciseSessionDao: ExerciseSessionDao
) {
    // Habits with auto-healing daily reset logic
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits().map { list ->
        list.map { habit ->
            val today = Calendar.getInstance()
            val lastCompleted = Calendar.getInstance().apply { timeInMillis = habit.lastCompletedTimestamp }
            val isSameDay = habit.lastCompletedTimestamp != 0L &&
                    today.get(Calendar.YEAR) == lastCompleted.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == lastCompleted.get(Calendar.DAY_OF_YEAR)

            // Auto-reset UI state if last completion was on a different day
            if (!isSameDay && habit.isCompletedToday) {
                habit.copy(isCompletedToday = false)
            } else {
                habit
            }
        }
    }

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)

    suspend fun deleteHabit(id: Int) = habitDao.deleteHabitById(id)

    suspend fun toggleHabitCompletion(habit: Habit) {
        val today = Calendar.getInstance()
        val isNowCompleted = !habit.isCompletedToday
        val lastCompleted = Calendar.getInstance().apply { timeInMillis = habit.lastCompletedTimestamp }
        
        val isYesterday = habit.lastCompletedTimestamp != 0L && {
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            yesterday.get(Calendar.YEAR) == lastCompleted.get(Calendar.YEAR) &&
            yesterday.get(Calendar.DAY_OF_YEAR) == lastCompleted.get(Calendar.DAY_OF_YEAR)
        }()

        val isSameDay = habit.lastCompletedTimestamp != 0L &&
                today.get(Calendar.YEAR) == lastCompleted.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == lastCompleted.get(Calendar.DAY_OF_YEAR)

        val newStreak = if (isNowCompleted) {
            if (isYesterday || habit.lastCompletedTimestamp == 0L) {
                habit.streak + 1
            } else if (isSameDay) {
                habit.streak
            } else {
                1 // streak broken and restarted
            }
        } else {
            if (habit.streak > 0) habit.streak - 1 else 0
        }

        val updated = habit.copy(
            isCompletedToday = isNowCompleted,
            lastCompletedTimestamp = if (isNowCompleted) today.timeInMillis else habit.lastCompletedTimestamp,
            streak = newStreak
        )
        habitDao.updateHabit(updated)
    }

    // Study Sessions
    val allStudySessions: Flow<List<StudySession>> = studySessionDao.getAllSessions()
    suspend fun insertStudySession(session: StudySession) = studySessionDao.insertSession(session)
    suspend fun deleteStudySession(id: Int) = studySessionDao.deleteSessionById(id)

    // Exercise Sessions
    val allExerciseSessions: Flow<List<ExerciseSession>> = exerciseSessionDao.getAllSessions()
    suspend fun insertExerciseSession(session: ExerciseSession) = exerciseSessionDao.insertSession(session)
    suspend fun deleteExerciseSession(id: Int) = exerciseSessionDao.deleteSessionById(id)
}

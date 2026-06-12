package com.example.lifeflowpremium;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HabitAdapter.OnHabitInteractionListener {

    private DatabaseHelper dbHelper;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;

    private TextView tvStreak;
    private TextView tvWaterCount;
    private TextView tvBreathPrompt;
    private Button btnStartBreath;
    private Button btnWaterMinus;
    private Button btnWaterPlus;
    private EditText etHabitTitle;
    private ImageButton btnAddHabit;
    private RecyclerView rvHabits;

    private int waterCountMl = 1000;
    private int streakCount = 5;
    private boolean isBreathingActive = false;
    private CountDownTimer breathingTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Bind Views
        tvStreak = findViewById(R.id.tv_streak);
        tvWaterCount = findViewById(R.id.tv_water_count);
        tvBreathPrompt = findViewById(R.id.tv_breath_prompt);
        btnStartBreath = findViewById(R.id.btn_start_breath);
        btnWaterMinus = findViewById(R.id.btn_water_minus);
        btnWaterPlus = findViewById(R.id.btn_water_plus);
        etHabitTitle = findViewById(R.id.et_habit_title);
        btnAddHabit = findViewById(R.id.btn_add_habit);
        rvHabits = findViewById(R.id.rv_habits);

        // Prepopulate database if empty for demo
        habitList = dbHelper.getAllHabits();
        if (habitList.isEmpty()) {
            dbHelper.addHabit("Drink warm lemon water");
            dbHelper.addHabit("5-min mindful breathing stretch");
            dbHelper.addHabit("Journal 3 things I'm grateful for");
            dbHelper.addHabit("Read 10 pages of my book");
            habitList = dbHelper.getAllHabits();
        }

        // Setup RecyclerView
        habitAdapter = new HabitAdapter(habitList, this);
        rvHabits.setLayoutManager(new LinearLayoutManager(this));
        rvHabits.setAdapter(habitAdapter);

        // Update UI Info
        updateWaterUi();
        updateStreakUi();

        // Water Click Handlers
        btnWaterPlus.setOnClickListener(v -> {
            waterCountMl += 250;
            updateWaterUi();
        });

        btnWaterMinus.setOnClickListener(v -> {
            if (waterCountMl >= 250) {
                waterCountMl -= 250;
                updateWaterUi();
            }
        });

        // Add Habit Handler
        btnAddHabit.setOnClickListener(v -> {
            String title = etHabitTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                dbHelper.addHabit(title);
                etHabitTitle.setText("");
                refreshHabits();
                Toast.makeText(MainActivity.this, "Habit added: " + title, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a habit title", Toast.LENGTH_SHORT).show();
            }
        });

        // Breathing Exercise countdown timer
        btnStartBreath.setOnClickListener(v -> {
            if (isBreathingActive) {
                stopBreathingExercise();
            } else {
                startBreathingExercise();
            }
        });
    }

    private void updateWaterUi() {
        tvWaterCount.setText("Water: " + waterCountMl + " ml");
    }

    private void updateStreakUi() {
        tvStreak.setText("Current Streak: " + streakCount + " days");
    }

    private void refreshHabits() {
        habitList = dbHelper.getAllHabits();
        habitAdapter.updateList(habitList);
    }

    private void startBreathingExercise() {
        isBreathingActive = true;
        btnStartBreath.setText("Stop");
        
        // Use setBackgroundTintList to safely change MaterialButton colors across API levels
        btnStartBreath.setBackgroundTintList(ColorStateList.valueOf(0xFFFF5252)); // holo_red_light hex

        breathingTimer = new CountDownTimer(60000, 4000) {
            private boolean inhale = true;

            @Override
            public void onTick(long millisUntilFinished) {
                long secsLeft = millisUntilFinished / 1000;
                if (inhale) {
                    tvBreathPrompt.setText("Breathe In deeply... (" + secsLeft + "s left)");
                } else {
                    tvBreathPrompt.setText("Breathe Out slowly... (" + secsLeft + "s left)");
                }
                inhale = !inhale;
            }

            @Override
            public void onFinish() {
                tvBreathPrompt.setText("Breath session completed! You are centered.");
                btnStartBreath.setText("Start Breaths");
                btnStartBreath.setBackgroundTintList(null); // Reset to primary/accent
                isBreathingActive = false;
                streakCount++;
                updateStreakUi();
                Toast.makeText(MainActivity.this, "Streak increased! Keep flowing.", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void stopBreathingExercise() {
        if (breathingTimer != null) {
            breathingTimer.cancel();
        }
        tvBreathPrompt.setText("Tap to start a 1-minute breath reset");
        btnStartBreath.setText("Start Breaths");
        btnStartBreath.setBackgroundTintList(null); // Reset to primary/accent
        isBreathingActive = false;
    }

    @Override
    public void onStatusChanged(Habit habit, boolean completed) {
        dbHelper.updateHabitStatus(habit.getId(), completed);
        refreshHabits();
    }

    @Override
    public void onDeleteRequested(Habit habit) {
        dbHelper.deleteHabit(habit.getId());
        refreshHabits();
        Toast.makeText(this, "Habit deleted", Toast.LENGTH_SHORT).show();
    }
}

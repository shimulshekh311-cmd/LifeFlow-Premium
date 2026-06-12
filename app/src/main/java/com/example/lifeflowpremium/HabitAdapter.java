package com.example.lifeflowpremium;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private List<Habit> habitList;
    private OnHabitInteractionListener listener;

    public interface OnHabitInteractionListener {
        void onStatusChanged(Habit habit, boolean completed);
        void onDeleteRequested(Habit habit);
    }

    public HabitAdapter(List<Habit> habitList, OnHabitInteractionListener listener) {
        this.habitList = habitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.tvTitle.setText(habit.getTitle());
        
        // Prevent recursive listener calls during bind
        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(habit.isCompleted());
        
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setCompleted(isChecked);
            listener.onStatusChanged(habit, isChecked);
        });

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteRequested(habit));
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public void updateList(List<Habit> newList) {
        this.habitList = newList;
        notifyDataSetChanged();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbCompleted;
        TextView tvTitle;
        ImageButton btnDelete;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCompleted = itemView.findViewById(R.id.cb_completed);
            tvTitle = itemView.findViewById(R.id.tv_habit_title);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

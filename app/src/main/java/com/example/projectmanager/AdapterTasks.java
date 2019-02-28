package com.example.projectmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterTasks extends RecyclerView.Adapter<AdapterTasks.ViewHolderTasks> {

    ArrayList<Task> taskArrayList;

    public AdapterTasks(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    @NonNull
    @Override
    public ViewHolderTasks onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_card, viewGroup, false);
        return new ViewHolderTasks(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTasks viewHolderTasks, int i) {
        viewHolderTasks.assignData(taskArrayList.get(i));
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public class ViewHolderTasks extends RecyclerView.ViewHolder {

        TextView name, desc, due, totalTime;
        CircularProgressBar bar;

        public ViewHolderTasks(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.taskName);
            desc = itemView.findViewById(R.id.taskDesc);
            due = itemView.findViewById(R.id.dueDate);
            totalTime = itemView.findViewById(R.id.totalTime);
            bar = itemView.findViewById(R.id.circularProgressBar);
        }

        public void assignData(Task task) {
            name.setText(task.getName());
            desc.setText(task.getDesc());

            Date dueDate = task.getDue();
            String pattern = "MM/dd/yyyy";
            DateFormat df = new SimpleDateFormat(pattern);
            String dueString = df.format(dueDate);

            due.setText(dueString);
            int totalSeconds = task.getTotalTime();
            int seconds = totalSeconds % 60;
            int totalMinutes = seconds / 60;
            int minutes = seconds % 60;
            int totalHours = totalMinutes / 60;
//            String elapsedTime = totalHours + ":" + minutes + ":" + seconds;
            String elapsedTime = "00:00:00";
            totalTime.setText(elapsedTime);
            bar.setProgress((float) task.getProgress());
//            bar.setColor();
        }
    }
}

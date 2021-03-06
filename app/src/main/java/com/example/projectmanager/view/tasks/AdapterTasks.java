package com.example.projectmanager.view.tasks;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.model.Task;
import com.example.projectmanager.utils.DateUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Adapter para las tareas del recyclerview.
 */
public class AdapterTasks extends RecyclerView.Adapter<AdapterTasks.ViewHolderTasks> implements View.OnClickListener, View.OnLongClickListener {

    ArrayList<Task> taskArrayList;
    private View.OnClickListener listener;
    private View.OnLongClickListener longListener;

    public AdapterTasks(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    @NonNull
    @Override
    public ViewHolderTasks onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_card, viewGroup, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
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

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        this.longListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (longListener != null) {
            longListener.onLongClick(v);
        }
        return true;
    }

    /**
     * ViewHolder para las tareas.
     */
    public class ViewHolderTasks extends RecyclerView.ViewHolder {

        TextView name, desc, due, expected;
        CircularProgressBar bar;

        public ViewHolderTasks(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.taskName);
            desc = itemView.findViewById(R.id.taskDesc);
            due = itemView.findViewById(R.id.dueDate);
            expected = itemView.findViewById(R.id.totalTime);
            bar = itemView.findViewById(R.id.circularProgressBar);
        }

        public void assignData(Task task) {
            name.setText(task.getName());
            String taskDesc = task.getDesc();
            if (taskDesc != null) {
                desc.setText(taskDesc);
            } else {
                desc.setText("");
            }

            Date dueDate = task.getDue();
            if (dueDate != null) {
                due.setText(DateUtils.toString(dueDate));
            } else {
                due.setText("");
            }

            expected.setText(Double.toString(task.getExpected()));
            bar.setProgress((float) task.getProgress());
        }
    }
}

package com.example.projectmanager.view.worktime;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.model.WorkSession;
import com.example.projectmanager.utils.DateUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterWork extends RecyclerView.Adapter<AdapterWork.ViewHolderWork> implements View.OnClickListener, View.OnLongClickListener {

    ArrayList<WorkSession> workSessionArrayList;
    private View.OnClickListener listener;
    private View.OnLongClickListener longListener;

    public AdapterWork(ArrayList<WorkSession> workSessionArrayList) {
        this.workSessionArrayList = workSessionArrayList;
    }

    @NonNull
    @Override
    public AdapterWork.ViewHolderWork onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.work_card, viewGroup, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolderWork(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterWork.ViewHolderWork viewHolderWork, int i) {
        viewHolderWork.assignData(workSessionArrayList.get(i));
    }

    @Override
    public int getItemCount() {
        return workSessionArrayList.size();
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

    public class ViewHolderWork extends RecyclerView.ViewHolder {
        TextView date, time;

        public ViewHolderWork(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.workDate);
            time = itemView.findViewById(R.id.workTime);
        }

        public void assignData(WorkSession workSession) {
            date.setText(DateUtils.toString(workSession.getDate()));
            time.setText(Double.toString(workSession.getTime()));
        }
    }
}

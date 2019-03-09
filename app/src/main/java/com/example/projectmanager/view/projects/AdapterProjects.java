package com.example.projectmanager.view.projects;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.model.Project;

import java.util.ArrayList;

public class AdapterProjects extends RecyclerView.Adapter<AdapterProjects.ViewHolderProjects> implements View.OnClickListener {

    ArrayList<Project> projectArrayList;
    private View.OnClickListener listener;

    public AdapterProjects(ArrayList<Project> projectArrayList) {
        this.projectArrayList = projectArrayList;
    }

    @NonNull
    @Override
    public ViewHolderProjects onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.project_card, null);
        view.setOnClickListener(this);
        return new ViewHolderProjects(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProjects viewHolderProjects, int i) {
        viewHolderProjects.assignData(projectArrayList.get(i));
    }

    @Override
    public int getItemCount() {
        return projectArrayList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }

    }

    public class ViewHolderProjects extends RecyclerView.ViewHolder {

        TextView name, desc;

        public ViewHolderProjects(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.proyectName);
            desc = itemView.findViewById(R.id.proyectDescription);
        }

        public void assignData(Project project) {
            name.setText(project.getName());
            desc.setText(project.getDesc());
        }
    }
}

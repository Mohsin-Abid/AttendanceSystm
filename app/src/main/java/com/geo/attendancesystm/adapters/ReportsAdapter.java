package com.geo.attendancesystm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.attendancesystm.R;
import com.geo.attendancesystm.model.classes.pojo.ReportModel;

import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.Holder> {
    List<ReportModel> list;
    Context context;

    public ReportsAdapter(List<ReportModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_report_layout, parent, false);
        return  new ReportsAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ReportModel reports = list.get(position);
        holder.seekBar.setProgress(reports.getPresent());
        holder.seekBar.setMax(reports.getPresent()+reports.getAbsent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView teacher,subject;
        CircularSeekBar seekBar;
        public Holder(@NonNull View itemView) {
            super(itemView);
            teacher = itemView.findViewById(R.id.teacher);
            subject = itemView.findViewById(R.id.subject);
            seekBar = itemView.findViewById(R.id.seekbar);
        }
    }
}

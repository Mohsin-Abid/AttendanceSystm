package com.geo.attendancesystm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.geo.attendancesystm.Interfaces.ClickListenerInterface;
import com.geo.attendancesystm.R;
import com.geo.attendancesystm.activities.LecturesActivity;
import com.geo.attendancesystm.model.classes.pojo.Lectures;

import java.util.List;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.Holder>
{
    Context context;
    List<Lectures> list;
    ClickListenerInterface clickListenerInterface;

    public LectureAdapter(Context context, List<Lectures> list, ClickListenerInterface clickListenerInterface) {
        this.context = context;
        this.list = list;
        this.clickListenerInterface = clickListenerInterface;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lectures_view_design, parent, false);
        return new LectureAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Lectures lectures = list.get(position);
        if (lectures.getMarked()!=null && lectures.getMarked()){
            holder.view1.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
        holder.subject.setText(lectures.getAssignsubject());
        holder.teacher.setText(lectures.getAssignclass());
        holder.startTime.setText(lectures.getStarttime());
        holder.endTime.setText(lectures.getEndtime());
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder
    {
        TextView subject,startTime,endTime,teacher;
        CardView cardLecture;
        View view1;
        public Holder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            teacher = itemView.findViewById(R.id.teacher);
            cardLecture = itemView.findViewById(R.id.cardLecture);
            view1 = itemView.findViewById(R.id.view1);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    clickListenerInterface.oniItemClick(getAdapterPosition());
                }
            });
        }
    }
}

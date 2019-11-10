package com.lpu.capstone.smartselfattendance.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lpu.capstone.smartselfattendance.R;
import com.lpu.capstone.smartselfattendance.model.LocalAttendanceModel;

import java.util.List;

public class ViewAttendanceAdapter extends RecyclerView.Adapter<ViewAttendanceAdapter.MyViewHolder> {

    private List<LocalAttendanceModel> attendanceModelList;
    private Context context;

    public ViewAttendanceAdapter(List<LocalAttendanceModel> attendanceModelList, Context context) {
        this.attendanceModelList = attendanceModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.attendance_view,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(itemview);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LocalAttendanceModel attendanceModel = attendanceModelList.get(position);
        holder.status.setText(attendanceModel.getStatus());
        if(attendanceModel.getStatus().equals("Present")){
            holder.status.setTextColor(Color.parseColor("#1EA308"));
        }
        else if(attendanceModel.getStatus().equals("Absent")){
            holder.status.setTextColor(Color.parseColor("#E00707"));
        }
        holder.time.setText("Time: "+attendanceModel.getTime());
        holder.date.setText("Date: "+attendanceModel.getDate());
    }

    @Override
    public int getItemCount() {
        return attendanceModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date, time, status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.txtdate);
            time = (TextView)itemView.findViewById(R.id.txttime);
            status = (TextView)itemView.findViewById(R.id.txtstatus);
        }
    }
}

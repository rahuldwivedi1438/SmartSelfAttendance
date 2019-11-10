package com.lpu.capstone.smartselfattendance.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lpu.capstone.smartselfattendance.ListingActivity;
import com.lpu.capstone.smartselfattendance.Main3Activity;
import com.lpu.capstone.smartselfattendance.MarkAttendanceActivity;
import com.lpu.capstone.smartselfattendance.R;
import com.lpu.capstone.smartselfattendance.ReauthenticationActivity;
import com.lpu.capstone.smartselfattendance.ViewAttendance1Activity;
import com.lpu.capstone.smartselfattendance.ViewAttendance2Activity;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.MyViewHolder> {

    private Context context;
    private List<String> optionList;

    public OptionAdapter(Context context, List<String> optionList) {
        this.context = context;
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.options_layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(itemview);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final String option = optionList.get(position);
        holder.optionString.setText(option);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(option.contains("View Faculty List"))
                {
                    intent = new Intent(context, ListingActivity.class);
                    intent.putExtra("title","List of Faculty");
                    intent.putExtra("level","2");

                }else if( option.contains("View Student List")){

                    intent = new Intent(context, ListingActivity.class);
                    intent.putExtra("title","List of Students");
                    intent.putExtra("level","3");
                }
                else if(option.contains("View Student's Attendance")){
                    intent = new Intent(context,ViewAttendance1Activity.class);

                }else{
                    intent = new Intent(context, ReauthenticationActivity.class);
                    intent.putExtra("option",option);
                    intent.putExtra("adminpassword","0");
                }
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView optionString;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            optionString = (TextView)itemView.findViewById(R.id.txtoption);
        }
    }
}

package com.lpu.capstone.smartselfattendance.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lpu.capstone.smartselfattendance.ModifyStudentActivity;
import com.lpu.capstone.smartselfattendance.ModifyTeacherActivity;
import com.lpu.capstone.smartselfattendance.R;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder> {

    private Context context;
    private List<User> student_faculty_list;

    public ListingAdapter(Context context, List<User> student_faculty_list) {
        this.context = context;
        this.student_faculty_list = student_faculty_list;
        Log.d("sonu", "ListingAdapter: got: "+student_faculty_list.size());
    }

    @NonNull
    @Override
    public ListingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.details_list_view,parent,false);
        ListingAdapter.MyViewHolder myViewHolder = new ListingAdapter.MyViewHolder(itemview);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListingAdapter.MyViewHolder holder, int position) {

        final User user = student_faculty_list.get(position);
        holder.name.setText(user.getName());
        holder.id.setText(user.getId());

        if(user.getImageLink().equals("default"))
        {
            holder.profileImage.setImageResource(R.drawable.ic_person);
        }else
        {
            Glide.with(context.getApplicationContext()).load(user.getImageLink()).into(holder.profileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (user.getLevel()){
                    case "3":
                        Intent intent = new Intent(context, ModifyStudentActivity.class);
                        intent.putExtra("id",user.getId());
                        context.startActivity(intent);
                        break;
                    case "2":
                        Intent intent1 = new Intent(context, ModifyTeacherActivity.class);
                        intent1.putExtra("id",user.getId());
                        context.startActivity(intent1);
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return student_faculty_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView name,id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView)itemView.findViewById(R.id.txtid);
            name = (TextView)itemView.findViewById(R.id.txtname);
            profileImage = (CircleImageView)itemView.findViewById(R.id.profile_image);
        }
    }
}

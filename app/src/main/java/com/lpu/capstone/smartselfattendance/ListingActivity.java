package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lpu.capstone.smartselfattendance.adapters.ListingAdapter;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ListingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner section, subject;
    private ArrayList<String> subjectList = ProjectUtils.setSubjectList();
    private ArrayList<String> sectionList = ProjectUtils.setSectionList();
    private RecyclerView recyclerView;
    private List<User> student_faculty_list;
    private ListingAdapter adapter;
    private String level;
    private DotProgressBar progressBar;
    private Button showBtn;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        section = findViewById(R.id.txtsection);
        subject = findViewById(R.id.txtsubject);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        progressBar = findViewById(R.id.progressbar);
        showBtn = findViewById(R.id.btn_show);
        error = findViewById(R.id.txterror);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        student_faculty_list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,subjectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter);
        subject.setSelection(0);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sectionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        section.setAdapter(adapter1);
        section.setSelection(0);
        level = getIntent().getStringExtra("level");
        if(level.equals("2")){
            section.setVisibility(View.GONE);
            subject.setVisibility(View.VISIBLE);
        }
        if(level.equals("3")){
            section.setVisibility(View.VISIBLE);
            subject.setVisibility(View.GONE);
        }

    }

    private void setList() {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                student_faculty_list.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    assert user!=null;
                    if(level.equals("2")){

                        if(user.getLevel().equals(level)&& user.getSubject().equals(subject.getSelectedItem().toString().trim()))
                        {
                            student_faculty_list.add(user);
                        }
                    }
                    if(level.equals("3")){

                        if(user.getLevel().equals(level)&& user.getSection().equals(section.getSelectedItem().toString().trim()))
                        {
                            student_faculty_list.add(user);
                        }
                    }
                }
                if(student_faculty_list.size()==0){
                    //Toast.makeText(ListingActivity.this,"No Data found",Toast.LENGTH_SHORT).show();
                    error.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else{
                    error.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter = new ListingAdapter(ListingActivity.this,student_faculty_list);
                recyclerView.setAdapter(adapter);
                showBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void showClicked(View view) {
        if(Validate()){
            showBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            setList();
        }
    }

    private boolean Validate() {
        boolean valid = true;
        if(level.equals("2") && subjectList.indexOf(subject.getSelectedItem().toString().trim())<1){
            valid = false;
        }
        if(level.equals("3") && sectionList.indexOf(section.getSelectedItem().toString().trim())<1){
            valid = false;
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

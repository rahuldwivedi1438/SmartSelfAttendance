package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lpu.capstone.smartselfattendance.adapters.ViewAttendanceAdapter;
import com.lpu.capstone.smartselfattendance.model.Attendance;
import com.lpu.capstone.smartselfattendance.model.AttendanceDetails;
import com.lpu.capstone.smartselfattendance.model.LocalAttendanceModel;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendance3Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<LocalAttendanceModel> attendanceList;
    private TextView section;
    private Spinner subject;
    private ViewAttendanceAdapter adapter;
    private List<String> subjectList = ProjectUtils.setSubjectList();
    private Button showBtn;
    private DotProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance3);
        toolbar  = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progressbar);
        showBtn = findViewById(R.id.btn_show);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detailed Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        subject = findViewById(R.id.subject_chooser);
        section = findViewById(R.id.txtsection);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,subjectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter);
        subject.setSelection(0);
        setScreen();

    }

    private void setScreen() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Getting Data");
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user!=null;

                if(ProjectUtils.setSectionList().contains(user.getSection())){
                    section.setText(user.getSection());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
        dialog.dismiss();
    }

    public void showClicked(View view) {
        if(subject.getSelectedItemPosition()==0){
            Toast.makeText(getApplicationContext(),"Select Subject",Toast.LENGTH_SHORT).show();
            return;
        }

        showBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Details");
        progressDialog.show();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attendanceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Attendance attendance = snapshot.getValue(Attendance.class);
                    assert attendance != null;
                    if (attendance.getSection().equals(section.getText().toString().trim()) && attendance.getSubject().equals(subject.getSelectedItem().toString().trim()))
                    {
                        LocalAttendanceModel attendanceModel = new LocalAttendanceModel();
                        attendanceModel.setDate(attendance.getLectureDate());
                        attendanceModel.setTime(attendance.getLectureTime());
                       List<AttendanceDetails> attendanceDetailsList = attendance.getDetailsList();
                       for(AttendanceDetails details: attendanceDetailsList){
                           if(details.getFid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                               attendanceModel.setFid(details.getFid());
                               attendanceModel.setId(details.getId());
                               attendanceModel.setName(details.getName());
                               attendanceModel.setStatus(details.getStatus());
                           }
                       }

                       attendanceList.add(attendanceModel);
                    }
                }
                if(attendanceList.size()==0) Toast.makeText(getApplicationContext(),"Nothing Found",Toast.LENGTH_SHORT).show();
                adapter = new ViewAttendanceAdapter(attendanceList,ViewAttendance3Activity.this);
                recyclerView.setAdapter(adapter);
                showBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();
    }
}

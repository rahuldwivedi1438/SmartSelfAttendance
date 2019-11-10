package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ViewAttendance2Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<LocalAttendanceModel> attendanceList;
    private TextView subject;
    private EditText idTxt;
    private ViewAttendanceAdapter adapter;
    private Button showBtn;
    private DotProgressBar progressBar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance2);
        toolbar  = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        subject = findViewById(R.id.txtsubject);
        idTxt = findViewById(R.id.txtid);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressbar);
        showBtn = findViewById(R.id.btn_show);
        getSupportActionBar().setTitle("Detailed Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceList = new ArrayList<>();
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

                if(ProjectUtils.setSubjectList().contains(user.getSubject())){
                    subject.setText(user.getSubject());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
        dialog.dismiss();
    }

    public void showClicked(View view) {
        if(TextUtils.isEmpty(idTxt.getText().toString())){
            idTxt.setError("required");
            return;
        }
        else{
            id = idTxt.getText().toString().trim();
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
                    if (attendance.getSubject().equals(subject.getText().toString().trim()))
                    {
                       if(studentWasInThisAttendance(attendance.getDetailsList())){
                           LocalAttendanceModel attendanceModel = new LocalAttendanceModel();
                           attendanceModel.setDate(attendance.getLectureDate());
                           attendanceModel.setTime(attendance.getLectureTime());
                           List<AttendanceDetails> attendanceDetailsList = attendance.getDetailsList();
                           for(AttendanceDetails details: attendanceDetailsList){
                               if(details.getId().equals(id)){
                                   attendanceModel.setFid(details.getFid());
                                   attendanceModel.setId(details.getId());
                                   attendanceModel.setName(details.getName());
                                   attendanceModel.setStatus(details.getStatus());
                               }
                           }

                           attendanceList.add(attendanceModel);
                       }
                    }
                }

                if(attendanceList.size()==0) Toast.makeText(getApplicationContext(),"Can't fetch Data",Toast.LENGTH_SHORT).show();
                adapter = new ViewAttendanceAdapter(attendanceList,ViewAttendance2Activity.this);
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

    private boolean studentWasInThisAttendance(List<AttendanceDetails> attendanceDetailsList){
        boolean isCorrect = false;
        for(AttendanceDetails details: attendanceDetailsList){
            if(details.getId().equals(id)){
                isCorrect = true;
            }
        }
        return isCorrect;
    }
}

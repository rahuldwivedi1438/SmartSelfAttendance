package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lpu.capstone.smartselfattendance.adapters.ListingAdapter;
import com.lpu.capstone.smartselfattendance.model.Attendance;
import com.lpu.capstone.smartselfattendance.model.AttendanceDetails;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TakeAttendanceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner lectureTimeChooser,sectionChooser,subjectChooser;
    private TextView lectureDateChooser,otpText;
    private List<String> lectureTimeList = ProjectUtils.setLectureTimingList();
    private List<String> sectionList = ProjectUtils.setSectionList();
    private List<String> subjectList = ProjectUtils.setSubjectList();
    private DatabaseReference attendanceReference;
    private Calendar calendar;
    private List<AttendanceDetails> studentAttendanceDetailsList = new ArrayList<>();
    private List<AttendanceDetails> previousStudentAttendanceDetailsList = new ArrayList<>();
    private int otp;
    private Button qrBtn,doneBtn,otpBtn,saveBtn;
    private String attendanceFid = null;
    private boolean attendancePreviouslyExisted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        toolbar = findViewById(R.id.toolbar);
        lectureDateChooser = findViewById(R.id.lecturedate_chooser);
        lectureTimeChooser = findViewById(R.id.lecturetime_chooser);
        sectionChooser = findViewById(R.id.section_chooser);
        subjectChooser = findViewById(R.id.subject_chooser);
        otpBtn = findViewById(R.id.btn_otp);
        otpText = findViewById(R.id.txtotp);
        saveBtn = findViewById(R.id.btn_save);
        qrBtn = findViewById(R.id.btn_qr);
        doneBtn = findViewById(R.id.btn_done);
        calendar = Calendar.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Take Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,lectureTimeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lectureTimeChooser.setAdapter(adapter);
        lectureTimeChooser.setSelection(0);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sectionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionChooser.setAdapter(adapter1);
        sectionChooser.setSelection(0);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,subjectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectChooser.setAdapter(adapter2);
        subjectChooser.setSelection(0);
    }

    public void saveClicked(View view) {
        if(validDetails()){
            saveBtn.setVisibility(View.INVISIBLE);
            attendanceAlreadyExists();
            getStudentsList();
        }
    }

    private void attendanceAlreadyExists() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Details");
        progressDialog.show();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Attendance attendance = snapshot.getValue(Attendance.class);
                    assert attendance != null;
                    if (attendance.getLectureTime().equals(lectureTimeChooser.getSelectedItem().toString())
                            && attendance.getLectureDate().equals(lectureDateChooser.getText().toString().trim())
                            && attendance.getSection().equals(sectionChooser.getSelectedItem().toString().toString())
                            && attendance.getSubject().equals(subjectChooser.getSelectedItem().toString())) {
                        attendanceFid = attendance.getAfID();
                        previousStudentAttendanceDetailsList = attendance.getDetailsList();
                    }
                }

                if(attendanceFid==null){
                    attendanceFid = UUID.randomUUID().toString();
                    attendancePreviouslyExisted = false;
                }
                attendanceReference = FirebaseDatabase.getInstance().getReference("Attendance").child(attendanceFid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        progressDialog.dismiss();
    }

    private boolean validDetails() {
        boolean valid= true;
        if(sectionChooser.getSelectedItemPosition()<1 || subjectChooser.getSelectedItemPosition()<1 ||
        lectureTimeChooser.getSelectedItemPosition()<1){
            Toast.makeText(getApplicationContext(),"Fill All details",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(lectureDateChooser.getText().toString().trim().equals("Select Date")){
            Toast.makeText(getApplicationContext(),"Fill All details",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private void getStudentsList() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Lecture Details");
        dialog.show();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentAttendanceDetailsList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    assert user!=null;
                        if(user.getLevel().equals("3")&& user.getSection().equals(sectionChooser.getSelectedItem().toString().trim()))
                        {
                            AttendanceDetails attendanceDetails = new AttendanceDetails(user.getId(),user.getName(),user.getfID(),"Absent");
                            studentAttendanceDetailsList.add(attendanceDetails);
                        }
                }
                dialog.dismiss();
                addAttendance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        dialog.dismiss();
        
    }

    private void addAttendance() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving Lecture Details");
        dialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("afID",attendanceFid);
        map.put("section",sectionChooser.getSelectedItem().toString().trim());
        map.put("subject",subjectChooser.getSelectedItem().toString().trim());
        map.put("lectureDate",lectureDateChooser.getText().toString().trim());
        map.put("lectureTime",lectureTimeChooser.getSelectedItem().toString().trim());
        if(previousStudentAttendanceDetailsList.size()==0){
            map.put("detailsList",studentAttendanceDetailsList);
        }else{
            map.put("detailsList",previousStudentAttendanceDetailsList);
        }
        otp = new Random().nextInt(900000-800000)+800000;
        map.put("otp",String.valueOf(otp));
        qrBtn.setVisibility(View.VISIBLE);
        otpBtn.setVisibility(View.VISIBLE);
        attendanceReference.updateChildren(map);
        dialog.dismiss();
    }

    public void setDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.PickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                String s = ""+date+"/"+month+"/"+year;
                lectureDateChooser.setText(s);
            }
        },calendar.get(Calendar.DATE),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR));
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE,0);
        datePickerDialog.getDatePicker().setMinDate(calendar1.getTimeInMillis());
        datePickerDialog.show();
    }

    public void qrClicked(View view) {
        qrBtn.setVisibility(View.GONE);
        otpBtn.setVisibility(View.GONE);
        Intent intent = new Intent(TakeAttendanceActivity.this,QRcodeActivity.class);
        intent.putExtra("otp",String.valueOf(otp));
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== RESULT_OK){
            resetOtp();
            saveBtn.setVisibility(View.VISIBLE);
        }
    }

    public void otpClicked(View view) {
        otpBtn.setVisibility(View.GONE);
        otpText.setVisibility(View.VISIBLE);
        otpText.setText(String.valueOf(otp));
        qrBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.VISIBLE);
    }

    public void doneClicked(View view) {
       saveBtn.setVisibility(View.VISIBLE);
        otpText.setVisibility(View.GONE);
        doneBtn.setVisibility(View.GONE);
        resetOtp();

    }

    private void resetOtp() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(attendanceFid);
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Marking Attendance");
        dialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("otp", "000000");
        reference.updateChildren(map);
        dialog.dismiss();
    }
}

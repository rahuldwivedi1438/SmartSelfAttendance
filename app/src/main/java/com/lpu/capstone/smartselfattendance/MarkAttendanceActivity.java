package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lpu.capstone.smartselfattendance.model.Attendance;
import com.lpu.capstone.smartselfattendance.model.AttendanceDetails;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MarkAttendanceActivity extends AppCompatActivity {

    private TextView lectureDateChooser, useless, sectionChooser;
    private Toolbar toolbar;
    private Spinner lectureTimeChooser, subjectChooser;
    private Calendar calendar;
    private String attendanceFid;
    private Button qrBtn, otpBtn, submitBtn;
    private LinearLayout otpLayout;
    private EditText otpTxt;
    private List<AttendanceDetails> attendanceDetailsList = new ArrayList<>();
    private List<String> lectureTimeList = ProjectUtils.setLectureTimingList();
    private List<String> sectionList = ProjectUtils.setSectionList();
    private List<String> subjectList = ProjectUtils.setSubjectList();
    private String otpQR;
    private boolean isAttendanceOpen = true;
    private Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);
        toolbar = findViewById(R.id.toolbar);
        lectureDateChooser = findViewById(R.id.lecturedate_chooser);
        lectureTimeChooser = findViewById(R.id.lecturetime_chooser);
        sectionChooser = findViewById(R.id.section_chooser);
        subjectChooser = findViewById(R.id.subject_chooser);
        calendar = Calendar.getInstance();
        otpTxt = findViewById(R.id.txtotp);
        qrBtn = findViewById(R.id.btn_qr);
        otpBtn = findViewById(R.id.btn_otp);
        submitBtn = findViewById(R.id.btn_submit);
        otpLayout = findViewById(R.id.otp_layout);
        useless = findViewById(R.id.txt_useless);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mark Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lectureTimeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lectureTimeChooser.setAdapter(adapter);
        lectureTimeChooser.setSelection(0);
        loading = new Dialog(this);
        loading.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.loading_view,null);
        loading.setContentView(view);
       /* ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sectionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionChooser.setAdapter(adapter1);
        sectionChooser.setSelection(0);*/
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, subjectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectChooser.setAdapter(adapter2);
        subjectChooser.setSelection(0);
        getSection();
    }

    private void getSection() {
        final Dialog loading = new Dialog(this);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        loading.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.loading_view, null);
        loading.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loading.create();
        }
        loading.show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                sectionChooser.setText(user.getSection());
                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Can't fetch data", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        });
    }

    public void qrClicked(View view) {
        if (Validate()) {
            getAttendanceDetails();
                startActivityForResult(new Intent(MarkAttendanceActivity.this, ScanQRActivity.class), 1);
        }
    }

    public void otpClicked(View view) {
        if (Validate()) {
            getAttendanceDetails();
                otpLayout.setVisibility(View.VISIBLE);

        }
    }

    private boolean Validate() {
        boolean valid = true;
        if (subjectChooser.getSelectedItemPosition() < 1 ||
                lectureTimeChooser.getSelectedItemPosition() < 1) {
            Toast.makeText(getApplicationContext(), "Fill All details", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (lectureDateChooser.getText().toString().trim().equals("Select Date")) {
            Toast.makeText(getApplicationContext(), "Fill All details", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (sectionChooser.getText().toString().trim().equals("Section")) {
            Toast.makeText(getApplicationContext(), "Fill All details", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private void getAttendanceDetails() {
        loading.show();
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
                            && attendance.getSection().equals(sectionChooser.getText().toString().toString())
                            && attendance.getSubject().equals(subjectChooser.getSelectedItem().toString())) {
                        attendanceFid = attendance.getAfID();
                    }
                }

                if (attendanceFid == null) {
                    isAttendanceOpen = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();
        loading.dismiss();
    }

    public void submitClicked(View view) {
        if (TextUtils.isEmpty(otpTxt.getText())) {
            otpTxt.setError("Required");
            return;
        }
        if (attendanceFid != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Getting Details");
            progressDialog.show();
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(attendanceFid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Attendance attendance = dataSnapshot.getValue(Attendance.class);
                    assert attendance != null;
                    attendanceDetailsList = attendance.getDetailsList();
                    String attendanceOTP = attendance.getOtp();
                    if (otpTxt.getText().toString().trim().equals(attendanceOTP)) {

                        int attendanceIndex = 0;
                        for (int i = 0; i < attendanceDetailsList.size(); i++) {
                            AttendanceDetails attendanceDetails = attendanceDetailsList.get(i);
                            if (attendanceDetails.getFid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                attendanceIndex = i;
                            }
                        }
                        List<AttendanceDetails> newAttendanceDetailsList = attendanceDetailsList;
                        AttendanceDetails attendanceDetails = attendanceDetailsList.get(attendanceIndex);
                        attendanceDetails.setStatus("Present");
                        newAttendanceDetailsList.set(attendanceIndex, attendanceDetails);
                        markAttendance(newAttendanceDetailsList);
                    } else {
                        Toast.makeText(getApplicationContext(), "Attendance Can't be marked", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            progressDialog.dismiss();
        } else {
            Toast.makeText(getApplicationContext(), "Attendance Not started yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void markAttendance(List<AttendanceDetails> newAttendanceDetailsList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(attendanceFid);
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Marking Attendance");
        dialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("detailsList", newAttendanceDetailsList);
        reference.updateChildren(map);
        dialog.dismiss();
        otpLayout.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "Attendance Marked", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void setDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.PickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                String s = "" + date + "/" + month + "/" + year;
                lectureDateChooser.setText(s);
            }
        }, calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, 0);
        datePickerDialog.getDatePicker().setMinDate(calendar1.getTimeInMillis());
        datePickerDialog.show();
    }

    private void markAttendanceByQR(final String data) {
        if (attendanceFid != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Getting Details");
            progressDialog.show();
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(attendanceFid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Attendance attendance = dataSnapshot.getValue(Attendance.class);
                    assert attendance != null;
                    attendanceDetailsList = attendance.getDetailsList();
                    String attendanceOTP = attendance.getOtp();
                    if (data.equals(attendanceOTP)) {

                        int attendanceIndex = 0;
                        for (int i = 0; i < attendanceDetailsList.size(); i++) {
                            AttendanceDetails attendanceDetails = attendanceDetailsList.get(i);
                            if (attendanceDetails.getFid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                attendanceIndex = i;
                            }
                        }
                        List<AttendanceDetails> newAttendanceDetailsList = attendanceDetailsList;
                        AttendanceDetails attendanceDetails = attendanceDetailsList.get(attendanceIndex);
                        attendanceDetails.setStatus("Present");
                        newAttendanceDetailsList.set(attendanceIndex, attendanceDetails);
                        progressDialog.dismiss();
                        markAttendance(newAttendanceDetailsList);
                    } else {
                        Toast.makeText(getApplicationContext(), "Attendance Can't be marked", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            progressDialog.dismiss();
        } else {
            Toast.makeText(getApplicationContext(), "Attendance Not started yet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            otpQR = data.getStringExtra("otp");
            markAttendanceByQR(otpQR);
        }
    }

}

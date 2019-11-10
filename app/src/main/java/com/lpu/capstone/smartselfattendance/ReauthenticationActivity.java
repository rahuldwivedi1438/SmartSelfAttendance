package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReauthenticationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText password;
    private Button reset;
    private TextView errorTxt;
    private String adminPassword;
    private String option;
    private DotProgressBar progressBar;
    private Button varifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reauthentication);
        password = findViewById(R.id.adminpasswordtxt);
        errorTxt = findViewById(R.id.txterror);
        reset  = findViewById(R.id.btn_resetpassword);
        adminPassword = getIntent().getStringExtra("adminpassword");
        option = getIntent().getStringExtra("option");
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressbar);
        varifyBtn = findViewById(R.id.btn_varify);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Varify Yourself");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private Intent createIntent() {
        Intent tempIntent = null;

        switch (option){
            case "Add New Student":
                tempIntent = new Intent(ReauthenticationActivity.this, AddStudentActivity.class);
                break;
            case "Modify Student Data":
                tempIntent = new Intent(ReauthenticationActivity.this, TakeIdActivity.class);
                break;
            case "Add New Faculty":
                tempIntent = new Intent(ReauthenticationActivity.this, AddTeacherActivity.class);
                break;
            case "Modify Faculty Data":
                tempIntent = new Intent(ReauthenticationActivity.this, TakeIdActivity.class);
                break;
            case "Delete Student's Account":
                tempIntent = new Intent(ReauthenticationActivity.this, DeleteStudentAccount.class);
                break;
            case "Delete Faculty's Account":
                tempIntent = new Intent(ReauthenticationActivity.this, DeleteFacultyAccount.class);
                break;
        }

        return tempIntent;
    }

    public void varifyClicked(View view){
        varifyBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        adminPassword = password.getText().toString().trim();
        FirebaseUser fireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(fireBaseUser.getEmail(), adminPassword);
        fireBaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> reAuthenticateTask) {
                if (!reAuthenticateTask.isSuccessful()){
                    errorTxt.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    varifyBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    Intent intent = createIntent();
                    intent.putExtra("adminpassword",adminPassword);
                    intent.putExtra("option",option);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public void resetClicked(View view){
        startActivity(new Intent(ReauthenticationActivity.this, ResetPasswordActivity.class));
        finish();
    }
}

package com.lpu.capstone.smartselfattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

public class TakeIdActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText id;
    private String adminPassword;
    private String option;
    private DotProgressBar progressBar;
    private Button modifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_id);
        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        modifyBtn = findViewById(R.id.btn_modify);
        progressBar = findViewById(R.id.progressbar);
        id = findViewById(R.id.txtid);
        getSupportActionBar().setTitle("Modify Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adminPassword = getIntent().getStringExtra("adminpassword");
        option = getIntent().getStringExtra("option");

    }

    public void ModifyClicked(View view) {
        modifyBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = createIntent();
        if(intent==null){
            modifyBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else{
            intent.putExtra("adminpassword",adminPassword);
            intent.putExtra("id",id.getText().toString());
            startActivity(intent);
            finish();
        }
    }

    private Intent createIntent() {
        Intent tempIntent = null;

        switch (option){
            case "Modify Student Data":
                tempIntent = new Intent(TakeIdActivity.this, ModifyStudentActivity.class);
                break;
            case "Modify Faculty Data":
                tempIntent = new Intent(TakeIdActivity.this, ModifyTeacherActivity.class);
                break;
        }

        return tempIntent;
    }
}

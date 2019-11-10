package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.lpu.capstone.smartselfattendance.adapters.OptionAdapter;

import java.util.List;

public class Main3Activity extends AppCompatActivity {

  private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Smart Self Attendance");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Main3Activity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;
            case R.id.profile_menu: {
                Intent intent = new Intent(Main3Activity.this, NonEditableProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("source","mainpage");
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    public void viewAttendanceClicked(View view) {
        startActivity(new Intent(Main3Activity.this,ViewAttendance3Activity.class));
    }

    public void markAttendanceClicked(View view) {
        startActivity(new Intent(Main3Activity.this,MarkAttendanceActivity.class));
    }
}

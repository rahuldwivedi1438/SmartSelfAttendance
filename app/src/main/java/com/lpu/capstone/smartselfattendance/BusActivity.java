package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lpu.capstone.smartselfattendance.model.User;

public class BusActivity extends AppCompatActivity {
    private String level;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        user = FirebaseAuth.getInstance().getCurrentUser();
        getLevel();

    }

    private void getLevel() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user!=null;
                if(user!=null)
                {
                    level = user.getLevel();
                    Intent intent = createIntent(user.getLevel());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ProjectUtils.setLevel(user.getLevel(),getApplicationContext());
                    startActivity(intent);
                    finish();
                }else{
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(BusActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });


    }

    private Intent createIntent(String level) {
        Intent tempIntent = null;
        if(level.equals("1")){
            tempIntent = new Intent(BusActivity.this,MainActivity.class);
        }
        else if(level.equals("2")){
            tempIntent = new Intent(BusActivity.this,Main2Activity.class);
        }else if(level.equals("3")){
            tempIntent = new Intent(BusActivity.this,Main3Activity.class);
        }
        return tempIntent;
    }
}

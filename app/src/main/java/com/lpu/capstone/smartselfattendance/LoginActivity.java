package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.HashMap;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private CheckBox checkBox;
    private final String credentials = "cred";
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private FirebaseUser user;
    private DotProgressBar progressBar;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTxtUsername);
        password = findViewById(R.id.editTxtPassword);
        checkBox = findViewById(R.id.checkbox);
        progressBar = findViewById(R.id.progressbar);
        loginBtn = findViewById(R.id.btn_login);
        progressBar.setVisibility(View.GONE);
    }

    public void login(View view) {
        if (validateForm()) {
            loginBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            methodSignin(email.getText().toString().trim(), password.getText().toString().trim());
        }
    }

    private boolean validateForm() {
        boolean state = true;
        String s1 = email.getText().toString().trim();
        String s2 = password.getText().toString().trim();
        if (TextUtils.isEmpty(s1)) {
            email.setError("Required");
            state = false;
        }
        if (TextUtils.isEmpty(s2)) {
            password.setError("Required");
            state = false;
        }
        return state;
    }

    @Override
    protected void onStop() {
        if (validateForm() && checkBox.isChecked()) {
            SharedPreferences sharedPreferences = getSharedPreferences(credentials, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email.getText().toString().trim());
            editor.commit();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(credentials, Context.MODE_PRIVATE);
        String s1 = sharedPreferences.getString("email", "");
        email.setText(s1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this,BusActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }


    public void forgotPasswordClicked(View view) {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    private void methodSignin(String semail, String spassword) {

        auth.signInWithEmailAndPassword(semail, spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firstTimeLogin();

                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    private void firstTimeLogin() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = (User) dataSnapshot.getValue(User.class);
                if (user == null) {
                    saveUserInDatabase();
                }
                else
                {
                    Intent intent = new Intent(LoginActivity.this,BusActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void saveUserInDatabase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String, String> values = new HashMap<>();
        int id = new Random().nextInt(50000-40000)+40000;
        values.put("id", String.valueOf(id));
        values.put("name", "0");
        values.put("imageLink", "default");
        values.put("email", email.getText().toString().trim());
        values.put("gender", "0");
        values.put("section", "0");
        values.put("subject", "0");
        values.put("level", "1");
        values.put("fID",user.getUid());
        values.put("password","0");
        reference.setValue(values).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, EditableProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("source","firstlogin");
                    startActivity(intent);
                    finish();
                }
                else{
                    auth.signOut();
                    Toast.makeText(getApplicationContext(),"Error in signIn",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}

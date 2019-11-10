package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.HashMap;

public class DeleteStudentAccount extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText id;
    private String adminPassword;
    private String adminEmail;
    private DotProgressBar progressBar;
    private Button deleteBtn;
    private String tfUID = null;
    private String givenId;
    private TextView error;
    private String studentPassword;
    private String studentEmail;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_student_account);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        deleteBtn = findViewById(R.id.btn_delete);
        progressBar = findViewById(R.id.progressbar);
        id = findViewById(R.id.txtid);
        error = findViewById(R.id.txterror);
        getSupportActionBar().setTitle("Delete Student's Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adminPassword = getIntent().getStringExtra("adminpassword");
        adminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        dialog  = new ProgressDialog(DeleteStudentAccount.this);
        dialog.setMessage("Saving");

    }


    public void deleteClicked(View view) {
        if (Valid()) {
            deleteBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getDetails();
        }
    }

    private void getDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    if (user.getId().equals(id.getText().toString()) && user.getLevel().equals("3")) {
                        tfUID = user.getfID();
                        studentPassword = user.getPassword();
                        studentEmail = user.getEmail();
                        deleteDetailsFromDatabase();
                    }
                }
                if (tfUID == null) {
                    error.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void deleteDetailsFromDatabase() {
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(tfUID);
        reference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                deleteAccount();
            }
        });
        if(dialog.isShowing())dialog.dismiss();

    }

    private void deleteAccount() {
        FirebaseAuth.getInstance().signOut();
        signInwithStudentAccount();
    }

    private void signInwithStudentAccount() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(studentEmail, studentPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    deleteAuthenticationAccount();
                } else {
                    adminSignIn(0);
                }
            }
        });
    }

    private void adminSignIn(final int result) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(adminEmail, adminPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (result == 0)
                        Toast.makeText(getApplicationContext(), "Error occured In deletion", Toast.LENGTH_SHORT).show();
                    if(result==1){
                        Toast.makeText(getApplicationContext(), "Account Successfully Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                }
            }
        });
    }

    private void deleteAuthenticationAccount() {
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    adminSignIn(1);
                }
                else{
                    adminSignIn(0);
                }
            }
        });
    }

    private boolean Valid() {
        boolean valid = true;
        if (id.getText().toString().length()!= 8) {
            valid = false;
            id.setError("8 Characters Needed");
        }

        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog.isShowing()) dialog.dismiss();
    }
}

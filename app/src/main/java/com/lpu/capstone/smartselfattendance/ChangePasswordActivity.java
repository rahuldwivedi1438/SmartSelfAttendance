package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPassword, newPassword, confirmPassword;
    private TextView errorTxt;
    private Toolbar toolbar;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPassword = findViewById(R.id.oldpasswordtxt);
        newPassword = findViewById(R.id.newpasswordtxt);
        confirmPassword = findViewById(R.id.confirmpasswordtxt);
        errorTxt = findViewById(R.id.txterror);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
        password = newPassword.getText().toString().trim();
    }

    public void changeClicked(View view) {
        if(validate()){
            varifiyUser();
        }
    }

    private void varifiyUser() {
        FirebaseUser fireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(fireBaseUser.getEmail(), oldPassword.getText().toString().trim());
        fireBaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> reAuthenticateTask) {
                if (!reAuthenticateTask.isSuccessful()){
                    errorTxt.setVisibility(View.VISIBLE);
                    oldPassword.setText("");
                    newPassword.setText("");
                    confirmPassword.setText("");
                }
                else {
                   changePassword();
                }
            }
        });
    }

    private void changePassword() {
        if(validate()){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.updatePassword(newPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Password Changed",Toast.LENGTH_SHORT).show();
                        changePasswordInDatabse();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Couldn't Update Password",Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }

    private void changePasswordInDatabse() {
        ProgressDialog dialog = new ProgressDialog(ChangePasswordActivity.this);
        dialog.setMessage("Saving");
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("password",password);
        reference.updateChildren(map);
        dialog.dismiss();
        finish();
    }

    private boolean validate() {
        boolean valid = true;
        if(newPassword.getText().toString().length()==0 || confirmPassword.getText().toString().length()==0
        || oldPassword.getText().toString().length()==0){
            oldPassword.setError("Required");
            newPassword.setError("Required");
            confirmPassword.setError("Required");
            valid = false;
        }
        if(!newPassword.getText().toString().trim().equals(confirmPassword.getText().toString().trim()))
        {
            Toast.makeText(getApplicationContext(),"new password and confirm password should be same",Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lpu.capstone.smartselfattendance.model.User;

import org.w3c.dom.Text;

public class NonEditableProfileActivity extends AppCompatActivity {

    private TextView userName,userEmail,userGender,userSection,userSubject,userId;
    private ImageView userImage;
    private DatabaseReference reference;
    private FirebaseUser fUser;
    private Toolbar toolbar;
    private Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_editable_profile);
        userName = findViewById(R.id.txtname);
        userEmail = findViewById(R.id.txtemail);
        userGender = findViewById(R.id.txtgender);
        userSection = findViewById(R.id.txtsection);
        userSubject = findViewById(R.id.txtsubject);
        userImage = findViewById(R.id.profile_image);
        userId = findViewById(R.id.txtid);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        String level = ProjectUtils.getLevel(getApplicationContext());
        Log.d("sonu", "onCreate: level"+level);

        if(level=="2"){
            userSection.setVisibility(View.GONE);
            userSubject.setVisibility(View.VISIBLE);
        }
        if(level=="3"){
            userSubject.setVisibility(View.GONE);
            userSection.setVisibility(View.VISIBLE);
        }
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        loading  = new Dialog(this);
        loading.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.loading_view,null);
        loading.setContentView(view);
        setProfile();


    }

    private void setProfile() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loading.create();
        }
        loading.show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user!=null;
                if(!user.getName().contentEquals("0"))userName.setText(user.getName());
                if(!user.getEmail().contentEquals("0"))userEmail.setText(user.getEmail());
                if(ProjectUtils.setGenderList().contains(user.getGender()))userGender.setText(user.getGender());
                if(ProjectUtils.setSectionList().contains(user.getSection())){
                    userSection.setText(user.getSection());
                }
                else userSection.setVisibility(View.GONE);
                if(ProjectUtils.setSubjectList().contains(user.getSubject()))userSubject.setText(user.getSubject());
                else userSubject.setVisibility(View.GONE);
                if(!user.getId().contentEquals("0"))userId.setText(user.getId());

                if(user.getImageLink().equals("default"))
                {
                    userImage.setImageResource(R.drawable.ic_person);
                }else
                { Glide.with(getApplicationContext()).load(user.getImageLink()).into(userImage);
                }
                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Can't fetch data",Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.change_password_option,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(NonEditableProfileActivity.this,ChangePasswordActivity.class));
        return super.onOptionsItemSelected(item);
    }

}

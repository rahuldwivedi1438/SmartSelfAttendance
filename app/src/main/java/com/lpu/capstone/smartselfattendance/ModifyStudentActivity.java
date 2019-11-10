package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.lpu.capstone.smartselfattendance.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModifyStudentActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private StorageReference storageReference;
    private CircleImageView profileImage;
    private EditText id,name;
    private TextView email,password;
    private Spinner gender,section;
    private FirebaseUser fUser;
    private Uri imageUri,tempUri;
    private StorageTask uploadTask;
    private static final int IMAGE_REQUEST=1;
    private FloatingActionButton editImage;
    private ArrayList<String> genderList = ProjectUtils.setGenderList();
    private ArrayList<String> sectionList = ProjectUtils.setSectionList();
    private String adminPassword;
    private String adminEmail;
    private String givenId;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private String tfUID=null;
    private Button saveBtn;
    private DotProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_student);
        id = findViewById(R.id.txtid);
        name = findViewById(R.id.txtname);
        gender = findViewById(R.id.txtgender);
        section = findViewById(R.id.txtsection);
        email = findViewById(R.id.txtemail);
        password = findViewById(R.id.txtdefaultpassword);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        profileImage = findViewById(R.id.profile_image);
        editImage = findViewById(R.id.editimagebtn);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressbar);
        saveBtn = findViewById(R.id.btn_save);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Students Details");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setSelection(0);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sectionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        section.setAdapter(adapter1);
        section.setSelection(0);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        auth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        adminEmail = fUser.getEmail();
        adminPassword = getIntent().getStringExtra("adminpassword");
        givenId = getIntent().getStringExtra("id");
        setProfile();
    }

    private void setProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                { User user = snapshot.getValue(User.class);
                    assert user!=null;
                    if(user.getId().equals(givenId) && user.getLevel().equals("3"))
                    {
                        tfUID = user.getfID();
                        setProfileDetails(tfUID);
                    }
                }
                if(tfUID==null)setProfileDetails(tfUID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setProfileDetails(String tfUID) {
        if(tfUID==null){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            dialog.setTitle("Not Found");
            dialog.setMessage("NO Faculty with this Id found");
            dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        }
        else{
            reference = FirebaseDatabase.getInstance().getReference("Users").child(tfUID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user!=null;
                    if(user!=null){
                        if(!user.getName().contentEquals("0"))name.setText(user.getName());
                        if(!user.getEmail().contentEquals("0"))email.setText(user.getEmail());
                        if(ProjectUtils.setGenderList().contains(user.getGender()))gender.setSelection(ProjectUtils.setGenderList().indexOf(user.getGender()));
                        if(ProjectUtils.setSectionList().contains(user.getSection()))section.setSelection(ProjectUtils.setSectionList().indexOf(user.getSection()));
                        if(!user.getId().contentEquals("0"))id.setText(user.getId());

                        if(user.getLevel()=="3")
                        {
                            section.setVisibility(View.VISIBLE);
                        }
                        if(user.getImageLink().equals("default"))
                        {
                            profileImage.setImageResource(R.drawable.ic_person);
                        }else
                        { Glide.with(getApplicationContext()).load(user.getImageLink()).into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }

            });


        }
    }

    public void saveClicked(View view) {

        if (uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(getApplicationContext(), "Upload in Progress", Toast.LENGTH_LONG).show();

        } else {
            if(validateDetails()){
                saveBtn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                updateStudentInDatabase();
            }
        }
    }


    private void updateStudentInDatabase() {

        ProgressDialog dialog = new ProgressDialog(ModifyStudentActivity.this);
        dialog.setMessage("Saving");
        dialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name",name.getText().toString().trim());
        map.put("gender",gender.getSelectedItem().toString().trim());
        map.put("section",section.getSelectedItem().toString().trim());
        reference.updateChildren(map);
        dialog.dismiss();
        uploadImage(tfUID);
    }

    private boolean validateDetails() {
        boolean valid = true;
        if(TextUtils.isEmpty(id.getText().toString()) || id.getText().toString().length()!=8 ){
            id.setError("Invalid, 6 characters needed");
            valid = false;
        }
        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError("Required");
            valid = false;
        }
        if(section.getSelectedItemPosition()==0){
            Toast.makeText(getApplicationContext(),"Select a Section",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }


    private void adminSignIn() {
        auth.signInWithEmailAndPassword(adminEmail, adminPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(getApplicationContext(),"error occured",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap =  MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(final String StudentUID) {
        if(imageUri!=null)
        {
            final ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setMessage("Uploading Data");
            progressDialog.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw  task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(StudentUID);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageLink",mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Failed to upload Image",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    saveBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
           finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fUser==null){
            adminSignIn();
        }
        finish();
    }
}

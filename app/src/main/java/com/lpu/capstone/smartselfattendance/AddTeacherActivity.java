package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeacherActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private StorageReference storageReference;
    private CircleImageView profileImage;
    private EditText id, name, email, password;
    private Spinner gender, subject;
    private FirebaseUser fUser;
    private Uri imageUri, tempUri;
    private StorageTask uploadTask;
    private static final int IMAGE_REQUEST = 1;
    private FloatingActionButton editImage;
    private ArrayList<String> genderList = ProjectUtils.setGenderList();
    private ArrayList<String> subjectList = ProjectUtils.setSubjectList();
    private String adminPassword;
    private String adminEmail;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private Button saveBtn;
    private DotProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        id = findViewById(R.id.txtid);
        name = findViewById(R.id.txtname);
        gender = findViewById(R.id.txtgender);
        subject = findViewById(R.id.txtsubject);
        email = findViewById(R.id.txtemail);
        password = findViewById(R.id.txtdefaultpassword);
        profileImage = findViewById(R.id.profile_image);
        progressBar = findViewById(R.id.progressbar);
        saveBtn = findViewById(R.id.btn_save);
        editImage = findViewById(R.id.editimagebtn);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Faculty Details");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setSelection(0);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, subjectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter1);
        subject.setSelection(0);
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

    }

    public void saveClicked(View view) {

        if (uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(getApplicationContext(), "Upload in Progress", Toast.LENGTH_LONG).show();

        } else {
            if (validateDetails()) {

                saveBtn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                auth.signOut();
                saveTeacherInDatabase();
            }

        }
    }

    private boolean validateDetails() {
        boolean valid = true;
        if (TextUtils.isEmpty(id.getText().toString()) || id.getText().toString().length() != 6) {
            id.setError("Invalid, 6 characters needed");
            valid = false;
        }
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Required");
            valid = false;
        }
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Required");
            valid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required");
            valid = false;
        }
        if(subject.getSelectedItemPosition()==0){
            Toast.makeText(getApplicationContext(),"Select a Subject",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private void saveTeacherInDatabase() {

        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = auth.getCurrentUser();
                    assert user != null;
                    String userID = user.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                    HashMap<String, String> values = new HashMap<>();
                    values.put("id", id.getText().toString().trim());
                    values.put("name", name.getText().toString().trim());
                    values.put("imageLink", "default");
                    values.put("email", email.getText().toString().trim());
                    values.put("gender", gender.getSelectedItem().toString().trim());
                    values.put("subject", subject.getSelectedItem().toString().trim());
                    values.put("level", "2");
                    values.put("section", "0");
                    values.put("fID", user.getUid());
                    values.put("password",password.getText().toString().trim());

                    reference.setValue(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                uploadImage(user.getUid());
                            }
                        }
                    });
                } else {
                    Toast.makeText(AddTeacherActivity.this, "This Email can't be used to create a new Faculty account", Toast.LENGTH_LONG).show();
                    adminSignIn(0);
                }
            }
        });
    }


    private void adminSignIn(final int result) {
        auth.signInWithEmailAndPassword(adminEmail, adminPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    if(result==1){
                        finish();
                    }
                } else {
                }

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(final String teacherUID) {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading Data");
            progressDialog.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(teacherUID);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageLink", mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();
                        auth.signOut();
                        adminSignIn(1);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to upload Image", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
            auth.signOut();
            adminSignIn(1);
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
        if (fUser == null) {
            adminSignIn(1);
        }
    }
}

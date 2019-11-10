package com.lpu.capstone.smartselfattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class EditableProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText userName,userSection,userSubject;
    private Spinner userGender;
    private TextView userEmail,userId;
    private ImageView userImage;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseUser fUser;
    private Uri imageUri,tempUri;
    private StorageTask uploadTask;
    private static final int IMAGE_REQUEST=1;
    private FloatingActionButton editImage;
    private ArrayList<String> genderList = ProjectUtils.setGenderList();
    private Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable_profile);
        toolbar = findViewById(R.id.toolbar);
        editImage = findViewById(R.id.editimagebtn);
        userName = findViewById(R.id.txtname);
        userEmail = findViewById(R.id.txtemail);
        userGender = findViewById(R.id.txtgender);
        userSection = findViewById(R.id.txtsection);
        userSubject = findViewById(R.id.txtsubject);
        userImage = findViewById(R.id.profile_image);
        userId = findViewById(R.id.txtid);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        String source =getIntent().getStringExtra("source");
        if(!source.contentEquals("firstlogin"))
        {getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");}
        else getSupportActionBar().setTitle("Fill Profile");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGender.setAdapter(adapter);
        userGender.setSelection(0);
        userSection.setVisibility(View.GONE);
        userSubject.setVisibility(View.GONE);
        loading  = new Dialog(this);
        loading.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.loading_view,null);
        loading.setContentView(view);
        setProfile();
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


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
                if(genderList.contains(user.getGender()))userGender.setSelection(genderList.indexOf(user.getGender()));
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap =  MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if(imageUri!=null)
        {
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
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageLink",mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    setProfile();
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), "Upload in Progress", Toast.LENGTH_LONG).show();

            } else {
                uploadImage();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_option,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if(item.getItemId()==R.id.save_menu){
           Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
           saveClicked();
       }
       else if(item.getItemId()==R.id.change_password_menu){
           startActivity(new Intent(EditableProfileActivity.this,ChangePasswordActivity.class));
       }
        return super.onOptionsItemSelected(item);
    }

    private void saveClicked() {
        ProgressDialog dialog = new ProgressDialog(EditableProfileActivity.this);
        dialog.setMessage("Saving");
        dialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name",userName.getText().toString().trim());
        map.put("gender",userGender.getSelectedItem().toString().trim());
        reference.updateChildren(map);
        dialog.dismiss();
        Intent intent = new Intent(EditableProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}

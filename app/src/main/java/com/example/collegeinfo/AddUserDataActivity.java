package com.example.collegeinfo;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;



public class AddUserDataActivity extends AppCompatActivity {


    private Button saveButton;
    private ImageView addPhotoBtn;

    private EditText titleEditText;
    private EditText thoughtsEt;
    private EditText postEmail;



    //Firebase Firestore
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("UserData");
    //Firebase Storage
    private StorageReference storageReference;
    //Firebase Auth
    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // Using Activity Result Launcher
    ActivityResultLauncher<String> mTakePhoto;
    Uri imageUri;

     //String email=accountLoginDataActivity.edit1.getText().toString();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_data);








        titleEditText=findViewById(R.id.post_title_et);
        thoughtsEt=findViewById(R.id.post_description_et);

        saveButton=findViewById(R.id.post_save_journal_button);
        addPhotoBtn=findViewById(R.id.postCameraButton);






        //FireBase storage Reference
        storageReference = FirebaseStorage.getInstance().getReference();
        // firebase Auth
        firebaseAuth =FirebaseAuth.getInstance();
        // Getting The Current User
        if(user != null){
            currentUserId=user.getUid();
            currentUserName=user.getDisplayName();


        }


        // Apply the input filter to your EditText field
        EditText editText = findViewById(R.id.post_description_et);
        editText.setFilters(new InputFilter[] {new AbusiveWordFilter()});






        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }


        });

        mTakePhoto=registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //Showing the image
                        addPhotoBtn.setImageURI(result);
                        //get the image uri
                        imageUri=result;
                    }
                }
        );


        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting Image From Gallery
                mTakePhoto.launch("image/*");
            }
        });


    }
    private void saveUserData() {

        String title=titleEditText.getText().toString().trim();
        String thoughts=thoughtsEt.getText().toString().trim();


        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) ){
            final StorageReference filepath=storageReference.child("userdata_images")
                    .child("my_image"+ Timestamp.now().getSeconds());
            //uploading the image
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUri=uri.toString();
                            // creating a journal object
                            UserData userData=new UserData();
                            userData.setTitle(title);
                            userData.setThoughts(thoughts);
                            userData.setImageUrl(imageUri);
                            userData.setUserEmail(currentUserName);
                            userData.setUserId(currentUserId);

                            collectionReference.add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Intent i=new Intent(AddUserDataActivity.this,AccountLoginActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddUserDataActivity.this, "OOops! Its Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(AddUserDataActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });



        }else{

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user=firebaseAuth.getCurrentUser();
    }
    }

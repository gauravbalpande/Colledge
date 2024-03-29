package com.example.collegeinfo;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;




public class RegisterActivity extends AppCompatActivity {








    public class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "email_database";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableQuery = "CREATE TABLE emails (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT)";
            db.execSQL(createTableQuery);






        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Handle database upgrades if needed
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                // You can access email accounts here
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }


    EditText password_create, username_create, email_create;
    Button createBtn;
    ImageView imageView;
    // FireBase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    Uri imageUri;
    ActivityResultLauncher<String> mTakePhoto;
    // FireBase Connections
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);








        password_create = findViewById(R.id.password_create);
        username_create = findViewById(R.id.username_create_ET);
        email_create = findViewById(R.id.email_create);
        createBtn = findViewById(R.id.acc_signUp_btn);
        imageView=findViewById(R.id.imageUpload);










        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // check permisiion


        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 2000);
        } else {
            // Permission already granted, proceed with your logic
            // You can access email accounts here
        }












        //Listening for changes in the authentication
        //state and responds accordingly when the state changes
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                // check the users logged in or not
                if (currentUser != null) {
                    // user already logged in

                } else {
                    // the user signed out


                }
            }
        };
        mTakePhoto=registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //Showing the image
                        imageView.setImageURI(result);
                        //get the image uri
                        imageUri=result;
                    }
                }
        );









        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting Image From Gallery
                mTakePhoto.launch("image/*");
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(email_create.getText().toString())
                        && !TextUtils.isEmpty(username_create.getText().toString())
                        && !TextUtils.isEmpty(password_create.getText().toString())
                ){
                    String email=email_create.getText().toString().trim();
                    String pass=password_create.getText().toString().trim();
                    String username=username_create.getText().toString().trim();
                    CreateUserEmailAccount(email,pass,username);
                }
                else{
                    Toast.makeText(RegisterActivity.this, "No Fields are allowed to be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void CreateUserEmailAccount(String email, String pass, String username) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(username)) {
            firebaseAuth.createUserWithEmailAndPassword(
                    email, pass
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Account created Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    }


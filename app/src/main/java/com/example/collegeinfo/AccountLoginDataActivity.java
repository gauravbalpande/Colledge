package com.example.collegeinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountLoginDataActivity extends AppCompatActivity {
    private TextView textView;
    public EditText edit1,edit2;
    private Button btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login_data);
        textView=findViewById(R.id.textView2);
        edit1=findViewById(R.id.email);
        edit2=findViewById(R.id.password);
        btn=findViewById(R.id.email_signIn);
        firebaseAuth=FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPassUser(
                        edit1.getText().toString().trim(),
                        edit2.getText().toString().trim()
                );
            }
        });

    }
    private void loginEmailPassUser(String email, String pwd) {
        //checking for empty text
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                Intent i = new Intent(AccountLoginDataActivity.this, AccountLoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } else {
                                // Handle case where user is unexpectedly null
                                Toast.makeText(AccountLoginDataActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occurred during sign in
                            Toast.makeText(AccountLoginDataActivity.this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle case where email or password is empty
            Toast.makeText(AccountLoginDataActivity.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
        }
    }

}






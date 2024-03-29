package com.example.collegeinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AccountLoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FloatingActionButton fb;

    //FireBase Firestore
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("UserData");

    //Firebase Storage
    private StorageReference storageReference;

    //list of journals
    private List<UserData> userDataList;

    //recyclerview and adapter
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private Button btn;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);
        textView=findViewById(R.id.textView5);
        btn=findViewById(R.id.switchButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AccountLoginActivity.this,AnonymousLoginActivity.class);
                startActivity(i);
            }
        });





        //Firebase Auth
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        //widgets
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //posts Arraylist
        userDataList=new ArrayList<>();
        fb=findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AccountLoginActivity.this,AddUserDataActivity.class);
                startActivity(i);
            }
        });
    }
    // Adding a Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId=item.getItemId();
        if(itemId==R.id.action_add) {

            if (user != null && firebaseAuth != null) {
                Intent i = new Intent(AccountLoginActivity.this, AddUserDataActivity.class);
                startActivity(i);
            }
        }else if(itemId==R.id.action_signout)
        {
            if (user != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                Intent i = new Intent(AccountLoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

        super.onStart();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //querySnapShot is an object that represents a single document received from a FireStore query
                //QueryDocumentsSnapshot--> document
                for (QueryDocumentSnapshot journal:queryDocumentSnapshots){
                    UserData journals=journal.toObject(UserData.class);
                    userDataList.add(journals);

                }

                // RecyclerView
                adapter=new MyAdapter(AccountLoginActivity.this,userDataList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountLoginActivity.this, "Oops! Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }

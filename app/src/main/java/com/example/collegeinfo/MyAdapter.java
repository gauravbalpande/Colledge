package com.example.collegeinfo;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<UserData> UserDataList;
    private Dialog dialog;


    public MyAdapter(Context context, List<UserData> userDataList) {
        this.context = context;
        UserDataList = userDataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_adapter
                        , parent,
                        false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserData currentUserData = UserDataList.get(position);
        holder.title.setText(currentUserData.getTitle());
        holder.thoughts.setText(currentUserData.getThoughts());
        holder.email.setText(currentUserData.getUserId());
        holder.userId.setText(currentUserData.getUserId());
        holder.username.setText(currentUserData.getUserEmail());

        holder.image1.setImageResource(R.drawable.iconsc);
        holder.image2.setImageResource(R.drawable.iconsb);
        holder.image3.setImageResource(R.drawable.iconsa);

        String imageUrl = currentUserData.getImageUrl();


        //glide library to display the image
        Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(holder.image);


    }

    @Override
    public int getItemCount() {
        return UserDataList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, thoughts, email;
        public ImageView image;
        public TextView userId, username, likeCount;
        public ImageView image1, image2, image3;
        public EditText editText9;


        private boolean isLiked = false;


        private static final String PREF_FILE_NAME = "MyPrefs";

        private static final String LIKE_COUNT_KEY = "like_count";

        private SharedPreferences sharedPreferences;
        private ImageView likeImageView;
        private int likeCount1=0;
        private boolean likedone;
        private FirebaseFirestore db=FirebaseFirestore.getInstance();
        private CollectionReference collectionReference=db.collection("UserData");
        //Firebase Storage
        private StorageReference storageReference;
        private FirebaseAuth firebaseAuth;
        private FirebaseAuth.AuthStateListener authStateListener;
        private FirebaseUser user;
        Uri imageUri;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.UserData_title_list);
            thoughts = itemView.findViewById(R.id.Userdata_thought_list);
            image = itemView.findViewById(R.id.Userdata_image_list);
            email = itemView.findViewById(R.id.UserData_email);
            username = itemView.findViewById(R.id.username);
            userId = itemView.findViewById(R.id.username2);
            image1 = itemView.findViewById(R.id.like);
            image2 = itemView.findViewById(R.id.comment);
            image3 = itemView.findViewById(R.id.share);
            editText9=itemView.findViewById(R.id.editText9);

            firebaseAuth=FirebaseAuth.getInstance();
            user=firebaseAuth.getCurrentUser();


















            likeCount = itemView.findViewById(R.id.likeCount);


            sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            likeCount1 = sharedPreferences.getInt(LIKE_COUNT_KEY, 0);
            likeCount.setText(String.valueOf(likeCount1));







            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        if (!sharedPreferences.contains(LIKE_COUNT_KEY)) {
                            likeCount1++;
                            likeCount.setText(String.valueOf(likeCount1)); // Update the text with likeCount1, not likeCount
                            image1.setEnabled(false);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(LIKE_COUNT_KEY, likeCount1);
                            editor.apply();

                        }
                    }
                }
            });


            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);
                }
            });
        }


        public void showDialog() {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.dialogue, null);
            dialog.setContentView(view);
            dialog.show();
            Button submit = view.findViewById(R.id.dialogButt);
            EditText edit = view.findViewById(R.id.editText9);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String groupName = edit.getText().toString();
                    //Toast.makeText(this, "Your ChatGroup"+groupName, Toast.LENGTH_SHORT).show();

                    // myViewModel.createGroup(groupName);

                    saveUserData1();













                    dialog.dismiss();
                }

                private void saveUserData1() {

                    String editText = editText9.getText().toString().trim();


                    if (!TextUtils.isEmpty(editText)) {
                        final StorageReference filepath = storageReference.child("userdata_images")
                                .child("my_image" + Timestamp.now().getSeconds());
                        //uploading the image
                        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUri = uri.toString();
                                        // creating a journal object
                                        UserData userData = new UserData();
                                        userData.setThoughts(editText);


                                        collectionReference.add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                Intent i = new Intent(context, AccountLoginActivity.class);
                                                context.startActivity(i);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "OOops! Its Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {

                    }
                }



















            });
        }
    }
}






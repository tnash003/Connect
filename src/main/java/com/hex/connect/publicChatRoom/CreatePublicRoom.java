package com.hex.connect.publicChatRoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hex.connect.ChatActivity;
import com.hex.connect.CreateEventRoomActivity;
import com.hex.connect.R;

import java.util.HashMap;

public class CreatePublicRoom extends AppCompatActivity {

    ImageView image_create_room,image_profile;
    TextInputLayout room_name,grp_details;
    Button save;

    FirebaseAuth firebaseAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_public_room);

        image_create_room = findViewById(R.id.image_create_room );
        image_profile = findViewById(R.id.image_profile );
        room_name = findViewById(R.id.room_name );
        grp_details = findViewById(R.id.grp_details );
        save = findViewById(R.id.save );

        firebaseAuth = FirebaseAuth.getInstance();

        //String str = "https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2F5251.jpg?alt=media&token=956acbd6-80de-4e95-84eb-78bd5c48709c";
        //Glide.with(this).load(str).into(image_create_room);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924").
                into(image_profile);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput();
            }
        });
    }

    private void confirmInput() {
        if (!validName()){
            return;
        }else{
            creatingGroup();
        }
    }

    private void creatingGroup() {
        pd = new ProgressDialog(this);
        pd.setMessage("Creating Chat Room");

        final String grp_title = room_name.getEditText().getText().toString().trim();
        final String grp_description = grp_details.getEditText().getText().toString().trim();

        pd.show();

            String str = "https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924";
            createGroup(grp_title,grp_description,str);

    }

    private void createGroup( String grp_title , String grp_description, String groupIcon) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PublicRoom");
        final String g_timestamp = reference.push().getKey();

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("roomId", g_timestamp);
        hashMap.put("roomTitle",grp_title);
        hashMap.put("roomDescription", grp_description);
        hashMap.put("groupIcon",groupIcon);
        hashMap.put("createdBy", firebaseAuth.getUid());
        hashMap.put("deepLink", getDeepLink(g_timestamp));

        reference.child(g_timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("id" ,firebaseAuth.getUid());
                hashMap1.put("role", "creator");
                hashMap1.put("roomId", g_timestamp);

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                reference1.child(g_timestamp).child("Participants").child(firebaseAuth.getUid())
                        .setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("publicroomId",g_timestamp);
                        editor.apply();
                        Intent intent = new Intent(CreatePublicRoom.this, ChatActivityPublic.class);
                        startActivity(intent);
                        Toast.makeText(CreatePublicRoom.this, "Chat Room created successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreatePublicRoom.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private String getDeepLink(String g_timestamp) {
        return "https://pindle.page.link/?link=connect.com/?id="+g_timestamp
                +"&apn=com.hex.connect";
    }

    private boolean validName(){
        String email1 = room_name.getEditText().getText().toString().trim();

        if (email1.isEmpty()){
            room_name.setError("Field can't be empty");
            return false;
        }else{
            room_name.setError(null);
            return true;
        }
    }
}

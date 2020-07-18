package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreateEventRoomActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE =100;
    private static final int STORAGE_REQUEST_CODE= 101;

    private static final int IMAGE_PICK_CAMERA_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE= 201;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri imageUri = null;

    ImageView image_create_room,image_profile;
    TextInputLayout room_name,grp_details;
    TextView date_event;
    LinearLayout change_date;
    Button save;

    FirebaseAuth firebaseAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_room);

        image_create_room = findViewById(R.id.image_create_room );
        image_profile = findViewById(R.id.image_profile );
        room_name = findViewById(R.id.room_name );
        grp_details = findViewById(R.id.grp_details );
        date_event = findViewById(R.id.date_event );
        change_date = findViewById(R.id.change_date );
        save = findViewById(R.id.save );

        firebaseAuth = FirebaseAuth.getInstance();

        cameraPermission = new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};

        String str = "https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2F5251.jpg?alt=media&token=956acbd6-80de-4e95-84eb-78bd5c48709c";
        Glide.with(this).load(str).into(image_create_room);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924").
                into(image_profile);

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });

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
        final String event_date = date_event.getText().toString();

        pd.show();

        if (imageUri == null){
            String str = "https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924";
            createGroup(grp_title,grp_description,str,event_date);
        }else{
            String fileNamePath = "Group_Imgs/"+"image"+System.currentTimeMillis();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNamePath);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!p_uriTask.isSuccessful()){
                        Uri p_downloadUrl = p_uriTask.getResult();
                        if (p_uriTask.isSuccessful()){
                            createGroup(grp_title,grp_description,""+p_downloadUrl,event_date);
                        }
                    }
                }
            });
        }
    }

    private void createGroup( String grp_title , String grp_description, String groupIcon, String event_date) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        final String g_timestamp = reference.push().getKey();

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("roomId", g_timestamp);
        hashMap.put("roomTitle",grp_title);
        hashMap.put("roomDescription", grp_description);
        hashMap.put("eventDate",event_date);
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
                        editor.putString("roomId",g_timestamp);
                        editor.apply();
                        Intent intent = new Intent(CreateEventRoomActivity.this, ChatActivity.class);
                        startActivity(intent);
                        Toast.makeText(CreateEventRoomActivity.this, "Chat Room created successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateEventRoomActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private String getDeepLink(String g_timestamp) {
        return "https://pindle.page.link/?link=connect.com/?id="+g_timestamp
                +"&apn=com.hex.connect";
    }

    private void showImagePicker() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image:")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            if (!checkCameraPermission()){
                                requestCameraPermission();
                            }else{
                                pickFromCamera();
                            }
                        }else{
                            if (!checkStoragePermission()){
                                requestStoragePermission();
                            }else{
                                pickFromGallery();
                            }
                        }
                    }
                }).show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Chat room image icon title");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Chat room image icon description" );
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent , CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result =  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result =  ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission(){
        requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case CAMERA_REQUEST_CODE:{
//                if (grantResults.length > 0){
//                    boolean cameraAccepted = grantResults[0] ==  PackageManager.PERMISSION_GRANTED;
//                    boolean storageAccepted = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
//
//                    if (cameraAccepted && storageAccepted){
//                        pickFromCamera();
//                    }else{
//                        Toast.makeText(this, "Permissions are needed.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            break;
//            case STORAGE_REQUEST_CODE:{
//                if (grantResults.length > 0){
//                    boolean cameraAccepted = grantResults[0] ==  PackageManager.PERMISSION_GRANTED;
//                    if (cameraAccepted ){
//                        pickFromGallery();
//                    }else{
//                        Toast.makeText(this, "Permissions are needed.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                imageUri = data.getData();
                Glide.with(this).load(imageUri).into(image_profile);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                Glide.with(this).load(imageUri).into(image_profile);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
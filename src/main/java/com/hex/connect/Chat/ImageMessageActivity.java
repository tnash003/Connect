package com.hex.connect.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hex.connect.MainActivity;
import com.hex.connect.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ImageMessageActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;
    String roomId;
    ImageView backarrow, imageView,btn_send;
    Intent intent;
    FirebaseAuth firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_message);
        intent = getIntent();
        roomId = intent.getStringExtra("roomId");

        backarrow = findViewById(R.id.backarrow);
        imageView = findViewById(R.id.imageView);
        btn_send = findViewById(R.id.btn_send);
        firebaseUser = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        CropImage.activity()
                .start(ImageMessageActivity.this);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Message");
        progressDialog.show();

        if (imageUri != null){
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        sendMessage( myUrl);

                        progressDialog.dismiss();

                        finish();
                    }else{
                        Toast.makeText(ImageMessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImageMessageActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE | requestCode == RESULT_OK){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();

                imageView.setImageURI(imageUri);
            }else{
                Toast.makeText(this, "Something gone wrong", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(ImageMessageActivity.this, MainActivity.class));
                finish();
            }

        }catch (NullPointerException e){
            finish();
        }
    }

    private void sendMessage( String imageUri){
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy   hh:mm:ss a");
        String timestamp = simpleDateFormat.format(calender.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");

        String messageId = reference.push().getKey();

        HashMap<String , Object> hashMap = new HashMap<>();

        hashMap.put("sender" , firebaseUser.getCurrentUser().getUid());
        hashMap.put("message" , "");
        hashMap.put("timestamp" , timestamp);
        hashMap.put("type" , "image");
        hashMap.put("messageId", messageId);
        hashMap.put("imageurl",imageUri);

        reference.child(roomId).child("Messages").child(messageId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageMessageActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

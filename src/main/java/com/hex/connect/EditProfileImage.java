package com.hex.connect;

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
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.hex.connect.Modal.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class EditProfileImage extends AppCompatActivity {

    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;
    FirebaseStorage mStorage;
    String imURL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_image);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        mStorage =FirebaseStorage.getInstance();

        CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(EditProfileImage.this);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Updating Photo");
        pd.show();
        if (mImageUri != null){
            final StorageReference filereference = storageRef.child(System.currentTimeMillis()+" , " +getFileExtension(mImageUri));

            uploadTask = filereference.putFile(mImageUri);
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
                        final String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (getContext() == null){
                                    return;
                                }
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                                HashMap<String , Object > hashMap = new HashMap<>();
                                hashMap.put("imageurl" , "" + myUrl);

                                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        startActivity(new Intent(EditProfileImage.this, EditProfileActivity.class));
                                    }
                                });



//                                User user = dataSnapshot.getValue(User.class);
//                                if (user.getImageurl().equals("https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924")){
//                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//                                    HashMap<String , Object > hashMap = new HashMap<>();
//                                    hashMap.put("imageurl" , "" + myUrl);
//
//                                    ref.updateChildren(hashMap);
//                                    pd.dismiss();
//                                }else{
//                                    StorageReference imageRef = mStorage.getReferenceFromUrl(user.getImageurl());
//                                    imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//                                            HashMap<String , Object > hashMap = new HashMap<>();
//                                            hashMap.put("imageurl" , "" + myUrl);
//
//                                            reference.updateChildren(hashMap);
//                                            pd.dismiss();
//                                        }
//                                    });
//                                }
//
//                                Glide.with(EditProfileImage.this).load(user.getImageurl()).into(image_profile);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        pd.dismiss();
                        Toast.makeText(EditProfileImage.this, "Updating Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE | resultCode == RESULT_OK){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                mImageUri = result.getUri();

                uploadImage();
            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            Intent intent = new Intent(EditProfileImage.this , EditProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }
}


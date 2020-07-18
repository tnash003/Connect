package com.hex.connect.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.EditProfileImage;
import com.hex.connect.Modal.User;
import com.hex.connect.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class EditOtherDetails extends Fragment {

    EditText add_more_details;
    CircleImageView image_profile;
    LinearLayout edit_pic,remove_more,remove_pic;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    ProgressDialog pd;
    Button save;

    Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_other_details, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        add_more_details = view.findViewById(R.id.add_more_details);
        image_profile = view.findViewById(R.id.image_profile);
        edit_pic = view.findViewById(R.id.edit_pic);
        remove_more = view.findViewById(R.id.remove_details);
        remove_pic = view.findViewById(R.id.remove_pic);
        progressBar = view.findViewById(R.id.progress_bar);
        save = view.findViewById(R.id.save);

//        edit_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFileChooser();
//            }
//        });

        remove_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_more_details.setText("");
            }
        });

        edit_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileImage.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(getContext());
                pd.setMessage("Updating Profile");
                pd.show();
                saveInfo();
            }
        });

        userDetails();

        return view;
    }

    private void defaultImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=c8708b8a-a613-4540-b02c-7f214586fb32");

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                }else{
                    pd.dismiss();
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("bio" , add_more_details.getText().toString());

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                }else{
                    pd.dismiss();
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
//
//        }
//    }
//
//    private void openFileChooser() {
//        Intent intent  = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,PICK_IMAGE_REQUEST);
//    }

    private void userDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                add_more_details.setText(user.getBio());
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

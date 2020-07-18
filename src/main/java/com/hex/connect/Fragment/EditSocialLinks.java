package com.hex.connect.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.Modal.Links;
import com.hex.connect.Modal.User;
import com.hex.connect.R;

import java.util.HashMap;

public class EditSocialLinks extends Fragment {

    EditText edit_insta,edit_snapchat , edit_youtube , edit_other;
    Button save;
    FirebaseUser firebaseUser;
    ProgressDialog pd;
    LinearLayout trash_insta, trash_snapchat, trash_youtube, trash_other;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_social_links, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        edit_insta = view.findViewById(R.id.edit_insta);
        edit_snapchat = view.findViewById(R.id.edit_snapchat);
        edit_youtube = view.findViewById(R.id.edit_youtube);
        edit_other = view.findViewById(R.id.edit_other);
        save = view.findViewById(R.id.save);
        trash_insta = view.findViewById(R.id.trash_insta);
        trash_snapchat = view.findViewById(R.id.trash_snapchat);
        trash_youtube = view.findViewById(R.id.trash_youtube);
        trash_other = view.findViewById(R.id.trash_other);

        trash_insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_insta.setText("");
            }
        });

        trash_snapchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_snapchat.setText("");
            }
        });

        trash_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_youtube.setText("");
            }
        });

        trash_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_other.setText("");
            }
        });

        getUserInfo();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(getContext());
                pd.setMessage("Updating Profile");
                pd.show();
                updateLinks();
            }
        });

        return view;
    }

    private void updateLinks() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Links").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("insta" , edit_insta.getText().toString().trim());
        hashMap.put("snapchat" , edit_snapchat.getText().toString().trim());
        hashMap.put("youtube" , edit_youtube.getText().toString().trim());
        hashMap.put("other" , edit_other.getText().toString().trim());

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

    private void getUserInfo()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Links").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                Links user = dataSnapshot.getValue(Links.class);

                edit_insta.setText(user.getInsta());
                edit_snapchat.setText(user.getSnapchat());
                edit_youtube.setText(user.getYoutube());
                edit_other.setText(user.getOther());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

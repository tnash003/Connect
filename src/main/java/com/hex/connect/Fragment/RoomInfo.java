package com.hex.connect.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.MainActivity;
import com.hex.connect.Modal.Message;
import com.hex.connect.Modal.Rooms;
import com.hex.connect.R;
import com.hex.connect.StartActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomInfo extends Fragment {

    CircleImageView room_image;
    TextView room_name,room_details,event_date,created_by,text_leave;
    Button edit;
    LinearLayout delete;

    FirebaseAuth firebaseAuth;
    String roomId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_info, container, false);

        room_image = view.findViewById(R.id.room_image);
        room_name = view.findViewById(R.id.room_name);
        room_details = view.findViewById(R.id.room_details);
        event_date = view.findViewById(R.id.event_date);
        created_by = view.findViewById(R.id.created_by);
        text_leave = view.findViewById(R.id.text_leave);
        edit = view.findViewById(R.id.edit);
        delete = view.findViewById(R.id.delete);

        firebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        roomId = preferences.getString("roomId", "none");

        uploadInfo();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getContext(),EditRoomInfo.class));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text_leave.getText().toString().equals("Leave")){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(roomId).child("Participants").child(firebaseAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(getContext(), "Room left", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if (text_leave.getText().toString().equals("Delete")){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(roomId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(getContext(), "Room Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return view;
    }

    private void uploadInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Rooms rooms = dataSnapshot.getValue(Rooms.class);
                    try{
                        Glide.with(getContext()).load(rooms.getGroupIcon()).into(room_image);
                        room_name.setText(rooms.getRoomTitle());
                        room_details.setText(rooms.getRoomDescription());
                        event_date.setText(rooms.getEventDate());
                        created_by.setText(rooms.getCreatedBy());

                        if (firebaseAuth.getCurrentUser().getUid().equals(rooms.getCreatedBy())){
                            edit.setVisibility(View.VISIBLE);
                            text_leave.setText("Delete");
                        }else{
                            edit.setVisibility(View.GONE);
                            text_leave.setText("Leave");
                        }
                    }catch (Exception e){
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

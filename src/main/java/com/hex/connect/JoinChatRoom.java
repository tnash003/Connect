package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.Adapter.GroupAdapter;
import com.hex.connect.Modal.Rooms;

import java.util.ArrayList;

public class JoinChatRoom extends AppCompatActivity {

    RecyclerView recycler_view;
    LinearLayout joinRoom;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    private ArrayList<Rooms> mRooms;
    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_chat_room);

        recycler_view = findViewById(R.id.recycler_view);
        joinRoom = findViewById(R.id.joinRoom);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        recycler_view.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        mRooms = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, mRooms);
        recycler_view.setAdapter(groupAdapter);

        firebaseAuth = FirebaseAuth.getInstance();

        loadRoomList();
    }

    private void loadRoomList() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRooms.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(firebaseAuth.getCurrentUser().getUid()).exists()){
                        Rooms rooms = ds.getValue(Rooms.class);
                        mRooms.add(rooms);
                    }
                }

                groupAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
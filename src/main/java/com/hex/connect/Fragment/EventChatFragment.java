package com.hex.connect.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.Adapter.MessageAdapter;
import com.hex.connect.Chat.ImageMessageActivity;
import com.hex.connect.Modal.Message;
import com.hex.connect.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class EventChatFragment extends Fragment {

    String roomId;
    FirebaseAuth firebaseAuth;
    ImageButton sendBtn;
    ImageView attach;
    EditText textMessage;
    RecyclerView recyclerView;
    ArrayList<Message> mMessage;
    MessageAdapter messageAdapter;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_event_chat, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        roomId = preferences.getString("roomId", "none");

        firebaseAuth = FirebaseAuth.getInstance();

        sendBtn = view.findViewById(R.id.sendBtn);
        attach = view.findViewById(R.id.attach);
        textMessage = view.findViewById(R.id.textMessage);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(), mMessage);
        recyclerView.setAdapter(messageAdapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(textMessage.getText().toString().trim())){
                    Toast.makeText(getContext(), "Can't send empty message.", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(textMessage.getText().toString().trim());
                }
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageMessageActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });

        loadMessage();

        return view;
    }

    //TODO: add query for 20 messages
    private void loadMessage() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(roomId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessage.clear();
                for (DataSnapshot ds :dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    mMessage.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String trim) {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy   hh:mm:ss a");
        String timestamp = simpleDateFormat.format(calender.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");

        String messageId = reference.push().getKey();

        HashMap<String , Object> hashMap = new HashMap<>();

        hashMap.put("sender" , firebaseAuth.getCurrentUser().getUid());
        hashMap.put("message" , trim);
        hashMap.put("timestamp" , timestamp);
        hashMap.put("type" , "text");
        hashMap.put("messageId", messageId);
        hashMap.put("imageurl","");

        reference.child(roomId).child("Messages").child(messageId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                textMessage.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

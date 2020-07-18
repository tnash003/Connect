package com.hex.connect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.ChatActivity;
import com.hex.connect.Modal.Message;
import com.hex.connect.Modal.Rooms;
import com.hex.connect.Modal.User;
import com.hex.connect.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.HolderGroupAdapter> {

    private Context mContext;
    private List<Message> mMessage;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private FirebaseAuth firebaseAuth;

    public MessageAdapter(Context mContext, List<Message> mRooms) {
        this.mContext = mContext;
        this.mMessage = mRooms;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_left, parent , false);
            return new HolderGroupAdapter(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_right, parent , false);
            return new HolderGroupAdapter(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderGroupAdapter holder, int position) {
        final Message message = mMessage.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(message.getSender());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.sender_name.setText(user.getUsername());
                try{
                    Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
                }catch (Exception e){
                    Glide.with(mContext)
                            .load("https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924")
                            .into(holder.image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(mContext, ""+message.getImageurl(), Toast.LENGTH_SHORT).show();
        if (!message.getImageurl().equals("")){
            holder.cardView.setVisibility(View.VISIBLE);
            holder.message_text.setVisibility(GONE);
            Glide.with(mContext).load(message.getImageurl()).into(holder.image_message);
        }else{
            holder.cardView.setVisibility(GONE);
            holder.message_text.setVisibility(View.VISIBLE);
            holder.message_text.setText(message.getMessage());
        }


        holder.date.setText(message.getTimestamp());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.date.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessage.get(position).getSender().equals(firebaseAuth.getCurrentUser().getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    public class HolderGroupAdapter extends RecyclerView.ViewHolder{

        private CircleImageView image_profile;
        private TextView sender_name,message_text,date;
        private ImageView image_message;
        private CardView cardView;

        public HolderGroupAdapter(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            date = itemView.findViewById(R.id.date);
            sender_name = itemView.findViewById(R.id.sender_name);
            message_text = itemView.findViewById(R.id.message_text);
            image_message = itemView.findViewById(R.id.image_message);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}

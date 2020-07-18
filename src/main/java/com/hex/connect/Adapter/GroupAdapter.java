package com.hex.connect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hex.connect.ChatActivity;
import com.hex.connect.Modal.Rooms;
import com.hex.connect.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.HolderGroupAdapter> {

    private Context mContext;
    private List<Rooms> mRooms;

    public GroupAdapter(Context mContext, List<Rooms> mRooms) {
        this.mContext = mContext;
        this.mRooms = mRooms;
    }

    @NonNull
    @Override
    public HolderGroupAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_item, parent , false);
        return new HolderGroupAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupAdapter holder, int position) {
        final Rooms rooms = mRooms.get(position);
         holder.grp_name.setText(rooms.getRoomTitle());
         try{
             Glide.with(mContext).load(rooms.getGroupIcon()).into(holder.image_profile);
         }catch (Exception e){
             Glide.with(mContext)
                     .load("https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924")
                     .into(holder.image_profile);
         }

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                            editor.putString("roomId", rooms.getRoomId());
                            editor.apply();
                 Intent intent = new Intent(mContext, ChatActivity.class);
//                 intent.putExtra("roomId", rooms.getRoomId());
                 mContext.startActivity(intent);
             }
         });
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public class HolderGroupAdapter extends RecyclerView.ViewHolder{

        private CircleImageView image_profile;
        private TextView grp_name,sender,message;

        public HolderGroupAdapter(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            grp_name = itemView.findViewById(R.id.grp_name);
            sender = itemView.findViewById(R.id.sender);
            message = itemView.findViewById(R.id.message);

        }
    }
}

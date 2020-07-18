package com.hex.connect.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.renderscript.Sampler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.EditProfileActivity;
import com.hex.connect.Modal.Links;
import com.hex.connect.Modal.User;
import com.hex.connect.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    FirebaseUser firebaseUser;
    String profileid;

    Spanned insta_s,snapchat_s,youtube_s,other_s;
    //Variables
    CircleImageView image_profile;
    TextView username, birthday , email , bio , insta , snapchat, youtube,other;
    RelativeLayout relLayout1;
    Button editProfile;

    //Linear layouts
    LinearLayout linLayout1,linLayout2;
    TextView link_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        profileid = preferences.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        username = view.findViewById(R.id.username);
        birthday = view.findViewById(R.id.birthday);
        email = view.findViewById(R.id.email);
        bio = view.findViewById(R.id.bio);
        insta = view.findViewById(R.id.insta);
        snapchat = view.findViewById(R.id.snapchat);
        youtube = view.findViewById(R.id.youtube);
        other = view.findViewById(R.id.other);
        relLayout1 = view.findViewById(R.id.relLayout1);
        editProfile = view.findViewById(R.id.editProfile);
        linLayout1 = view.findViewById(R.id.linLayout1);
        linLayout2 = view.findViewById(R.id.linLayout2);
        link_text = view.findViewById(R.id.link_text);

        linLayout2.setVisibility(View.GONE);

//        text  = Html.fromHtml(
//                "<a href='" + getResources().getString(R.string.hello)+"'>Instagram</a>");

//        insta.setMovementMethod(LinkMovementMethod.getInstance());
//        insta.setText(text);

        if (!firebaseUser.getUid().equals(profileid)){
            relLayout1.setVisibility(View.GONE);
        }else{
            relLayout1.setVisibility(View.VISIBLE);
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("profileid", profileid);
                startActivity(intent);
            }
        });

        userInfo();
        checkLink();

        return view;
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                birthday.setText(user.getBirthday());
                email.setText(firebaseUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkLink(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Links").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Links links = dataSnapshot.getValue(Links.class);
                if (!links.getInsta().equals("")){
                    insta_s  = Html.fromHtml(
                            "<a href='" + Uri.parse(links.getInsta())+"'>Instagram</a>");
                    insta.setMovementMethod(LinkMovementMethod.getInstance());
                    insta.setText(insta_s);
                }else{
                    insta.setText(R.string.insta);
                }

                if (!links.getSnapchat().equals("")){
                    snapchat_s  = Html.fromHtml(
                            "<a href='" +Uri.parse(links.getSnapchat())+"'>SnapChat</a>");

                    snapchat.setMovementMethod(LinkMovementMethod.getInstance());
                    snapchat.setText(snapchat_s);
                }else{
                    snapchat.setText(R.string.snapchat);
                }

                if (!links.getYoutube().equals("")){
                    youtube_s  = Html.fromHtml(
                            "<a href='" +Uri.parse(links.getYoutube())+"'>YouTube</a>");

                    youtube.setMovementMethod(LinkMovementMethod.getInstance());
                    youtube.setText(youtube_s);
                }else{
                    youtube.setText(R.string.youtube);
                }

                if (!links.getOther().equals("")){
                    other_s  = Html.fromHtml(
                            "<a href='" + Uri.parse(links.getOther())+"'>Other</a>");

                    other.setMovementMethod(LinkMovementMethod.getInstance());
                    other.setText(other_s);
                }else{
                    other.setText(R.string.other);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
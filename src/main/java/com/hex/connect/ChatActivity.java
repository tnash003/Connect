package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.Fragment.EventChatFragment;
import com.hex.connect.Fragment.PriorityMessage;
import com.hex.connect.Fragment.QRFragment;
import com.hex.connect.Fragment.RoomInfo;

public class ChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    String roomId;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        roomId = preferences.getString("roomId", "none");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        firebaseAuth = FirebaseAuth.getInstance();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
          this,drawerLayout,toolbar,R.string.openNavBar,R.string.navBarClose
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new EventChatFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_chat);

        setTitle();

    }

    private void setTitle(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("roomId").equalTo(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    getSupportActionBar().setTitle(""+ds.child("roomTitle").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new EventChatFragment()).commit();
                break;
            case R.id.nav_p_messages:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PriorityMessage()).commit();
                navigationView.setCheckedItem(R.id.nav_p_messages);
                break;
            case R.id.nav_share:
                shareLink();
                break;
            case R.id.nav_qr:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new QRFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_qr);
                break;
            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RoomInfo()).commit();
                navigationView.setCheckedItem(R.id.nav_info);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareLink() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "Join chat room : ";
        intent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
        intent.putExtra(Intent.EXTRA_TEXT, "https://pindle.page.link/?link=connect.com/?id="+roomId
                +"&apn=com.hex.connect");
        startActivity(Intent.createChooser(intent,"Share Using"));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
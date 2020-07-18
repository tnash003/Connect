package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.hex.connect.publicChatRoom.CreatePublicRoom;

public class MainActivity extends AppCompatActivity implements BottomSheetOptions.bottomSheetListener , GoogleApiClient.OnConnectionFailedListener{

    ImageView options,home;
    LinearLayout createRoom,chatRooms;
    int number;
    public static TextView scan , result;
    private static final int PERMISSION_CODE = 2;
    private GoogleApiClient googleApiClient;

    //Dialog box variables
    Dialog dialog;
    ImageView image;
    Button btn_event, btn_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = findViewById(R.id.options);
        home = findViewById(R.id.home);
        createRoom = findViewById(R.id.createRoom);
        chatRooms = findViewById(R.id.chatRooms);
        scan = findViewById(R.id.scan);
        result = findViewById(R.id.result);

        String str = "https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2F5234.jpg?alt=media&token=3a60683e-35e5-448b-a134-7599e5918d83";
        Glide.with(this).load(str).into(home);

        dialog = new Dialog(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this).
                        addApi(AppInvite.API).
                        build();
        googleApiClient.connect();

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog();
            }
        });

        chatRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JoinChatRoom.class));
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetOptions bottomSheet = new BottomSheetOptions();
                bottomSheet.show(getSupportFragmentManager(),"bottomSheet");
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    String[] permission = {Manifest.permission.CAMERA};
                    requestPermissions(permission,PERMISSION_CODE);
                }else{
                    startActivity(new Intent(MainActivity.this, ScanCodeActivity.class));
                }
            }
        });

//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
//
//        topText = findViewById(R.id.topText);
//

//
//        str = "";

       AppInvite.AppInviteApi.getInvitation(googleApiClient,this,false)
               .setResultCallback(new ResultCallback<AppInviteInvitationResult>() {
                   @Override
                   public void onResult(@NonNull AppInviteInvitationResult appInviteInvitationResult) {
                       if (appInviteInvitationResult.getStatus().isSuccess()){
                           Intent intent = appInviteInvitationResult.getInvitationIntent();
                           String deepLink = AppInviteReferral.getDeepLink(intent);
                           String uid= deepLink.substring(deepLink.lastIndexOf("?")+4,deepLink.lastIndexOf("&")-1);

                           SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                           editor.putString("roomId",uid);
                           editor.apply();
                           Intent i = new Intent(MainActivity.this, ChatActivity.class);
                           startActivity(i);
                       }
                   }
               });
    }

    @Override
    public void onTextClicked(int i) {
        number = i;
        getActivity();
    }

    private void getActivity() {
        if (number == 1){
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA};
                requestPermissions(permission,PERMISSION_CODE);
            }else{
                startActivity(new Intent(MainActivity.this, ScanCodeActivity.class));
            }

        }else if (number == 2){
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            editor.apply();
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));

        }else if (number == 3){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBody = "Download this app now : ";
            String str = "Connect";
            intent.putExtra(Intent.EXTRA_SUBJECT,str);
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(intent,"Share Using"));
        }else if (number == 4){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void newDialog(){
        dialog.setContentView(R.layout.create_room_dialog);
        image = dialog.findViewById(R.id.image_pop_up);
        btn_event = dialog.findViewById(R.id.btn_event);
        btn_place = dialog.findViewById(R.id.btn_place);

//        Glide.with(ChangePasswordActivity.this)
//                .load(R.drawable.contact)
//                .into(image);


        btn_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateEventRoomActivity.class));
            }
        });

        btn_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreatePublicRoom.class));
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed"+ connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

//    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//
//                    switch (menuItem.getItemId()){
//                        case R.id.nav_home:
//                            selectedFragment = new EventChatFragment();
//                            str = "Home";
//                            break;
//                        case R.id.nav_community:
//                            selectedFragment = new QRFragment();
//                            str = "Community";
//                            break;
//                        case R.id.nav_add:
//                            startActivity(new Intent(MainActivity.this,AddActivity.class));
//                            str = "Add";
//                            break;
//                        case R.id.nav_chat:
//                            selectedFragment = new PriorityMessage();
//                            str = "Chat Room";
//                            break;
//                        case R.id.nav_profile:
//                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                            editor.apply();
//                            selectedFragment = new ProfileFragment();
//                            str = "Profile";
//                            break;
//                    }
//
//                    if (selectedFragment != null){
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                                selectedFragment).commit();
//                        topText.setText(str);
//                    }
//
//                    return true;
//                }
//            };
}
package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    MaterialEditText send_email;
    Button btn_send;
    FirebaseAuth auth;
    ImageView backarrow;
    TextView emailus;

    //Dialog box variables
    Dialog dialog;
    ImageView close,image;
    Button btn_review, btn_copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        send_email = findViewById(R.id.send_email);
        btn_send = findViewById(R.id.btn_reset);
        backarrow = findViewById(R.id.backarrow);
        emailus = findViewById(R.id.emailus);

        //Dialog variables
        dialog = new Dialog(this);

        auth = FirebaseAuth.getInstance();

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        emailus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = send_email.getText().toString();
                if (email.equals("")){
                    Toast.makeText(ChangePasswordActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }else{
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ChangePasswordActivity.this, "Email sent! Please check your inbox", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ChangePasswordActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void newDialog(){
        dialog.setContentView(R.layout.pop_up_dialog);
        image = dialog.findViewById(R.id.image_pop_up);
        btn_copy = dialog.findViewById(R.id.btn_copy);

        Glide.with(ChangePasswordActivity.this)
                .load(R.drawable.contact)
                .into(image);


        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Email","cbusinessxd@gmail.com");
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(ChangePasswordActivity.this, "Copied to clip board.", Toast.LENGTH_SHORT).show();
            }
        });

//        btn_review.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("market://details?id="+ getPackageName())));
//                }catch (Exception e){
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("https://play.google.com/store/apps/details?id=com.DandA.wallpaper")));
//                }
//            }
//        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
    }
}

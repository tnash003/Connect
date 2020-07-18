package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EnterPasswordActivity extends AppCompatActivity {

    String username, email;
    Button register;
    TextView txtEmail;
    TextInputLayout password;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        register = findViewById(R.id.register);
        txtEmail = findViewById(R.id.textEmail);
        password = findViewById(R.id.password);

        txtEmail.setText(email);

        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput();
            }
        });
    }

    private boolean validPassword() {
        String uname = password.getEditText().getText().toString();

        if (uname.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        }else if (uname.length() < 8){
            password.setError("Minimum 8 characters required");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    private void confirmInput()
    {
        if ( !validPassword() ){
            return;
        }

        pd = new ProgressDialog(EnterPasswordActivity.this);
        pd.setMessage("Registering. Please wait..");
        pd.show();

        register(username , email , password.getEditText().getText().toString());

    }

    private void register(final String username , String email , final String password){
        auth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(EnterPasswordActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Links").child(userid);
                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("insta", "");
                            hm.put("snapchat" ,"");
                            hm.put("youtube","");
                            hm.put("other","");

                            ref.setValue(hm);

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username" , username.toLowerCase());
                            hashMap.put("birthday","-");
                            hashMap.put("bio","No Details added");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/connect-4d422.appspot.com/o/default%2Fdefault_image.png?alt=media&token=7ac4b40e-2303-4014-9803-cec8541a2924");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent =  new Intent(EnterPasswordActivity.this , MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }else{
                            pd.dismiss();
                            Toast.makeText(EnterPasswordActivity.this, "You can't register with this email or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

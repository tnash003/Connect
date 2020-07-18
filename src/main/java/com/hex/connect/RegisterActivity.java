package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout username ,  email ;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
//        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput();
            }
        });
    }

    private boolean validUsername() {
        String uname = username.getEditText().getText().toString();

        if (uname.isEmpty()) {
            username.setError("Field can't be empty");
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }

    private boolean validemail(){
        String email1 = email.getEditText().getText().toString();

        if (email1.isEmpty()){
            email.setError("Field can't be empty");
            return false;
        }else{
            email.setError(null);
            return true;
        }
    }

    private void confirmInput()
    {
        if ( !validUsername() | !validemail()){
            return;
        }

        Intent intent = new Intent(RegisterActivity.this, EnterPasswordActivity.class);
        intent.putExtra("username", username.getEditText().getText().toString());
        intent.putExtra("email", email.getEditText().getText().toString());
        startActivity(intent);
    }
}

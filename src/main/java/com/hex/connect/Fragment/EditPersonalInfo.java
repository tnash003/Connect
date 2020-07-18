package com.hex.connect.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hex.connect.EnterPasswordActivity;
import com.hex.connect.Modal.User;
import com.hex.connect.R;

import java.util.HashMap;

public class EditPersonalInfo extends Fragment {

    TextInputLayout username;
    TextView textEmail, birthday;
    LinearLayout edit_birthday;
    Button save;
    FirebaseUser firebaseUser;
    EditText edit_username;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_personal_info, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        username = view.findViewById(R.id.username);
        textEmail = view.findViewById(R.id.textEmail);
        birthday = view.findViewById(R.id.birthday);
        edit_birthday = view.findViewById(R.id.edit_birthday);
        save = view.findViewById(R.id.save);
        edit_username = view.findViewById(R.id.edit_username);

        long today = MaterialDatePicker.todayInUtcMilliseconds();

//        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
//        constraintsBuilder.setValidator(CalendarConstraints.DateValidator.)

        //material date picker
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(today);

        builder.setTitleText("Choose Birth Date");

        final MaterialDatePicker materialDatePicker = builder.build();

        edit_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(),"DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                birthday.setText(materialDatePicker.getHeaderText());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validUsername()){
                    return;
                }else{
                    pd = new ProgressDialog(getContext());
                    pd.setMessage("Updating Profile");
                    pd.show();
                    saveInfo();
                }
            }
        });

        userInfo();

        return view;
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                edit_username.setText(user.getUsername());
                birthday.setText(user.getBirthday());
                textEmail.setText(firebaseUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("username" , edit_username.getText().toString());
        hashMap.put("birthday" , birthday.getText().toString());

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                }else{
                    pd.dismiss();
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
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
}

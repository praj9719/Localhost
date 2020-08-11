package com.eklavya.localhost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText mName, mEmail, mPassword;
    private Button mRegister;
    private TextView mLogin;
    private ProgressBar mProgress;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName = findViewById(R.id.activity_register_edit_text_name);
        mEmail = findViewById(R.id.activity_register_edit_text_email);
        mPassword = findViewById(R.id.activity_register_edit_text_password);
        mRegister = findViewById(R.id.activity_register_button_register);
        mLogin = findViewById(R.id.activity_register_text_view_login);
        mProgress = findViewById(R.id.activity_register_progressbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("User");

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setVisibility(View.VISIBLE);
                registerActivity();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void registerActivity() {
    String email = mEmail.getText().toString().trim();
    String password = mPassword.getText().toString().trim();
    String name = mName.getText().toString().trim();
    if(!mEmail.getText().toString().trim().equals("") && !password.equals("") && !name.equals("")){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_SHORT).show();
                        appendUserInDatabase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }else {
        Toast.makeText(RegisterActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
    }
    }

    private void appendUserInDatabase() {
        String userId = mDatabaseReference.push().getKey();
        User user = new User(userId, mEmail.getText().toString(), mName.getText().toString().trim());
        mDatabaseReference.child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgress.setVisibility(View.VISIBLE);
                        Toast.makeText(RegisterActivity.this, "Registered Success", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgress.setVisibility(View.INVISIBLE);
                    }
                });
    }
}

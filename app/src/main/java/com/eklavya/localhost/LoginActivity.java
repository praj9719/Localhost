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

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin;
    private TextView mRegister;
    private ProgressBar mProgress;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.activity_login_edit_text_email);
        mPassword = findViewById(R.id.activity_login_edit_text_password);
        mLogin = findViewById(R.id.activity_login_button_login);
        mRegister = findViewById(R.id.activity_login_text_view_create_account);
        mProgress = findViewById(R.id.activity_login_progressbar);

        mAuth = FirebaseAuth.getInstance();
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setVisibility(View.VISIBLE);
                loginActivity();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginActivity() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (!mEmail.getText().toString().trim().equals("")&&!password.equals("")){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(LoginActivity.this, "Invalid syntex", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.eklavya.localhost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView mName;
    private TextView mEmail;
    private RecyclerView mRecyclerView;
    private List<Host> mList;
    private static String UserEmail;
    private static String UserName;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        UserName = intent.getStringExtra("UserName");
        UserEmail = intent.getStringExtra("UserEmail");
        mName = findViewById(R.id.activity_profile_name);
        mEmail = findViewById(R.id.activity_profile_email);
        mRecyclerView = findViewById(R.id.activity_profile_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Service");
        mList = new ArrayList<>();
        mName.setText(UserName);
        mEmail.setText(UserEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                    Host host = mySnap.getValue(Host.class);
                    if (host.getFrom().equals(UserEmail)){
                        mList.add(host);
                    }
                }
                ServiceAdapter serviceAdapter = new ServiceAdapter(ProfileActivity.this, mList);
                mRecyclerView.setAdapter(serviceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

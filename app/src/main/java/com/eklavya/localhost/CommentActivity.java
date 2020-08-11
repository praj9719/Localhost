package com.eklavya.localhost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private TextView mTitle, mDetails, mPost;
    private EditText mComment;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private List<Comment> mCommentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        String hostId = intent.getStringExtra("HostId");
        String title = intent.getStringExtra("Title");
        String details = intent.getStringExtra("Details");
        mAuth = FirebaseAuth.getInstance();
        mTitle = findViewById(R.id.activity_comment_text_view_title);
        mDetails = findViewById(R.id.activity_comment_text_view_details);
        mPost = findViewById(R.id.activity_comment_text_view_post);
        mComment = findViewById(R.id.activity_comment_edit_text_comment);
        mRecyclerView = findViewById(R.id.activity_comment_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommentList = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Service").child(hostId).child("Comments");
        mTitle.setText(title);
        mDetails.setText(details);
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentActivity();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCommentList.clear();
                for (DataSnapshot CommentSnap : dataSnapshot.getChildren()){
                    Comment comment = CommentSnap.getValue(Comment.class);
                    mCommentList.add(comment);
                }
                CommentAdapter commentAdapter = new CommentAdapter(CommentActivity.this, mCommentList);
                mRecyclerView.setAdapter(commentAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void commentActivity() {
        if (!mComment.getText().toString().trim().equals("")){
            String commentId = mDatabaseReference.push().getKey();
            String from = mAuth.getCurrentUser().getEmail();
            String message = mComment.getText().toString().trim();
            Comment comment = new Comment(commentId, from, message);

            mDatabaseReference.child(commentId).setValue(comment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CommentActivity.this, "Comment posted", Toast.LENGTH_SHORT).show();
                            mComment.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

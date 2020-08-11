package com.eklavya.localhost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class HostActivity extends AppCompatActivity {
    private EditText mTitle, mDetails, mLink, mContact;
    private TextView mTextImage, mWait, mLocation;
    private ProgressBar mProgress;
    private ImageView mImageView;
    private Button mHost;
    private Spinner mSpinner;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri imgUri;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        Intent intent = getIntent();
        final String latitude = intent.getStringExtra("Latitude");
        final String longitude = intent.getStringExtra("Longitude");

        mTitle = findViewById(R.id.activity_host_edit_text_title);
        mDetails = findViewById(R.id.activity_host_edit_text_details);
        mLink = findViewById(R.id.activity_host_edit_text_link);
        mContact = findViewById(R.id.activity_host_edit_text_contact);
        mTextImage = findViewById(R.id.activity_host_text_view_add_image);
        mLocation = findViewById(R.id.activity_host_text_view_location);
        mHost = findViewById(R.id.activity_host_button_host);
        mImageView = findViewById(R.id.activity_host_image_view);
        mSpinner = findViewById(R.id.activity_host_spinner);
        mWait = findViewById(R.id.activity_host_text_view_wait);
        mProgress = findViewById(R.id.activity_host_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Service");
        mStorageReference = FirebaseStorage.getInstance().getReference("Service");

        mLocation.setText("Latitude:\t"+latitude+"\nLongitude:\t"+longitude);

        mTextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallary();
                mImageView.setVisibility(View.VISIBLE);
            }
        });

        mHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTitle.getText().toString().trim().equals("")
                        && !imgUri.toString().trim().equals("")
                        && !mDetails.toString().trim().equals("")
                        && !mLink.toString().trim().equals("")
                        &&!mContact.toString().trim().equals("")){
                    mProgress.setVisibility(View.VISIBLE);
                    mWait.setVisibility(View.VISIBLE);
                    hostActivity(latitude, longitude);
                }
                else {
                    Toast.makeText(HostActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void hostActivity(final String latitude, final String longitude) {
        final StorageReference fileRefrence =
                mStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(imgUri));
        fileRefrence.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String id = mDatabaseReference.push().getKey();
                                String from = mAuth.getCurrentUser().getEmail();
                                String title = mTitle.getText().toString().trim();
                                String imguri = uri.toString();
                                String details = mDetails.getText().toString().trim();
                                String category = mSpinner.getSelectedItem().toString();
                                String link = mLink.getText().toString().trim();
                                String contact = mContact.getText().toString().trim();
                                Host host = new Host(id, from, title, imguri, details, category, link, contact, latitude, longitude, 0, 0, 0);
                                mDatabaseReference.child(id).setValue(host)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(HostActivity.this, "Service Added", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(new Intent(HostActivity.this, MainActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(HostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                mProgress.setVisibility(View.GONE);
                                                mWait.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgress.setVisibility(View.GONE);
                        mWait.setVisibility(View.GONE);
                    }
                });
    }


    private void openGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ){
            imgUri = data.getData();
            mImageView.setImageURI(imgUri);
        }
    }

}

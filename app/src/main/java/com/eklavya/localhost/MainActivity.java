package com.eklavya.localhost;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseUserReference;
    private FusedLocationProviderClient fusedLocationClient;
    private Double latitude = 0.0, longitude = 0.0 ;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static boolean firstTime = false;
    private static boolean firstCheck = true;
    private static String UserName;
    private static String UserEmail;
    private RecyclerView mRecyclerView;
    private List<Host> mList;
    private List<Host> mFilterdList;
    private ProgressBar mProgress;
    private LinearLayout mLinearLayout;
    private Button mFilter;
    private Spinner mCategory;
    private Spinner mDistance;
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Service");
        mDatabaseUserReference = FirebaseDatabase.getInstance().getReference("User");
        mRecyclerView = findViewById(R.id.content_main_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mFilterdList = new ArrayList<>();
        mSwipeRefresh = findViewById(R.id.content_main_swipe_refresh);
        mProgress = findViewById(R.id.content_main_progress_bar);
        mLinearLayout = findViewById(R.id.content_main_linear_layout_inner);
        mFilter = findViewById(R.id.content_main_button_filter);
        mCategory = findViewById(R.id.content_main_spinner_category);
        mDistance = findViewById(R.id.content_main_spinner_distance);
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterActivity();
                mLinearLayout.setVisibility(View.GONE);
            }
        });
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                filterActivity();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            mLinearLayout.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            //Home
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("UserName", UserName);
            intent.putExtra("UserEmail", UserEmail);
            startActivity(intent);

        } else if (id == R.id.nav_host) {
            fetchLocation();
            Intent intent = new Intent(MainActivity.this, HostActivity.class);
            intent.putExtra("Latitude", latitude.toString());
            intent.putExtra("Longitude", longitude.toString());
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logout success!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (mUser==null) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            UserEmail = mUser.getEmail();
            mDatabaseUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnap:dataSnapshot.getChildren()){
                        User user = userSnap.getValue(User.class);
                        if (user.getEmail().equals(UserEmail)){
                            UserName = user.getName();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            fetchLocation();
            mProgress.setVisibility(View.VISIBLE);
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mList.clear();
                    if (firstCheck){
                        firstTime = true;
                        firstCheck = false;
                    }
                    for (DataSnapshot mySnap : dataSnapshot.getChildren()) {
                        Host host = mySnap.getValue(Host.class);
                        mList.add(host);
                    }
                    for (int i = 0; i < mList.size() - 1; i++) {
                        for (int j = i; j < mList.size(); j++) {

                            Double hostLat_i = Double.parseDouble(mList.get(i).getLatitude());
                            Double hostLong_i = Double.parseDouble(mList.get(i).getLongitude());
                            Double diff_i = (Math.pow(latitude - hostLat_i, 2)
                                    + Math.pow(longitude - hostLong_i, 2)) * 10000000;

                            Double hostLat_j = Double.parseDouble(mList.get(j).getLatitude());
                            Double hostLong_j = Double.parseDouble(mList.get(j).getLongitude());
                            Double diff_j = (Math.pow(latitude - hostLat_j, 2)
                                    + Math.pow(longitude - hostLong_j, 2)) * 10000000;

                            if (diff_i > diff_j) {
                                swapObjects(mList.get(i), mList.get(j));
                            }
                        }
                    }
                    mProgress.setVisibility(View.INVISIBLE);
                    if (firstTime){
                        firstTime = false;
                        mLinearLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void filterActivity() {
        mFilterdList.clear();
        String Category = mCategory.getSelectedItem().toString();
        int DistancePos = mDistance.getSelectedItemPosition();
        Double Distance =10000000.0;
        if (DistancePos == 1){
            Distance = 2500.0;
        }
        else if (DistancePos == 2){
            Distance = 6000.0;
        }
        else if (DistancePos == 3){
            Distance = 12000.0;
        }
        for (int i = 0; i < mList.size() - 1; i++) {
            Double hostLat = Double.parseDouble(mList.get(i).getLatitude());
            Double hostLong = Double.parseDouble(mList.get(i).getLongitude());
            Double dist = (Math.pow(latitude - hostLat, 2)
                    + Math.pow(longitude - hostLong, 2)) * 10000000;
            if (dist < Distance){
                if (Category.equals(mList.get(i).getCategory()) || Category.equals("All")){
                    String id = mList.get(i).getId();
                    String from = mList.get(i).getFrom();
                    String title = mList.get(i).getTitle();
                    String imgUri = mList.get(i).getImgUri();
                    String details = mList.get(i).getDetails();
                    String cat = mList.get(i).getCategory();
                    String link = mList.get(i).getLink();
                    String contact = mList.get(i).getContact();
                    String latitude = mList.get(i).getLatitude();
                    String longitude = mList.get(i).getLongitude();
                    int great = mList.get(i).getGreat();
                    int good = mList.get(i).getGood();
                    int ok = mList.get(i).getOk();
                    Host newHost = new Host(id, from, title, imgUri, details, cat, link, contact, latitude, longitude, great, good, ok);
                    mFilterdList.add(newHost);
                }
            }
        }
        ServiceAdapter serviceAdapter = new ServiceAdapter(MainActivity.this, mFilterdList);
        mRecyclerView.setAdapter(serviceAdapter);

    }

    private void swapObjects(Host host1, Host host2) {
        Host temp = new Host("id", "from", "title", "imgUri", "details", "category", "link", "contact", "latitude", "longitude", 0, 0, 0);

        temp.setId(host1.getId());
        temp.setFrom(host1.getFrom());
        temp.setTitle(host1.getTitle());
        temp.setImgUri(host1.getImgUri());
        temp.setDetails(host1.getDetails());
        temp.setCategory(host1.getCategory());
        temp.setLink(host1.getLink());
        temp.setContact(host1.getContact());
        temp.setLatitude(host1.getLatitude());
        temp.setLongitude(host1.getLongitude());
        temp.setGreat(host1.getGreat());
        temp.setGood(host1.getGood());
        temp.setOk(host1.getOk());

        host1.setId(host2.getId());
        host1.setFrom(host2.getFrom());
        host1.setTitle(host2.getTitle());
        host1.setImgUri(host2.getImgUri());
        host1.setDetails(host2.getDetails());
        host1.setCategory(host2.getCategory());
        host1.setLink(host2.getLink());
        host1.setContact(host2.getContact());
        host1.setLatitude(host2.getLatitude());
        host1.setLongitude(host2.getLongitude());
        host1.setGreat(host2.getGreat());
        host1.setGood(host2.getGood());
        host1.setOk(host2.getOk());

        host2.setId(temp.getId());
        host2.setFrom(temp.getFrom());
        host2.setTitle(temp.getTitle());
        host2.setImgUri(temp.getImgUri());
        host2.setDetails(temp.getDetails());
        host2.setCategory(temp.getCategory());
        host2.setLink(temp.getLink());
        host2.setContact(temp.getContact());
        host2.setLatitude(temp.getLatitude());
        host2.setLongitude(temp.getLongitude());
        host2.setGreat(temp.getGreat());
        host2.setGood(temp.getGood());
        host2.setOk(temp.getOk());

    }


    private void fetchLocation() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Required Location Permission!")
                        .setMessage("You have to give this permission to access the feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            }
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //runs after location is granted
            } else {

            }
        }
    }

}



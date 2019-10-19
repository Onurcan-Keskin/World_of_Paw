package com.example.wop;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wop.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

public class Home extends AppCompatActivity {

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;

    Uri uriProfileImage;

    private ImageView hImageView;

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;
    private ConnectivityManagern c;
    User mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.dark_theme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.nav_home);
        setContentView(R.layout.activity_home);

        gDrawer=(DrawerLayout) findViewById(R.id.drawer_home);
        gNavigation=(NavigationView) findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());

        /*Header*/
        hImageView= findViewById(R.id.header_user_pic);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile").child(current_uid);
        mDatabaseRef.keepSynced(true);

        /*if (mAuth.getCurrentUser()!=null){
            mUserRef = FirebaseDatabase.getInstance().getReference()
                    .child("user_profile")
                    .child(mAuth.getCurrentUser().getUid());
        }*/

        /*
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();


                if (!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.wop_new)
                            .into(hImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.wop_new).into(hImageView);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this,databaseError.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });*/

        MyHomeMenu();
    }

    private void MyHomeMenu() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id= menuItem.getItemId();

                switch (id){
                    case R.id.account:
                        intent=new Intent(Home.this,AccountSettings.class);
                        startActivity(intent);
                        break;
                    case R.id.mypet:
                        intent=new Intent(Home.this,MyPet.class);
                        startActivity(intent);
                        break;
                    case R.id.message:
                        intent=new Intent(Home.this,Messaging.class);
                        startActivity(intent);
                        break;
                    case R.id.lost:
                        intent = new Intent(Home.this, Lost.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent=new Intent(Home.this,Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        mAuth.signOut();
                        intent=new Intent(Home.this,MainActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void downloadImageFromFirebaseStorage(){
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if(uriProfileImage != null){
            profileImageRef.getFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            intent = new Intent(this, Home.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendtoStart();
        }else {
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCurrentUser != null){
            mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendtoStart() {
        intent = new Intent(Home.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }
}

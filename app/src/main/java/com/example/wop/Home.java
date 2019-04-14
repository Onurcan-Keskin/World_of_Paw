package com.example.wop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class Home extends AppCompatActivity {

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;

    ImageView user_image;
    Uri uriProfileImage;
    String profileImageUrl;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gDrawer=(DrawerLayout) findViewById(R.id.drawer_home);
        gNavigation=(NavigationView) findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_image=(ImageView) findViewById(R.id.user_pic);
        mAuth = FirebaseAuth.getInstance();

        MyHomeMenu();
    }

    private void MyHomeMenu() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id= menuItem.getItemId();

                switch (id){
                    case R.id.mypet:
                        intent=new Intent(Home.this,MyPet.class);
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
                        intent=new Intent(Home.this,Loginn.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

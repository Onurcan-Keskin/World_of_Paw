package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class Messaging extends AppCompatActivity {

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;
    private Toolbar mToolbar;

    private ViewPager mVievPager;
    private MessagingPagerAdapter mMessagingPagerAdapter;
    private TabLayout mTabLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabaseRef;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.dark_theme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Messaging");
        setContentView(R.layout.activity_messaging);

        gDrawer=(DrawerLayout) findViewById(R.id.drawer_messaging);
        gNavigation=(NavigationView) findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        gDrawer.setDrawerTitle(Gravity.NO_GRAVITY,"Messaging");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());

        /*Tabs*/
        mVievPager = findViewById(R.id.message_pager);
        mMessagingPagerAdapter = new MessagingPagerAdapter(getSupportFragmentManager());

        mVievPager.setAdapter(mMessagingPagerAdapter);

        mTabLayout = findViewById(R.id.message_tabs);
        mTabLayout.setupWithViewPager(mVievPager);

        MyPetMenu();
    }

    private void MyPetMenu() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id= menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        intent=new Intent(Messaging.this,Home.class);
                        startActivity(intent);
                        break;
                    case R.id.account:
                        intent=new Intent(Messaging.this,AccountSettings.class);
                        startActivity(intent);
                        break;
                    case R.id.mypet:
                        intent=new Intent(Messaging.this,MyPet.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent=new Intent(Messaging.this,Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        mAuth.signOut();
                        intent=new Intent(Messaging.this,MainActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.messagingmenu,menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendtoStart();
        }else {
            mUserDatabaseRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser!=null){
            mUserDatabaseRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendtoStart() {
        intent = new Intent(Messaging.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        /*Extra Menu Inflater*/
        int id = item.getItemId();
        switch (id) {
            case R.id.message_bar_account_settings:
                intent = new Intent(Messaging.this,AccountSettings.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

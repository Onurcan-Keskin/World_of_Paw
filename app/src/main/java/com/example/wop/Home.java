package com.example.wop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

public class Home extends AppCompatActivity {

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;

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

        MyHomeMenu();
    }

    private void MyHomeMenu() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id= menuItem.getItemId();

                switch (id){
                    case R.id.settings:
                        intent=new Intent(Home.this,Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

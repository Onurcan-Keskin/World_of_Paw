package com.example.wop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Settings extends AppCompatActivity {

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;

    private Spinner spinner;
    private Locale mylocale;
    private String currentlang="en", getCurrentlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        gDrawer=(DrawerLayout) findViewById(R.id.drawer_settings);
        gNavigation=(NavigationView) findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MySettings();

        /* Language Selector Test */

        currentlang=getIntent().getStringExtra(currentlang);
        spinner=(Spinner) findViewById(R.id.lang_select_spinner);

        List<String> list=new ArrayList<String>();

        list.add("-------");
        list.add("English");
        list.add("Turkish");

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        setLocale("en");
                        break;
                    case 2:
                        setLocale("tr");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void MySettings() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id= menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        intent=new Intent(Settings.this,Home.class);
                        startActivity(intent);
                        break;
                    case R.id.mypet:
                        intent=new Intent(Settings.this,MyPet.class);
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

    public void setLocale(String localeName){
        if (!localeName.equals(currentlang)){
            mylocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();
            config.locale = mylocale;
            res.updateConfiguration(config,dm);
            Intent refresh = new Intent(this, Settings.class);
            refresh.putExtra(currentlang,localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(Settings.this,"Language is already selected",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }*/


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(),"Back button is disabled on this page.",Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Geri tuşu bu sayfada devre dışıdır.", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

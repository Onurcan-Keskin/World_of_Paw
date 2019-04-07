package com.example.wop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Intro extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro);

        addSlide(AppIntroFragment.newInstance("World of Paw Intro", "Details",
                R.drawable.ic_launcher_background, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("World of Paw Intro2", "Details",
                R.drawable.ic_launcher_background, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("World of Paw Intro3", "Details",
                R.drawable.ic_launcher_background, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        /*etc -> copy change*/
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(),Home.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(),Home.class);
        startActivity(intent);
    }
}

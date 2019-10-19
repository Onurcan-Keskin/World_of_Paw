package com.example.wop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wop.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Locale;
import java.util.regex.Pattern;

import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Loginn extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth myAuth;

    private boolean doubleBackToExit = false;

    private AutoCompleteTextView mEmailView;
    private ProgressBar progressBar;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private ProgressBar l_progress;
    private EditText edpass;
    private EditText edmail;
    private Intent intent;

    private DatabaseReference mDatabaseRef;

    float x1, x2,y1,y2;

    private ProgressDialog mProgress;

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private String currentlang="en", getCurrentlang;
    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        //findViewById(R.id.email_sign_up_button).setOnClickListener(this);
        edpass = findViewById(R.id.edtpassword);
        edmail = findViewById(R.id.edtemail);
        l_progress = findViewById(R.id.login_progress);
        onStartCount = 1;

        myAuth = FirebaseAuth.getInstance();

        currentlang=getIntent().getStringExtra(currentlang);

        mProgress = new ProgressDialog(this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile");

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);

        findViewById(R.id.lng_tr).setOnClickListener(v -> setLocale("tr"));

        findViewById(R.id.lng_en).setOnClickListener(v -> setLocale("en"));
    }

    public void setLocale(String localeName){
        if (!localeName.equals(currentlang)){
            Locale mylocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();
            config.locale = mylocale;
            res.updateConfiguration(config,dm);
            Intent refresh = new Intent(this, Loginn.class);
            refresh.putExtra(currentlang,localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(Loginn.this,R.string.error_lang_selected,Toast.LENGTH_SHORT).show();
        }
    }

    private void userlogin(){
        String email = edmail.getText().toString().trim();
        String pass = edpass.getText().toString().trim();

        if (email.isEmpty()){
            edmail.setError(getString(R.string.error_field_required));
            edmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edmail.setError(getString(R.string.error_invalid_email));
            edmail.requestFocus();
            return;
        }

        if (pass.isEmpty()){
            edpass.setError(getString(R.string.error_field_required));
            edpass.requestFocus();
            return;
        }

        if (pass.length()<6){
            edpass.setError(getString(R.string.error_invalid_password));
            edpass.requestFocus();
            return;
        }

        myAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                finish();

                final String uid = myAuth.getCurrentUser().getUid();
                //final Task<InstanceIdResult> deviceToken = FirebaseInstanceId.getInstance().getInstanceId();
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String tokenId = instanceIdResult.getToken();
                        mDatabaseRef.child(uid).child("tokenID").setValue(tokenId).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(Loginn.this,Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Loginn.this,R.string.error_global_login,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    /*
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(mRunnable, 2000);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                if (x1 < x2) {
                    Intent intent = new Intent(Loginn.this, SignUp.class);
                    startActivity(intent);
                }
                if (x1 > x2) {
                    Intent intent = new Intent(Loginn.this, SignUp.class);
                    startActivity(intent);
                }
            }
            break;
        }
        return false;
    }

    private void updateUI(FirebaseUser user){
        if (user != null){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = myAuth.getCurrentUser();
        updateUI(currentUser);
        if (onStartCount>1){
            this.overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
        }else if (onStartCount==1){
            onStartCount++;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.email_sign_up_button:
                finish();
                startActivity(new Intent(Loginn.this, SignUp.class));
                break;*/

            case R.id.email_sign_in_button:
                userlogin();
                break;
        }
    }
}

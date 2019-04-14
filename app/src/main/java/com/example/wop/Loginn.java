package com.example.wop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wop.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.regex.Pattern;

public class Loginn extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth myAuth;

    private AutoCompleteTextView mEmailView;
    private ProgressBar progressBar;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
     Button btnSignIn;
     Button btnSignUp;
     Button btn_tr;
     Button brn_en;

    private EditText edpass;
    private EditText edmail;

    private Locale mylocale;
    private String currentlang="en", getCurrentlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginn);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_sign_up_button).setOnClickListener(this);
        edpass = (EditText) findViewById(R.id.edtpassword);
        edmail = (EditText) findViewById(R.id.edtemail);

        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        myAuth = FirebaseAuth.getInstance();

        currentlang=getIntent().getStringExtra(currentlang);

        findViewById(R.id.tr_flag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("tr");
            }
        });

        findViewById(R.id.en_flag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
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

        progressBar.setVisibility(View.VISIBLE);
        myAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(Loginn.this,Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(),R.string.error_back_button,Toast.LENGTH_SHORT).show();
        return false;
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        if (myAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,ProfileAct.class));
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.email_sign_up_button:
                finish();
                startActivity(new Intent(this, SignUp.class));
                break;

            case R.id.email_sign_in_button:
                userlogin();
                break;
        }
    }
}

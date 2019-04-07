package com.example.wop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    private EditText edname;
    private EditText edpass;
    private EditText edmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginn);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_sign_up_button).setOnClickListener(this);
        edname = (EditText) findViewById(R.id.edtname);
        edpass = (EditText) findViewById(R.id.edtpassword);
        edmail = (EditText) findViewById(R.id.edtemail);

        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        myAuth = FirebaseAuth.getInstance();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference Label_user = database.getReference("User");
        final DatabaseReference table_user = database.getReference("User");
    }

    private void userlogin(){
        String email = edmail.getText().toString().trim();
        String name = edname.getText().toString().trim();
        String pass = edpass.getText().toString().trim();

        if (email.isEmpty()){
            edmail.setError("Error");
            edmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edmail.setError("Error");
            edmail.requestFocus();
            return;
        }

        if (name.isEmpty()){
            edname.setError("NameError");
            edname.requestFocus();
            return;
        }

        if (pass.isEmpty()){
            edpass.setError("PassError");
            edpass.requestFocus();
            return;
        }

        if (pass.length()<6){
            edpass.setError("PassLength");
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
    protected void onStart() {
        super.onStart();
        if (myAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,ProfileAct.class));
        }
    }

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

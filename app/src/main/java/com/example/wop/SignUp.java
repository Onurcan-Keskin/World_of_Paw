package com.example.wop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText edmail, edpass, edname;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edmail = (EditText) findViewById(R.id.edtemail);
        edname = (EditText) findViewById(R.id.edtname);
        edpass = (EditText) findViewById(R.id.edtpassword);

        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.email_sign_up_button).setOnClickListener(this);
    }

    private void registerUser() {
        String email = edmail.getText().toString().trim();
        String name = edname.getText().toString().trim();
        String pass = edpass.getText().toString().trim();

        if (email.isEmpty()) {
            edmail.setError("Error");
            edmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edmail.setError("Error");
            edmail.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            edname.setError("NameError");
            edname.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            edpass.setError("PassError");
            edpass.requestFocus();
            return;
        }

        if (pass.length() < 6) {
            edpass.setError("PassLength");
            edpass.requestFocus();
            return;
        }
            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        finish();
                        startActivity(new Intent(SignUp.this, ProfileAct.class));
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), R.string.error_already_registered, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.email_sign_up_button:
                registerUser();
                break;
        }
    }
}


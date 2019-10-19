package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    EditText edmail, edpass, edname;
    Button saveUser, verifyEmail;
    Intent intent;

    private StorageTask mUpload;
    private DatabaseReference mDatabaseRef;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    private String currentlang="en", getCurrentlang;
    float x1, x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edmail = findViewById(R.id.edtemail);
        edname = findViewById(R.id.edtname);
        edpass = findViewById(R.id.edtpassword);
        saveUser = findViewById(R.id.email_sign_up_button);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        currentlang=getIntent().getStringExtra(currentlang);

        findViewById(R.id.lng_tr).setOnClickListener(v -> setLocale("tr"));

        findViewById(R.id.lng_en).setOnClickListener(v -> setLocale("en"));

        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edmail.getText().toString().trim();
                String name = edname.getText().toString().trim();
                String pass = edpass.getText().toString().trim();

                if (mUpload != null && mUpload.isInProgress()){
                    Toast.makeText(SignUp.this,R.string.action_save_inprogress,Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name,email,pass);
                }
            }
        });
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
                    Intent intent = new Intent(SignUp.this, Loginn.class);
                    startActivity(intent);
                }
                if (x1 > x2) {
                    Intent intent = new Intent(SignUp.this, Loginn.class);
                    startActivity(intent);
                }
            }
            break;
        }
        return false;
    }

    public void setLocale(String localeName){
        if (!localeName.equals(currentlang)){
            Locale mylocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();
            config.locale = mylocale;
            res.updateConfiguration(config,dm);
            Intent refresh = new Intent(this, SignUp.class);
            refresh.putExtra(currentlang,localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(SignUp.this,R.string.error_lang_selected,Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(final String display_name, final String email, final String password) {

        String n_email = edmail.getText().toString().trim();
        String n_name = edname.getText().toString().trim();
        String n_pass = edpass.getText().toString().trim();

        if (n_email.isEmpty()) {
            edmail.setError(getString(R.string.error_field_required));
            edmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(n_email).matches()) {
            edmail.setError(getString(R.string.error_invalid_email));
            edmail.requestFocus();
            return;
        }

        if (n_name.isEmpty()) {
            edname.setError(getString(R.string.error_field_required));
            edname.requestFocus();
            return;
        }

        if (n_pass.isEmpty()) {
            edpass.setError(getString(R.string.error_field_required));
            edpass.requestFocus();
            return;
        }

        if (n_pass.length() < 6) {
            edpass.setError(getString(R.string.error_invalid_password));
            edpass.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = current_user.getUid();

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String tokenId = instanceIdResult.getToken();
                            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile").child(uid);
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name", display_name);
                            userMap.put("email",email);
                            userMap.put("password",password);
                            userMap.put("tokenID",tokenId);
                            userMap.put("status","Hi there. I am using World of Paw");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("location", "default");
                            userMap.put("lovely","0");

                            mProgress.setTitle(getString(R.string.action_sign_up_short));
                            mProgress.setMessage(getString(R.string.action_sign_up_dialog));
                            mProgress.setCanceledOnTouchOutside(false);
                            mProgress.show();
                            mDatabaseRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgress.dismiss();
                                        intent = new Intent(SignUp.this,Home.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mProgress.hide();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), R.string.error_already_registered, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return false;
    }
}

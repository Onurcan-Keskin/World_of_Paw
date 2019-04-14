package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wop.Model.Pet;
import com.example.wop.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SignUp extends AppCompatActivity {

    private static final int PICK_USER_IMAGE_REQUEST = 3;
    private static final int TAKE_USER_IMAGE_REQUEST = 4;

    ProgressBar progressBar;
    EditText edmail, edpass, edname;
    Button saveUser, verifyEmail;
    Intent intent;

    String profileImageURL;

    ImageView profile_pic;
    Uri mImageUri;

    private StorageReference mStorageRef;
    private StorageTask mUpload;
    private DatabaseReference mDatabaseRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edmail = findViewById(R.id.edtemail);
        edname = findViewById(R.id.edtname);
        edpass = findViewById(R.id.edtpassword);
        saveUser = findViewById(R.id.email_sign_up_button);
        verifyEmail = findViewById(R.id.btnVerify);
        profile_pic = findViewById(R.id.user_pic);
        progressBar = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("user_profile");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("user_profile");

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoseUserProfilePic();
            }
        });

        loadUserInformation();
        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpload != null && mUpload.isInProgress()){
                    Toast.makeText(SignUp.this,R.string.action_save_inprogress,Toast.LENGTH_SHORT).show();
                } else {
                    FieldCheck();
                    saveUserInfo();
                    registerUser();
                    uploadUser();
                    //startActivity(new Intent(SignUp.this,Loginn.class));
                }
            }
        });
    }

    private void FieldCheck(){
        String email = edmail.getText().toString().trim();
        String name = edname.getText().toString().trim();
        String pass = edpass.getText().toString().trim();

        if (email.isEmpty()) {
            edmail.setError(getString(R.string.error_field_required));
            edmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edmail.setError(getString(R.string.error_invalid_email));
            edmail.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            edname.setError(getString(R.string.error_field_required));
            edname.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            edpass.setError(getString(R.string.error_field_required));
            edpass.requestFocus();
            return;
        }

        if (pass.length() < 6) {
            edpass.setError(getString(R.string.error_invalid_password));
            edpass.requestFocus();
            return;
        }
    }

    private void registerUser() {
        String email = edmail.getText().toString().trim();
        String pass = edpass.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
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

    private void saveUserInfo(){
        String displayName = edname.getText().toString();

        if (displayName.isEmpty()) {
            edname.setError("Name required");
            edname.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageURL != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageURL))
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp.this, R.string.action_profile_update, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public  void checkEmail(){
        mAuth.fetchSignInMethodsForEmail(edmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();

                        if (!check){
                            Toast.makeText(SignUp.this,"Email not found",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUp.this,"Email already  present",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadUser(){
        progressBar.setVisibility(View.VISIBLE);
        if (mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            mUpload = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            },5000);
                            checkEmail();
                            User userUpload = new User(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(),
                                    edname.getText().toString().trim(),
                                    edpass.getText().toString().trim(),
                                    edmail.getText().toString().trim()); //Constructor Java Class
                            String uploadID = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadID).setValue(userUpload);
                            Toast.makeText(SignUp.this,R.string.action_save_success,Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUp.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }else {
            Toast.makeText(this,R.string.error_no_image,Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl().toString()).into(profile_pic);
            }

            if (user.getDisplayName() != null) {
                edname.setText(R.string.action_verify);
            } else {
                Toast.makeText(SignUp.this,R.string.error_verification,Toast.LENGTH_SHORT).show();
                verifyEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(SignUp.this, R.string.action_verify_sent, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }

    public void ChoseUserProfilePic() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.setTitle("Alert Dialog View");
        dialog.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        openFileChooser();
                    }
                });
        dialog.findViewById(R.id.btnTakePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        activeTakePhoto();
                    }
                });

        // show dialog on screen
        dialog.show();
    }

    /**
     * Take Photo
     * */
    private void activeTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, TAKE_USER_IMAGE_REQUEST);
        }
    }


    /**
     * Choose Picture
     * */
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_USER_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.i("Permissions",getString(R.string.permission_succsess));
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e("Error Permission: ","Some permission Error");
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PICK_USER_IMAGE_REQUEST:
                if (requestCode == PICK_USER_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
                    mImageUri = data.getData();
                    Picasso.with(this).load(mImageUri).into(profile_pic);
                }
                break;
            case TAKE_USER_IMAGE_REQUEST:
                if (requestCode == TAKE_USER_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mImageUri = data.getData();
                    Picasso.with(this).load(mImageUri).into(profile_pic);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            intent = new Intent(this, Loginn.class);
            startActivity(intent);
        }
        return false;
    }
}


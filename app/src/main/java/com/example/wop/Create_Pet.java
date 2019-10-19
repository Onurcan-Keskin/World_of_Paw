package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wop.Model.Pet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Create_Pet extends AppCompatActivity {

    private static final int PET_IMAGE_REQUEST = 1;

    DrawerLayout gDrawer;
    NavigationView gNavigation;
    ActionBarDrawerToggle gToggle;
    Intent intent;

    Uri mPetImageUri;

    Button ButtonSaveRecord;
    CircleImageView PetPic;
    EditText PetName;
    EditText PetBreed;
    EditText PetAge;
    EditText PetAddress;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private StorageReference mStorageRef;
    private StorageTask mUpload;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.dark_theme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Create a Pet");
        setContentView(R.layout.activity_create_pet);

        gDrawer = findViewById(R.id.drawer_pet_upload);
        gNavigation = findViewById(R.id.nav_view);
        gToggle = new ActionBarDrawerToggle(this, gDrawer, R.string.openDrawer, R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButtonSaveRecord = findViewById(R.id.pet_upload);
        PetPic = findViewById(R.id.pet_chose);
        PetName = findViewById(R.id.pet_name);
        PetBreed = findViewById(R.id.pet_breed);
        PetAge = findViewById(R.id.pet_age);
        PetAddress = findViewById(R.id.pet_address);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        String current_uid = mCurrentUser.getUid();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("user_profile").child(current_uid).child("pet_profile");

        ButtonSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pet_upload();

                intent = new Intent(Create_Pet.this, MyPet.class);
                startActivity(intent);
            }
        });

        PetPic.setOnClickListener(v -> openFileChooser());

        MyPetMenu();
    }

    private void openFileChooser() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PET_IMAGE_REQUEST);
    }

    private void MyPetMenu() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.home:
                        intent = new Intent(Create_Pet.this, Home.class);
                        startActivity(intent);
                        break;
                    case R.id.account:
                        intent = new Intent(Create_Pet.this, AccountSettings.class);
                        startActivity(intent);
                        break;
                    case R.id.mypet:
                        intent = new Intent(Create_Pet.this, MyPet.class);
                        startActivity(intent);
                        break;
                    case R.id.message:
                        intent = new Intent(Create_Pet.this, Messaging.class);
                        startActivity(intent);
                        break;
                    case R.id.lost:
                        intent = new Intent(Create_Pet.this, Lost.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent = new Intent(Create_Pet.this, Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        intent = new Intent(Create_Pet.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void FieldCheck() {
        String name = PetName.getText().toString().trim();
        String breed = PetBreed.getText().toString().trim();
        String age = PetAge.getText().toString().trim();
        String address = PetAddress.getText().toString().trim();

        if (name.isEmpty()) {
            PetName.setError(getString(R.string.error_field_required));
            PetName.requestFocus();
            return;
        }

        if (breed.isEmpty()) {
            PetBreed.setError(getString(R.string.error_field_required));
            PetBreed.requestFocus();
            return;
        }

        if (age.isEmpty()) {
            PetAge.setError(getString(R.string.error_field_required));
            PetAge.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            PetAddress.setError(getString(R.string.error_field_required));
            PetAddress.requestFocus();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PET_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mPetImageUri = data.getData();
            Picasso.get().load(mPetImageUri).placeholder(R.drawable.logo_wop).into(PetPic);
        }
    }

    private void pet_upload() {

        FieldCheck();

        if (mPetImageUri != null) {

            final StorageReference petFileRef = mStorageRef.child("pet_profile").child(System.currentTimeMillis() + ".jpg");
            petFileRef.putFile(mPetImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    mProgress = new ProgressDialog(Create_Pet.this);
                    mProgress.setTitle(getString(R.string.action_uploading_image));
                    mProgress.setMessage(getString(R.string.action_uploading_message));
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(0);
                        }
                    }, 5000);
                    mProgress.dismiss();

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri downloadURL = uriTask.getResult();

                    Pet pet = new Pet(downloadURL.toString(),
                            "default",
                            PetName.getText().toString().trim(),
                            PetBreed.getText().toString().trim(),
                            PetAge.getText().toString().trim(),
                            PetAddress.getText().toString().trim(),
                            "default",
                            "default");
                    String uploadID = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadID).setValue(pet);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Create_Pet.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgress.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendtoStart();
        }else {
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser!=null){
            mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendtoStart() {
        intent = new Intent(Create_Pet.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
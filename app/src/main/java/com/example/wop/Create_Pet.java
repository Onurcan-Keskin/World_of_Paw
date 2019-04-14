package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wop.Model.Pet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import java.util.ArrayList;
import java.util.List;

public class Create_Pet extends AppCompatActivity {

    private static final int PICK_PET_IMAGE_REQUEST = 1;
    private static final int TAKE_PET_IMAGE_REQUEST = 2;

    private ArrayList<MyImage> images;

    DrawerLayout gDrawer;
    NavigationView gNavigation;
    ActionBarDrawerToggle gToggle;
    Intent intent;

    Uri mImageUri;

    Button ButtonSaveRecord;
    ImageView PetPic;
    EditText PetName;
    EditText PetOwner;
    EditText PetBreed;
    EditText PetAge;
    EditText PetAddress;
    ProgressBar mProgressBar;

    private StorageReference mStorageRef;
    private StorageTask mUpload;
    private DatabaseReference mDatabaseRef;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet);

        gDrawer=findViewById(R.id.drawer_pet_upload);
        gNavigation=findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButtonSaveRecord = findViewById(R.id.pet_upload);
        PetPic = findViewById(R.id.pet_choose);
        PetName = findViewById(R.id.pet_name);
        PetOwner = findViewById(R.id.pet_owner);
        PetBreed = findViewById(R.id.pet_breed);
        PetAge = findViewById(R.id.pet_age);
        PetAddress = findViewById(R.id.pet_address);
        mProgressBar = findViewById(R.id.pet_progress);

        mStorageRef = FirebaseStorage.getInstance().getReference("pet_profile");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("pet_profile");

        images = new ArrayList();

        PetPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChosePetProfilePic();
            }
        });

        ButtonSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpload != null && mUpload.isInProgress()){
                    Toast.makeText(Create_Pet.this,R.string.action_save_inprogress,Toast.LENGTH_SHORT).show();
                } else {
                    FieldCheck();
                    uploadPet();
                    Toast.makeText(Create_Pet.this,R.string.action_save_success,Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Create_Pet.this,MyPet.class));
                }
            }
        });

        requestMultiplePermissions();
        MyPetMenu();
    }

    private void MyPetMenu() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id= menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        intent=new Intent(Create_Pet.this,Home.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent=new Intent(Create_Pet.this,Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        intent=new Intent(Create_Pet.this,Loginn.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void FieldCheck(){
        String name = PetName.getText().toString().trim();
        String owner = PetOwner.getText().toString().trim();
        String breed = PetBreed.getText().toString().trim();
        String address = PetAddress.getText().toString().trim();

        if(name.isEmpty()){
            PetName.setError(getString(R.string.error_field_required));
            PetName.requestFocus();
            return;
        }

        if (owner.isEmpty()){
            PetOwner.setError(getString(R.string.error_field_required));
            PetOwner.requestFocus();
            return;
        }

        if(breed.isEmpty()){
            PetBreed.setError(getString(R.string.error_field_required));
            PetBreed.requestFocus();
            return;
        }

        if (address.isEmpty()){
            PetAddress.setError(getString(R.string.error_field_required));
            PetAddress.requestFocus();
            return;
        }
    }

    public void ChosePetProfilePic() {
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
            startActivityForResult(intent, TAKE_PET_IMAGE_REQUEST);
        }
    }


    /**
     * Choose Picture
     * */
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_PET_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_PET_IMAGE_REQUEST:
                if (requestCode == PICK_PET_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
                    mImageUri = data.getData();
                    Picasso.with(this).load(mImageUri).into(PetPic);
                }
                break;
            case TAKE_PET_IMAGE_REQUEST:
                if (requestCode == TAKE_PET_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    mImageUri = data.getData();
                    Picasso.with(this).load(mImageUri).into(PetPic);
                }
                break;
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPet(){
        mProgressBar.setVisibility(View.VISIBLE);
        if (mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            mUpload = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            },5000);
                            Pet petupload = new Pet(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(),
                                    PetName.getText().toString().trim(),
                                    PetOwner.getText().toString().trim(),
                                    PetBreed.getText().toString().trim(),
                                    Integer.parseInt(PetAge.getText().toString().trim()),
                                    PetAddress.getText().toString()); //Constructor Java Class
                            String uploadID = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadID).setValue(petupload);
                            Toast.makeText(Create_Pet.this,R.string.action_save_success,Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Create_Pet.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int)progress);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }else {
            Toast.makeText(this,R.string.error_no_image,Toast.LENGTH_SHORT).show();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
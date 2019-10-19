package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wop.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AccountSettings extends AppCompatActivity {

    private static final int GALLERY_PIC = 1;

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;

    private LinearLayout imageLayout;

    private CircleImageView mImage;
    private TextView mName, mStatus;
    private ImageView hImageView;

    private EditText mNickname, mStat;
    private Button mAlertSaveBtn;

    private ProgressDialog mProgress;

    FirebaseAuth mAuth;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;

    private TextView u_mail, u_name;
    private CircleImageView u_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.dark_theme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.nav_account));
        setContentView(R.layout.activity_account_settings);

        gDrawer=(DrawerLayout) findViewById(R.id.drawer_account);
        gNavigation=(NavigationView) findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gDrawer.setDrawerTitle(Gravity.NO_GRAVITY,"Account Settings");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImage = findViewById(R.id.profile_image);
        mName = findViewById(R.id.user_name);
        mStatus = findViewById(R.id.user_status);

        CardView nameLayout = findViewById(R.id.name_layout);
        CardView statLayout = findViewById(R.id.stat_layout);
        imageLayout = findViewById(R.id.image_layout);

        /*Header*/
        hImageView=findViewById(R.id.header_user_pic);

        /*Firebase*/
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        String current_uid = mCurrentUser.getUid();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile").child(current_uid);
        mDatabaseRef.keepSynced(true);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String mail = dataSnapshot.child("email").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                /*Header*/

                mName.setText(name);
                mStatus.setText(status);

                if (!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.logo_wop)
                            .into(mImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.logo_wop).into(mImage);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AccountSettings.this,databaseError.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        /* Name */
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AccountSettings.this);
                dialog.setContentView(R.layout.change_name_box);
                Button save = dialog.findViewById(R.id.name_save);
                Button cancel = dialog.findViewById(R.id.name_cancel);
                dialog.setCanceledOnTouchOutside(false);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNickname = dialog.findViewById(R.id.name_box);
                        String name = mNickname.getText().toString();
                        mDatabaseRef.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.action_save_success, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(getApplicationContext(),R.string.error_saving,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        /* Status */
        statLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AccountSettings.this);
                dialog.setContentView(R.layout.change_stat_box);
                Button save = dialog.findViewById(R.id.stat_save);
                Button cancel = dialog.findViewById(R.id.stat_cancel);
                dialog.setCanceledOnTouchOutside(false);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mStat = dialog.findViewById(R.id.stat_box);
                        String status = mStat.getText().toString();
                        mDatabaseRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.action_save_success, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(getApplicationContext(),R.string.error_saving,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child(mCurrentUser.getEmail().replace(".",","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            User user = dataSnapshot.getValue(User.class);
                            Glide.with(AccountSettings.this).load(user.getImage()).into(u_image);
                            u_name.setText(user.getName());
                            u_mail.setText(user.getStatus());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AccountSettings.this,databaseError.getMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                });



        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSettings.this);
            }
        });

        MyHomeMenu();
        //MainZoomingFeature();
    }

    /*
    private void MainZoomingFeature() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.id.profile_image);
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        mImage.setImageDrawable(mDrawable);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AccountSettings.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_image,null);
                PhotoView photoView = mView.findViewById(R.id.imageview_profile);
                photoView.setImageResource(R.drawable.wop_logo);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }*/

    private void MyHomeMenu() {
        gNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                gDrawer.closeDrawers();
                int id= menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        intent=new Intent(AccountSettings.this,Home.class);
                        startActivity(intent);
                        break;
                    case R.id.mypet:
                        intent=new Intent(AccountSettings.this,MyPet.class);
                        startActivity(intent);
                        break;
                    case R.id.message:
                        intent=new Intent(AccountSettings.this,Messaging.class);
                        startActivity(intent);
                        break;
                    case R.id.lost:
                        intent=new Intent(AccountSettings.this,Lost.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent=new Intent(AccountSettings.this,Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        mAuth.signOut();
                        intent=new Intent(AccountSettings.this,Loginn.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
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
        intent = new Intent(AccountSettings.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PIC && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(16,9).start(AccountSettings.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(AccountSettings.this);
                mProgress.setTitle(getString(R.string.action_uploading_image));
                mProgress.setMessage(getString(R.string.action_uploading_message));
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                final String current_uid = mCurrentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = mStorageRef.child("user_profile").child(current_uid+".jpg");
                final StorageReference thumb_filepath =  mStorageRef.child("user_profile").child("thumbs").child(current_uid+".jpg");

                if (resultUri != null){
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                mStorageRef.child("user_profile").child(current_uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadURL = uri.toString();
                                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                                if (thumb_task.isSuccessful()){
                                                    mStorageRef.child("user_profile").child(current_uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            final String thumb_downloadURL = uri.toString();
                                                            mDatabaseRef.child("thumb_image").setValue(thumb_downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Map<String, Object> update_Map = new HashMap<>();
                                                                        update_Map.put("image",downloadURL);
                                                                        update_Map.put("thumb_image",thumb_downloadURL);
                                                                        mDatabaseRef.updateChildren(update_Map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    mProgress.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }else {
                                                    Toast.makeText(AccountSettings.this,"Error Uploading",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }else {
                                Toast.makeText(AccountSettings.this,"Error - Hata",Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }
                        }
                    });
                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception exception = result.getError();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

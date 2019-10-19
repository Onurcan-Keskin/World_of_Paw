package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wop.Model.Pet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

public class Single_Pet extends AppCompatActivity {

    private static final int GALLERY_PIC = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    private DatabaseReference mDatabaseRef;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;
    private StorageReference petFileRef;

    private ProgressDialog mProgress;
    private Intent intent;

    private DatabaseReference mRootRef;

    private RelativeLayout rl;

    private TextView mPetName, mPetBreed, mPetAge, mPetAddress, mBarPetName;
    private EditText ePetName, ePetBreed, ePetAge, ePetAddress;
    private CircleImageView mPetImage, mBarPetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.dark_theme);
        } else setTheme(R.style.AppTheme);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_single_pet);

        mPetName = findViewById(R.id.pet_name_text);
        mPetBreed = findViewById(R.id.pet_breed_text);
        mPetAge = findViewById(R.id.pet_age_text);
        mPetAddress = findViewById(R.id.pet_address_text);
        mPetImage = findViewById(R.id.pet_circle_image);

        rl = findViewById(R.id.lost_btn);

        mBarPetImage = findViewById(R.id.prof_pet_image);
        mBarPetName = findViewById(R.id.prof_pet_name);

        final Dialog dialog = new Dialog(Single_Pet.this);

        /* FireBase Pet */
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        petFileRef = mStorageRef.child("pet_profile").child(System.currentTimeMillis() + ".jpg");

        String current_uid = mCurrentUser.getUid();
        final String pet_id = getIntent().getStringExtra("pet_id");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("user_profile").child(current_uid).child("pet_profile").child(pet_id);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Lost").child(pet_id);
        mDatabaseRef.keepSynced(true);

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("pet_name").getValue().toString();
                String breed = dataSnapshot.child("pet_breed").getValue().toString();
                String age = dataSnapshot.child("pet_age").getValue().toString();
                String address = dataSnapshot.child("pet_address").getValue().toString();
                final String image = dataSnapshot.child("pet_image").getValue().toString();
                String nfc = dataSnapshot.child("pet_nfc_location").getValue().toString();

                mPetName.setText(name);
                mPetBreed.setText(breed);
                mPetAge.setText(age);
                mPetAddress.setText(address);

                mBarPetName.setText(name);

                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"YES", "NO"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(Single_Pet.this);
                        builder.setTitle("Is your pet LOST?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Click event for each item
                                if (which == 0){

                                    mRootRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            Map lostMap = new HashMap();
                                            lostMap.put("lost_pet_owner",current_uid);
                                            lostMap.put("lost_pet_name",name);
                                            lostMap.put("lost_pet_breed",breed);
                                            lostMap.put("lost_pet_age",age);
                                            lostMap.put("lost_pet_address",address);
                                            lostMap.put("lost_pet_image",image);
                                            lostMap.put("lost_pet_nfc_location",nfc);

                                            mRootRef.updateChildren(lostMap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if (databaseError != null){
                                                        Log.d("Chat_LOG", databaseError.getMessage().toString());
                                                    }else {
                                                        Intent profintent = new Intent(Single_Pet.this,Lost.class);
                                                        profintent.putExtra("pet_lost_id",pet_id);
                                                        startActivity(profintent);
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                }else if (which == 1){
                                    Intent chatintent = new Intent(Single_Pet.this,MyPet.class);
                                    chatintent.putExtra("pet_lost_id",pet_id);
                                    startActivity(chatintent);
                                }
                            }
                        });
                        builder.show();
                    }
                });

                /* Main */
                if (!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .into(mPetImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(mPetImage);
                                }
                            });
                }

                /* Bar */
                if (!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.logo_wop)
                            .into(mBarPetImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.logo_wop).into(mBarPetImage);
                                }
                            });
                }

                findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Single_Pet.this,MyPet.class);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* Image */
        findViewById(R.id.pet_image_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Single_Pet.this);
            }
        });

        /* Name */
        findViewById(R.id.pet_name_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Single_Pet.this);
                dialog.setContentView(R.layout.change_pet_name_box);
                Button save = dialog.findViewById(R.id.pet_name_save);
                Button cancel = dialog.findViewById(R.id.pet_name_cancel);
                dialog.setCanceledOnTouchOutside(false);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ePetName = dialog.findViewById(R.id.pet_name_box);
                        String name = ePetName.getText().toString();
                        mDatabaseRef.child("pet_name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.action_save_success, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.error_saving, Toast.LENGTH_SHORT).show();
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

        /* Breed */
        findViewById(R.id.pet_breed_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Single_Pet.this);
                dialog.setContentView(R.layout.change_pet_breed_box);
                Button save = dialog.findViewById(R.id.pet_breed_save);
                Button cancel = dialog.findViewById(R.id.pet_breed_cancel);
                dialog.setCanceledOnTouchOutside(false);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ePetBreed = dialog.findViewById(R.id.pet_breed_box);
                        String status = ePetBreed.getText().toString();
                        mDatabaseRef.child("pet_breed").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.action_save_success, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.error_saving, Toast.LENGTH_SHORT).show();
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

        /* Age */
        findViewById(R.id.pet_age_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Single_Pet.this);
                dialog.setContentView(R.layout.change_pet_age_box);
                Button save = dialog.findViewById(R.id.pet_age_save);
                Button cancel = dialog.findViewById(R.id.pet_age_cancel);
                dialog.setCanceledOnTouchOutside(false);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ePetAge = dialog.findViewById(R.id.pet_age_box);
                        String status = ePetAge.getText().toString();
                        mDatabaseRef.child("pet_age").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.action_save_success, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.error_saving, Toast.LENGTH_SHORT).show();
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

        /* Address */
        findViewById(R.id.pet_address_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Single_Pet.this);
                dialog.setContentView(R.layout.change_pet_address_box);
                Button save = dialog.findViewById(R.id.pet_address_save);
                Button cancel = dialog.findViewById(R.id.pet_address_cancel);
                dialog.setCanceledOnTouchOutside(false);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ePetAddress = dialog.findViewById(R.id.pet_address_box);
                        String status = ePetAddress.getText().toString();
                        mDatabaseRef.child("pet_address").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.action_save_success, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.error_saving, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendtoStart();
        } else {
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
    }

    private void sendtoStart() {
        intent = new Intent(Single_Pet.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PIC && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(16,9).start(Single_Pet.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(Single_Pet.this);
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

                final StorageReference filepath = mStorageRef.child("pet_profile").child(current_uid+".jpg");
                final StorageReference thumb_filepath =  mStorageRef.child("pet_profile").child("thumbs").child(current_uid+".jpg");

                if (resultUri != null){
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                mStorageRef.child("pet_profile").child(current_uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadURL = uri.toString();
                                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                                if (thumb_task.isSuccessful()){
                                                    mStorageRef.child("pet_profile").child(current_uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                                                    Toast.makeText(Single_Pet.this,"Error Uploading",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }else {
                                Toast.makeText(Single_Pet.this,"Error - Hata",Toast.LENGTH_SHORT).show();
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
}

package com.example.wop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wop.NFC_Model.AsyncOperationCallback;
import com.example.wop.NFC_Model.AsyncUiCallback;
import com.example.wop.NFC_Model.InsufficientCapacityException;
import com.example.wop.NFC_Model.NfcActivity;
import com.example.wop.NFC_Model.NfcReadUtility;
import com.example.wop.NFC_Model.NfcReadUtilityImpl;
import com.example.wop.NFC_Model.NfcWriteUtility;
import com.example.wop.NFC_Model.ReadOnlyTagException;
import com.example.wop.NFC_Model.TagNotPresentException;
import com.example.wop.NFC_Model.WriteCallbackNfcAsync;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NFC extends NfcActivity implements LocationListener {

    Context context;
    public static String tvLongi;
    public static String tvLati;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;
    private DatabaseReference mUserRef;

    private TextView tvNFCContent;
    private TextView Mname, Mmail, Mstatus;
    private Button btnWrite;
    private Button btnGet;
    private String geoURI;
    private static final String TAG = NFC.class.getName();
    private EditText mLongitude, mLatitude;
    private Intent intent;
    private LocationManager locationManager;

    NfcReadUtility mNfcReadUtility = new NfcReadUtilityImpl();
    ProgressDialog mProgressDialog;

    AsyncUiCallback mAsyncUiCallback = new AsyncUiCallback() {
        @Override
        public void callbackWithReturnValue(Boolean result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (result) {
                Toast.makeText(NFC.this, R.string.message_nfc_write_success, Toast.LENGTH_LONG).show();
            }

            Log.d(TAG,"Received our result : " + result);

        }

        @Override
        public void onProgressUpdate(Boolean... values) {
            if (values.length > 0 && values[0] && mProgressDialog != null) {
                mProgressDialog.setMessage("Writing");
                Log.d(TAG,"Writing !");
            }
        }

        @Override
        public void onError(Exception e) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            Log.i(TAG,"Encountered an error !",e);
            Toast.makeText(NFC.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    AsyncOperationCallback mAsyncOperationCallback;
    private AsyncTask<Object, Void, Boolean> mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.dark_theme);
        }else setTheme(R.style.AppTheme);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_nfc);

        context = this;

        tvNFCContent = findViewById(R.id.nfc_contents);
        Mname = findViewById(R.id.edit_name);
        Mmail = findViewById(R.id.edit_mail);
        Mstatus = findViewById(R.id.edit_status);
        btnWrite = findViewById(R.id.button);
        btnGet = findViewById(R.id.loc_marker);
        mLatitude = findViewById(R.id.latitude);
        mLongitude = findViewById(R.id.longitude);

        /* Database Elements User */
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        String current_uid = mCurrentUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile").child(current_uid);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(NFC.this, MyPet.class);
                startActivity(intent);
            }
        });


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String mail = dataSnapshot.child("email").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                Mname.setText(name);
                Mmail.setText(mail);
                Mstatus.setText(status);

                btnGet.setOnClickListener(v -> getLocation());

                tvNFCContent.setOnClickListener(v -> {
                    EditText pin = findViewById(R.id.pin_name);
                    final String loc_name = pin.getText().toString().trim();
                    if (mLatitude != null && mLongitude != null){
                        final double longitude = Double.parseDouble(mLongitude.getText().toString());
                        final double latitude = Double.parseDouble(mLatitude.getText().toString());
                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude,loc_name);
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
                        startActivity(intent);
                    }else {
                        FieldCheck();
                    }
                });

                if (btnWrite != null) {
                    btnWrite.setOnClickListener(v -> {
                        EditText latitudeText = (EditText) retrieveElement(R.id.latitude);
                        EditText longitudeText = (EditText) retrieveElement(R.id.longitude);
                        if (latitudeText != null && longitudeText != null) {

                            final double longitude = Double.parseDouble(latitudeText.getText().toString());
                            final double latitude = Double.parseDouble(longitudeText.getText().toString());

                            mDatabaseRef.child("location").setValue(longitude+","+latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Map<String,Object> locationMap = new HashMap<>();
                                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", longitude, latitude);
                                        locationMap.put("location",uri);
                                        mDatabaseRef.updateChildren(locationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(NFC.this,R.string.action_save,Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });

                            mAsyncOperationCallback = new AsyncOperationCallback() {
                                @Override
                                public boolean performWrite(NfcWriteUtility writeUtility) throws ReadOnlyTagException, InsufficientCapacityException, TagNotPresentException, FormatException {
                                    return writeUtility.writeGeolocationToTagFromIntent(longitude, latitude, getIntent());
                                }
                            };
                            showDialog();
                        } else {
                            FieldCheck();
                            Toast.makeText(NFC.this, getString(R.string.error_nfc_proximity),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                enableBeam();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NFC.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        CheckPermission();
    }

    private void FieldCheck(){
        String n_latitude = mLatitude.getText().toString().trim();
        String n_longitude = mLongitude.getText().toString().trim();

        if (n_latitude.isEmpty()){
            mLatitude.setError(getString(R.string.error_field_required));
            mLatitude.requestFocus();
            return;
        }
        if (n_longitude.isEmpty()){
            mLongitude.setError(getString(R.string.error_field_required));
            mLongitude.requestFocus();
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
        if (getNfcAdapter() != null) {
            getNfcAdapter().disableForegroundDispatch(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            intent = new Intent(this, MyPet.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onNewIntent(final Intent paramIntent) {
        super.onNewIntent(paramIntent);

        if (mAsyncOperationCallback != null && mProgressDialog != null && mProgressDialog.isShowing()) {
            new WriteCallbackNfcAsync(mAsyncUiCallback, mAsyncOperationCallback).executeWriteOperation();
            mAsyncOperationCallback = null;
        } else {
            for (final String data : mNfcReadUtility.readFromTagWithMap(paramIntent).values()) {
                tvNFCContent.setText(data);

                Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showNoInputToast() {
        Toast.makeText(this, getString(R.string.no_input), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public void showDialog() {
        mProgressDialog = new ProgressDialog(NFC.this);
        mProgressDialog.setTitle(R.string.progressdialog_waiting_for_tag);
        mProgressDialog.setMessage(getString(R.string.progressdialog_waiting_for_tag_message));
        mProgressDialog.show();
    }

    private TextView retrieveElement(int id) {
        TextView element = (TextView) findViewById(id);
        return (element != null) && ((TextView) findViewById(id)).getText() != null && !"".equals(((TextView) findViewById(id)).getText().toString()) ? element : null;
    }

    public void getLocation(){
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        tvLongi = String.valueOf(location.getLongitude());
        tvLati = String.valueOf(location.getLatitude());

        mLongitude.setText(tvLongi);
        mLatitude.setText(tvLati);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this,"Location status changed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, getString(R.string.message_provider_enabled) + provider,Toast.LENGTH_SHORT).show();
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
        intent = new Intent(NFC.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(NFC.this,getString(R.string.message_enable_nfc),Toast.LENGTH_SHORT).show();
    }
}
package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wop.Model.Pet;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MyPet extends AppCompatActivity {

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mPetDatabaseRef;
    private DatabaseReference mLostDatabaseRef;

    private RecyclerView mPetList;
    private ProgressDialog mprogress;

    SwipePetCardView swipePetCardView = null;
    private ItemTouchHelper itemTouchHelper;
    private DatabaseReference mRootRef;
    private FirebaseUser current_UID;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.dark_theme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getText(R.string.nav_mydog));
        setContentView(R.layout.activity_my_pet);

        gDrawer=(DrawerLayout) findViewById(R.id.drawer_my_pet);
        gNavigation=(NavigationView) findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        current_UID = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        uid = current_UID.getUid();
        mPetDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("user_profile").child(uid).child("pet_profile");
        mPetDatabaseRef.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());

        findViewById(R.id.add_pet_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MyPet.this, Create_Pet.class);
                startActivity(intent);
            }
        });

        mPetList = findViewById(R.id.pet_list);
        mPetList.setHasFixedSize(true);
        mPetList.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView();
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
                        intent=new Intent(MyPet.this,Home.class);
                        startActivity(intent);
                        break;
                    case R.id.account:
                        intent=new Intent(MyPet.this,AccountSettings.class);
                        startActivity(intent);
                        break;
                    case R.id.message:
                        intent=new Intent(MyPet.this,Messaging.class);
                        startActivity(intent);
                        break;
                    case R.id.lost:
                        intent=new Intent(MyPet.this,Lost.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent=new Intent(MyPet.this,Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        mAuth.signOut();
                        intent=new Intent(MyPet.this,MainActivity.class);
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

        Query query = FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions<Pet> option = new FirebaseListOptions.Builder<Pet>()
                .setLayout(R.layout.pet_single_layout)
                .setQuery(query, Pet.class)
                .build();

        FirebaseRecyclerOptions<Pet> options = new FirebaseRecyclerOptions.Builder<Pet>()
                .setQuery(mPetDatabaseRef,Pet.class)
                .build();

        FirebaseRecyclerAdapter<Pet,PetViewHolder> adapter = new FirebaseRecyclerAdapter<Pet, PetViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull PetViewHolder petViewHolder, int i, @NonNull Pet pet) {
                petViewHolder.setPetName(pet.getPet_name());
                petViewHolder.setPetBreed(pet.getPet_breed());
                petViewHolder.setPetAge(pet.getPet_age());
                petViewHolder.setPetAddress(pet.getPet_address());
                petViewHolder.setPetImage(pet.getPet_image());

                final String list_pet = getRef(i).getKey();

                petViewHolder.mPetView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getApplicationContext(), Single_Pet.class);
                        intent.putExtra("pet_id",list_pet);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_single_layout,parent,false);
                return new PetViewHolder(view);
            }
        };
        adapter.startListening();
        mPetList.setAdapter(adapter);
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder{

        View mPetView;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            mPetView = itemView;
        }

        public  void setPetName(String name){
            TextView mPetName = mPetView.findViewById(R.id.pet_single_name);
            mPetName.setText(name);
        }

        public void setPetBreed(String breed){
            TextView mPetBreed = mPetView.findViewById(R.id.pet_single_breed);
            mPetBreed.setText(breed);
        }

        public void setPetAge(String age){
            TextView mPetAge = mPetView.findViewById(R.id.pet_single_age);
            mPetAge.setText(age);
        }

        public void setPetAddress(String address){
            TextView mPetAddress = mPetView.findViewById(R.id.pet_single_address);
            mPetAddress.setText(address);
        }

        public void setPetImage(final String image){
            final CircleImageView mPetImage = mPetView.findViewById(R.id.pet_single_image);
            //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mPetImage);
            if (!image.equals("default")) {
                //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.logo_wop)
                        .into(mPetImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image).placeholder(R.drawable.logo_wop).into(mPetImage);
                            }
                        });
            }
        }
    }

    public void setupRecyclerView(){

        mPetList.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));

        swipePetCardView = new SwipePetCardView(new SwipeContollerActions() {
            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);

                intent = new Intent(MyPet.this, NFC.class);
                startActivity(intent);
            }

            @Override
            public void onRightClicked(int position) {
                super.onRightClicked(position);
                intent = new Intent(MyPet.this, DevicesBLE.class);
                startActivity(intent);
            }
        });

        itemTouchHelper = new ItemTouchHelper(swipePetCardView);
        itemTouchHelper.attachToRecyclerView(mPetList);

        mPetList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                swipePetCardView.onDraw(c);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser!=null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendtoStart() {
        intent = new Intent(MyPet.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            intent = new Intent(this, Home.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
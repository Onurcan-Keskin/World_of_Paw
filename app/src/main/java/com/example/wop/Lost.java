package com.example.wop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wop.Model.Pet;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Lost extends AppCompatActivity {

    private DrawerLayout gDrawer;
    private NavigationView gNavigation;
    private ActionBarDrawerToggle gToggle;
    private Intent intent;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mPetDatabaseRef;
    private DatabaseReference mLostRef;

    private RecyclerView mLostList;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.dark_theme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getText(R.string.nav_lost));
        setContentView(R.layout.activity_lost);

        gDrawer=(DrawerLayout) findViewById(R.id.drawer_lost);
        gNavigation=(NavigationView) findViewById(R.id.nav_view);
        gToggle=new ActionBarDrawerToggle(this,gDrawer,R.string.openDrawer,R.string.closeDrawer);
        gDrawer.addDrawerListener(gToggle);
        gToggle.syncState();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        mPetDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("user_profile").child(uid).child("pet_profile");
        mLostRef = FirebaseDatabase.getInstance().getReference().child("Lost<");
        mPetDatabaseRef.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());

        mLostList = findViewById(R.id.lost_list);
        mLostList.setHasFixedSize(true);
        mLostList.setLayoutManager(new LinearLayoutManager(this));

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
                        intent=new Intent(Lost.this,Home.class);
                        startActivity(intent);
                        break;
                    case R.id.mypet:
                        intent=new Intent(Lost.this,MyPet.class);
                        startActivity(intent);
                        break;
                    case R.id.account:
                        intent=new Intent(Lost.this,AccountSettings.class);
                        startActivity(intent);
                        break;
                    case R.id.message:
                        intent=new Intent(Lost.this,Messaging.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent=new Intent(Lost.this,Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.wophome:
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://worldofpaw.com"));
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        mAuth.signOut();
                        intent=new Intent(Lost.this,MainActivity.class);
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

        FirebaseRecyclerOptions<Pet> options = new FirebaseRecyclerOptions.Builder<Pet>()
                .setQuery(mPetDatabaseRef,Pet.class)
                .build();

        FirebaseRecyclerAdapter<Pet,mLostPetViewHolder> adapter = new FirebaseRecyclerAdapter<Pet, mLostPetViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull mLostPetViewHolder lostPetViewHolder, int i, @NonNull Pet pet) {
                lostPetViewHolder.setPetName(pet.getPet_name());
                lostPetViewHolder.setPetBreed(pet.getPet_breed());
                lostPetViewHolder.setPetAge(pet.getPet_age());
                lostPetViewHolder.setPetAddress(pet.getPet_nfc_location());
                lostPetViewHolder.setPetImage(pet.getPet_image());

                final String list_pet = getRef(i).getKey();

                lostPetViewHolder.mPetView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(getApplicationContext(), Single_Lost_Pet.class);
                        intent.putExtra("lost_pet_id",list_pet);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public mLostPetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_single_lost_layout,parent,false);
                return new mLostPetViewHolder(view);
            }
        };
        adapter.startListening();
        mLostList.setAdapter(adapter);
    }

    public static class mLostPetViewHolder extends RecyclerView.ViewHolder{

        View mPetView;

        public mLostPetViewHolder(@NonNull View itemView) {
            super(itemView);
            mPetView = itemView;
        }

        public  void setPetName(String name){
            TextView mPetName = mPetView.findViewById(R.id.lost_pet_single_name);
            mPetName.setText(name);
        }

        public void setPetBreed(String breed){
            TextView mPetBreed = mPetView.findViewById(R.id.lost_pet_single_breed);
            mPetBreed.setText(breed);
        }

        public void setPetAge(String age){
            TextView mPetAge = mPetView.findViewById(R.id.lost_pet_single_age);
            mPetAge.setText(age);
        }

        public void setPetAddress(String address){
            TextView mPetAddress = mPetView.findViewById(R.id.lost_pet_single_address);
            mPetAddress.setText(address);
        }

        public void setPetImage(final String image){
            final CircleImageView mPetImage = mPetView.findViewById(R.id.lost_pet_single_image);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(current_user != null){
            mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendtoStart() {
        intent = new Intent(Lost.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.wop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.example.wop.Model.Friends;
import com.example.wop.Model.Pet;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileAct extends AppCompatActivity {

    private ImageView mProfImage;
    private CollapsingToolbarLayout mCollapse;
    private TextView mFriendsCount, mLoveliesCount, mProfileStat, mStared, mUserName;
    private Switch mMuteSwitch;
    private Button mSendRequestBtn, mDeclineRequestBtn, mLovelyBtn, mBlocktBtn;
    private CardView mDeclineCard;
    private CircleImageView mProfUserImage;
    private TextView mProfUserName, mProfLastSeen;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mFriendReqDatabaseRef;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationRef;
    private DatabaseReference mRootRef;

    private FirebaseUser mCurrentUser;

    private DatabaseReference mPetDatabaseRef;

    private RecyclerView mPetList;

    private Intent intent;

    private String mCurrent_state;

    private Friends friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.dark_theme);
        }else setTheme(R.style.AppTheme);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile").child(user_id);
        mFriendReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mPetDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("user_profile").child(user_id).child("pet_profile");
        mPetDatabaseRef.keepSynced(true);

        mProfImage = findViewById(R.id.prof_image);

        mFriendsCount = findViewById(R.id.prof_friend_count);
        mLoveliesCount = findViewById(R.id.prof_likes_count);
        mProfileStat = findViewById(R.id.prof_status);
        mStared = findViewById(R.id.prof_stared);
        mUserName = findViewById(R.id.prof_name);

        /* Custom Bar */
        mProfUserImage = findViewById(R.id.prof_user_image);
        mProfUserName = findViewById(R.id.prof_user_name);
        mProfLastSeen = findViewById(R.id.prof_last_seen);

        mMuteSwitch = findViewById(R.id.prof_mute_switch);

        mSendRequestBtn = findViewById(R.id.prof_send_request);
        mDeclineRequestBtn = findViewById(R.id.prof_decline_request);
        mDeclineCard = findViewById(R.id.prof_decline_card);
        mLovelyBtn = findViewById(R.id.prof_lovely_btn);
        mBlocktBtn = findViewById(R.id.prof_block_btn);

        mPetList = findViewById(R.id.prof_pet_list);

        mCurrent_state = "not_friends";

        mDeclineCard.setVisibility(View.INVISIBLE);
        mDeclineRequestBtn.setVisibility(View.INVISIBLE);
        mDeclineCard.setEnabled(false);

        mPetList = findViewById(R.id.prof_pet_list);
        mPetList.setHasFixedSize(true);
        mPetList.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ProfileAct.this, Messaging.class);
                startActivity(intent);
            }
        });

        /*mFriendDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user_id).getValue().toString() != null){
                    String time = dataSnapshot.child(user_id).getValue().toString().trim();
                    mTillDate.setText(R.string.message_friends_with_you_since + " " + time);
                }else {
                    mTillDate.setText(R.string.message_not_friends);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mTillDate.setText(R.string.message_not_friends);
            }
        });*/

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                final String love = dataSnapshot.child("lovely").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();

                if (online.equals("true")){
                    mProfLastSeen.setText("Online");
                }else {

                    GetTimeAgo getTimeAgon = new GetTimeAgo();
                    long lasttime = Long.parseLong(online);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lasttime, getApplicationContext());

                    mProfLastSeen.setText(lastSeenTime);
                }

                mUserName.setText(display_name);
                mProfUserName.setText(display_name);
                mProfileStat.setText(status);
                mLoveliesCount.setText(love);

                mLovelyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //love++;
                        int lovecnt_int = Integer.parseInt(love);
                        lovecnt_int++;
                        String lovecnt_str = String.valueOf(lovecnt_int);
                        mLoveliesCount.setText(""+lovecnt_str);
                        Map<String, Object> loven = new HashMap<>();
                        loven.put("lovely",lovecnt_str);
                        mDatabaseRef.updateChildren(loven);
                    }
                });

                if (!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.logo_wop).into(mProfImage);
                    Picasso.get().load(image).placeholder(R.drawable.logo_wop).into(mProfUserImage);
                }

                /**
                 * FRIENDS LIST / REQUESTS
                 */
                mFriendReqDatabaseRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (request_type.equals("received")){
                                mCurrent_state = "req_received";
                                mSendRequestBtn.setText(getString(R.string.action_accept_request));

                                mDeclineCard.setVisibility(View.VISIBLE);
                                mDeclineRequestBtn.setVisibility(View.VISIBLE);
                                mDeclineRequestBtn.setEnabled(true);

                            }else if (request_type.equals("sent")){
                                mCurrent_state = "req_sent";
                                mSendRequestBtn.setText(getText(R.string.action_cancel_request));

                                mDeclineCard.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setEnabled(false);
                            }
                        }else {
                            mFriendReqDatabaseRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        mCurrent_state="friends";
                                        mSendRequestBtn.setText(getString(R.string.action_unfriend));


                                        mDeclineCard.setVisibility(View.INVISIBLE);
                                        mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                        mDeclineRequestBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSendRequestBtn.setEnabled(false);

                /**
                 *  NOT FRIENDS STATE - NOTIFICATIONS
                 */
                if (mCurrent_state.equals("not_friends")){

                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationID = newNotificationRef.getKey();

                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Requests/"+mCurrentUser.getUid()+"/"+user_id+"/request_type","sent");
                    requestMap.put("Friend_Requests/"+user_id+"/"+mCurrentUser.getUid()+"/request_type", "received");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationID,notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null){
                                Toast.makeText(ProfileAct.this,"There is some error sending request",Toast.LENGTH_SHORT).show();
                            }
                            mSendRequestBtn.setEnabled(true);
                            mCurrent_state="req_sent";
                            mSendRequestBtn.setText(getString(R.string.action_cancel_request));
                        }
                    });
                }
                /**
                 *  CANCEL REQUEST STATE
                 */
                if (mCurrent_state.equals("req_sent")){
                    mFriendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mSendRequestBtn.setEnabled(true);
                                    mCurrent_state="not_friends";
                                    mSendRequestBtn.setText(getString(R.string.action_send_request));

                                    mDeclineCard.setVisibility(View.INVISIBLE);
                                    mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                    mDeclineRequestBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }
                /**
                 * REQUEST RECEIVED
                 */
                if (mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id+"/date",currentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid()+"/date",currentDate);

                    friendsMap.put("Friend_Requests/"+mCurrentUser.getUid()+"/"+user_id,null);
                    friendsMap.put("Friend_Requests/"+user_id+"/"+mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null){
                                mSendRequestBtn.setEnabled(true);
                                mCurrent_state="friends";
                                mSendRequestBtn.setText(getString(R.string.action_unfriend));
                                mDeclineCard.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setEnabled(false);
                            }else {
                                Toast.makeText(ProfileAct.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                /**
                 * UNFRIEND
                 */
                if (mCurrent_state.equals("friends")){
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id,null);
                    unfriendMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null){

                                mCurrent_state="not_friends";
                                mSendRequestBtn.setText(getString(R.string.action_send_request));
                                mDeclineCard.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setEnabled(false);
                            }else {
                                Toast.makeText(ProfileAct.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

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
        intent = new Intent(ProfileAct.this, MainActivity.class);
        startActivity(intent);
        fileList();
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
}
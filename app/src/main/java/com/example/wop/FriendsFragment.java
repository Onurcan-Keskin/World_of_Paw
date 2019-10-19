package com.example.wop;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wop.Model.Friends;
import com.example.wop.Model.User;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabaseRef;
    private DatabaseReference mUsersDatabaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    private String mCurrent_uid;
    private  View mMainView;

    private Intent intent;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_friends,container,false);

        mFriendsList = mMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());
        mCurrent_uid = mAuth.getCurrentUser().getUid();

        mFriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_uid);
        mFriendsDatabaseRef.keepSynced(true);
        mUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile");
        mUsersDatabaseRef.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){

        }else {
            mUserRef.child("online").setValue(true);
        }

        Query query = FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions<Friends> option = new FirebaseListOptions.Builder<Friends>()
                .setLayout(R.layout.user_single_layout)
                .setQuery(query,Friends.class)
                .build();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendsDatabaseRef,Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int i, @NonNull Friends friends) {

                friendsViewHolder.setDate(getString(R.string.message_friends_with_you_since)+": "+friends.getDate());

                final String list_uid = getRef(i).getKey();

                mUsersDatabaseRef.child(list_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String UserName = dataSnapshot.child("name").getValue().toString();
                        String UserThum = dataSnapshot.child("thumb_image").getValue().toString();
                        //String userOnline = dataSnapshot.child("online").getValue().toString();

                        if (dataSnapshot.hasChild("online")){
                            String userOnline =  dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);
                        }
                        friendsViewHolder.setUserName(UserName);
                        friendsViewHolder.setThumb(UserThum);

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Click event for each item
                                        if (which == 0){
                                            Intent profintent = new Intent(getContext(),ProfileAct.class);
                                            profintent.putExtra("user_id",list_uid);
                                            startActivity(profintent);
                                        }else if (which == 1){
                                            Intent chatintent = new Intent(getContext(),ChatActivity.class);
                                            chatintent.putExtra("user_id",list_uid);
                                            startActivity(chatintent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_single_layout,parent,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        mFriendsList.setAdapter(adapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDate(String date){
            TextView mDate = mView.findViewById(R.id.user_single_status);
            mDate.setText(date);
        }

        public void setUserName(String name){
            TextView mUserName = mView.findViewById(R.id.user_single_name);
            mUserName.setText(name);
        }

        public void setUserOnline(String online){
            ImageView userOnlineView = mView.findViewById(R.id.online_icon);
            if (online.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

        public void setThumb(final String thumb){
            final CircleImageView mUserImage = mView.findViewById(R.id.user_single_image);
            //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mUserImage);
            if (!thumb.equals("default")) {
                //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                Picasso.get().load(thumb).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(mUserImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(thumb).placeholder(R.drawable.ic_account_circle_black_24dp).into(mUserImage);
                            }
                        });
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser!=null){
            mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
        }
    }
}

package com.example.wop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wop.Model.User;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AllUserFragment extends Fragment {

    private Intent intent;

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    private String mCurr;

    private CircleImageView mUserImage;
    private LinearLayout mUserSingleLayout;
    private ImageView mIm;
    private RecyclerView mUserList;
    private  View mMainView;
    private FragmentActivity context;

    public AllUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user_profile")
                .child(mAuth.getCurrentUser().getUid());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile");
        mDatabaseRef.keepSynced(true);

        context = getActivity();

        mCurr = FirebaseAuth.getInstance().getUid();

        mMainView = inflater.inflate(R.layout.fragment_all_user,container,false);

        mUserList = mMainView.findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(getContext()));

        mUserImage = mMainView.findViewById(R.id.user_single_image);
        mUserSingleLayout = mMainView.findViewById(R.id.user_single_layout);
        mIm = mMainView.findViewById(R.id.prof_image);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        mUserRef.child("online").setValue(true);


        Query query = FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions<User> option = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.user_single_layout)
                .setQuery(query,User.class)
                .build();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(mDatabaseRef,User.class)
                .build();

        FirebaseRecyclerAdapter<User,UsersViewHolder> adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull User user) {

                usersViewHolder.setName(user.getName());
                usersViewHolder.setStatus(user.getStatus());
                usersViewHolder.setImage(user.getImage());

                final String uid = getRef(i).getKey();

                if (mCurr.equals(uid)){
                    usersViewHolder.setName(user.getName()+" (Me)");
                }

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProfileAct.class);
                        intent.putExtra("user_id", uid);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout,parent,false);
                return new UsersViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
        adapter.startListening();
        mUserList.setAdapter(adapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView mUserName = mView.findViewById(R.id.user_single_name);
            mUserName.setText(name);
        }
        public void setStatus(String status){
            TextView mUserStatus = mView.findViewById(R.id.user_single_status);
            mUserStatus.setText(status);
        }
        public void setImage(final String image){
            final CircleImageView mUserImage = mView.findViewById(R.id.user_single_image);
            //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mUserImage);
            if (!image.equals("default")) {
                //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.logo_wop)
                        .into(mUserImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(mUserImage);
                            }
                        });
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
    }
}
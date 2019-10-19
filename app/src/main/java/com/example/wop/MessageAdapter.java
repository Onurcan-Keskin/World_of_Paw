package com.example.wop;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wop.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.TAG;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private DatabaseReference mRootRef;

    public MessageAdapter(List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {


        String currentUID = mAuth.getCurrentUser().getUid();
        Log.d("TAG id ==========>>>>> ", currentUID);
        mRootRef = FirebaseDatabase.getInstance().getReference();

        Messages c = mMessagesList.get(position);
        String fromUID = c.getFrom();
        int orange = Color.rgb(230, 81, 0);

        if (fromUID.equals(currentUID)) {

            holder.mSendmessageTxt.setTextColor(Color.BLACK);
            holder.mSendcard.setCardBackgroundColor(Color.WHITE);
            holder.mSendcard.setRadius(20);
            holder.mSendRelative.setVisibility(View.VISIBLE);
            holder.mSendmessageTxt.setText(c.getMessage());

        } else {
            holder.mRecmessageTxt.setTextColor(Color.WHITE);
            holder.mReccard.setCardBackgroundColor(orange);
            holder.mReccard.setRadius(20);
            holder.mRecRelative.setVisibility(View.VISIBLE);
            holder.mRecmessageTxt.setText(c.getMessage());
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user_profile");
        mDatabaseRef.keepSynced(true);

        return new MessageViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mSendRelative, mRecRelative;
        private TextView mSendmessageTxt, mSendmessageNameTxt;
        private TextView mRecmessageTxt, mRecmessageNameTxt;
        private TextView mSendtimeTxt, mRectimeTxt;
        private CircleImageView mSendprofimage;
        private CircleImageView mRecprofimage;
        private CardView mSendcard, mReccard;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            /* Receiver */
            mRecRelative = itemView.findViewById(R.id.rec_relative);
            mReccard = itemView.findViewById(R.id.rec_message_card_view);
            mRecmessageTxt = itemView.findViewById(R.id.rec_message_item);
            mRecmessageNameTxt = itemView.findViewById(R.id.rec_message_name);
            mRectimeTxt = itemView.findViewById(R.id.rec_message_timestamp);
            mRecprofimage = itemView.findViewById(R.id.rec_message_pic);

            /* Sender */
            mSendRelative = itemView.findViewById(R.id.send_relative);
            mSendcard = itemView.findViewById(R.id.send_message_card_view);
            mSendmessageTxt = itemView.findViewById(R.id.send_message_item);
            mSendmessageNameTxt = itemView.findViewById(R.id.send_message_name);
            mSendtimeTxt = itemView.findViewById(R.id.send_message_timestamp);
            mSendprofimage = itemView.findViewById(R.id.send_message_pic);
        }

        public void setName(String name) {
            TextView mUserName = itemView.findViewById(R.id.rec_message_name);
            mUserName.setText(name);
        }

        public void setImage(final String image) {
            final CircleImageView mUserImage = itemView.findViewById(R.id.rec_message_pic);
            //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mUserImage);
            if (!image.equals("default")) {
                //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mImage);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
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
}

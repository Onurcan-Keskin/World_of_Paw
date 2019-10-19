package com.example.wop;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class MessagingPagerAdapter extends FragmentPagerAdapter {

    public MessagingPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                AllUserFragment allUserFragment = new AllUserFragment();
                return allUserFragment;
            case 1:
                RequestsFragment requestsFragment= new RequestsFragment();
                return requestsFragment;
            case 2:
                MessagingFragment messagingFragment = new MessagingFragment();
                return messagingFragment;
            case 3:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "World of Paw Users";
            case 1:
                return "REQUESTS";
            case 2:
                return "CHATS";
            case 3:
                return "FRIENDS";
            default:
                return null;
        }
    }
}

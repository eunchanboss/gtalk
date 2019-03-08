package com.example.dldmd.gtalk.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.dldmd.gtalk.chat.ChatListFragment;
import com.example.dldmd.gtalk.friend.FriendListFragment;
import com.example.dldmd.gtalk.post.PostListFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private static int PAGE_NUMBER=3;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return PostListFragment.newInstance();
            case 1:
                return FriendListFragment.newInstance();
            case 2:
                return ChatListFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Post";
            case 1:
                return "Friends";
            case 2:
                return "Chat";
            default:
                return null;
        }
    }
}

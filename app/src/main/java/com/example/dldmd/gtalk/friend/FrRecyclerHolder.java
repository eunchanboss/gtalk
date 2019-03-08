package com.example.dldmd.gtalk.friend;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dldmd.gtalk.R;

public class FrRecyclerHolder extends RecyclerView.ViewHolder {
    public TextView userName;
    public TextView userUid;
    public ImageView userImg;

    public FrRecyclerHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.userName);
        userUid = itemView.findViewById(R.id.userUid);
        userImg = itemView.findViewById(R.id.userImg);
    }
}

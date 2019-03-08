package com.example.dldmd.gtalk.chat;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dldmd.gtalk.R;

public class ChatInRecylerHolder extends RecyclerView.ViewHolder {

    public TextView fMessage;
    public TextView message;
    public ConstraintLayout fLay;
    public ConstraintLayout mLay;
    public ImageView userImg;
    public TextView fnowTime;
    public TextView nowTime;

    public ChatInRecylerHolder(@NonNull View itemView) {
        super(itemView);
        fMessage = itemView.findViewById(R.id.fMessage);
        message = itemView.findViewById(R.id.message);
        fLay = itemView.findViewById(R.id.fLay);
        mLay = itemView.findViewById(R.id.mLay);
        userImg = itemView.findViewById(R.id.userImg);
        fnowTime = itemView.findViewById(R.id.fnowTime);
        nowTime = itemView.findViewById(R.id.nowTime);
    }
}

package com.example.dldmd.gtalk.chat;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dldmd.gtalk.R;

public class ChatroomRecylerHolder extends RecyclerView.ViewHolder {

    public TextView fname;
    public TextView latelyMessage;
    public ImageView fImg;
    public TextView nowTime;



    public ChatroomRecylerHolder(@NonNull View itemView) {
        super(itemView);
        fname = itemView.findViewById(R.id.fname);
        latelyMessage = itemView.findViewById(R.id.latelyMessage);
        fImg = itemView.findViewById(R.id.fImg);
        nowTime = itemView.findViewById(R.id.nowTime);
    }
}

package com.example.dldmd.gtalk.post;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dldmd.gtalk.R;

public class PostRecyclerHolder extends RecyclerView.ViewHolder {
    public ImageView userImg;
    public TextView userName;
    public TextView nowTime;
    public TextView contentsId;
    public TextView contents;
    public ImageView contentsImg;
    public Button transBtn;
    public Button basicBtn;
    public TextView tContents;



    public PostRecyclerHolder(@NonNull View itemView) {

        super(itemView);
        userImg = itemView.findViewById(R.id.userImg);
        userName = itemView.findViewById(R.id.userName);
        nowTime = itemView.findViewById(R.id.nowTime);
        contentsId = itemView.findViewById(R.id.contentsId);
        contents = itemView.findViewById(R.id.contents);
        contentsImg = itemView.findViewById(R.id.contentsImg);
        transBtn = itemView.findViewById(R.id.transBtn);
        basicBtn = itemView.findViewById(R.id.basicBtn);
        tContents = itemView.findViewById(R.id.tContents);

    }
}

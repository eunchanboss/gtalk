package com.example.dldmd.gtalk.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.profile.Profile;
import com.example.dldmd.gtalk.profile.ProfileDetail;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FrAdapter extends RecyclerView.Adapter<FrRecyclerHolder>{
    private ArrayList<UserVO> frList;
    Context context;
    Bitmap bitmap;
    String uImg;
    String intentFuid;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://gtalk-9ff0a.appspot.com");
    StorageReference storageRef = storage.getReference();
    StorageReference spaceRef = storageRef.child("images/");

    public FrAdapter(ArrayList itemList) {
        this.frList = itemList;
    }

    @NonNull
    @Override
    public FrRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parant, int i) {
        View view = LayoutInflater.from(parant.getContext()).inflate(R.layout.fragment_friend_item_list,parant,false);
        //뷰그룹에 접근
        context = parant.getContext();
        FrRecyclerHolder holder = new FrRecyclerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FrRecyclerHolder frRecyclerHolder, int position) {
        frRecyclerHolder.userName.setText(frList.get(position).getUserName());
        frRecyclerHolder.userUid.setText(frList.get(position).getUserUid());
        spaceRef = spaceRef.child(frList.get(position).getUserImg());

        Glide.with(frRecyclerHolder.itemView)
                .load(frList.get(position).getUserImg())
                .into(frRecyclerHolder.userImg);

        intentFuid = frList.get(position).getUserImg();
        frRecyclerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("FRIEND_UID",frRecyclerHolder.userUid.getText());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return frList.size();
    }
}

package com.example.dldmd.gtalk.post;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.PapagoDetect;
import com.example.dldmd.gtalk.PapagoNMT;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.profile.Profile;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostRecyclerHolder> {
    private ArrayList<PostVO> postList;
    UserVO userVO;
    Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userLang;
    PapagoDetect papagoDetect;
    PapagoNMT papagoNMT;

    String resultDet;
    String resultNMT;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userData = databaseReference.child("USER");

    public PostAdapter(ArrayList itemList){
        this.postList = itemList;
    }

    @NonNull
    @Override
    public PostRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parant, int i) {
        View view = LayoutInflater.from(parant.getContext()).inflate(R.layout.fragment_post_item_list,parant,false);
        //뷰그룹에 접근
        context = parant.getContext();
        PostRecyclerHolder holder = new PostRecyclerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostRecyclerHolder postRecyclerHolder, final int position) {
        userData.child(postList.get(position).getUserUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userVO = dataSnapshot.getValue(UserVO.class);
                postRecyclerHolder.userName.setText(userVO.getUserName());
                Glide.with(postRecyclerHolder.itemView)
                        .load(userVO.getUserImg())
                        .into(postRecyclerHolder.userImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postRecyclerHolder.nowTime.setText(postList.get(position).getNowTime());
        postRecyclerHolder.contentsId.setText(postList.get(position).getPostContentsId());
        postRecyclerHolder.contents.setText(postList.get(position).getPostContents());

        Glide.with(postRecyclerHolder.itemView)
                .load(postList.get(position).getPostContentsImg())
                .into(postRecyclerHolder.contentsImg);
        postRecyclerHolder.contentsImg.setVisibility(View.VISIBLE);
        if(postList.size()>0){
            postRecyclerHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,PostDetail.class);
                    intent.putExtra("CONTENTS_ID",postList.get(position).getPostContentsId());
                    context.startActivity(intent);
                }
            });
        }
        postRecyclerHolder.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getUid().equals(postList.get(position).getUserUid())){
                    Toast.makeText(context.getApplicationContext(),"본인 프로필 입니다",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(context,Profile.class);
                    intent.putExtra("FRIEND_UID",postList.get(position).getUserUid());
                    context.startActivity(intent);
                }
            }
        });

        postRecyclerHolder.contents.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context.getApplicationContext(),postList.get(position).getPostContents(),Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //사용자 언어 선택에 맞는 언어 감지
        userData.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userVO = dataSnapshot.getValue(UserVO.class);
                userLang = userVO.getUserLang();


                papagoDetect = new PapagoDetect();
                try{
                    resultDet = papagoDetect.execute(postRecyclerHolder.contents.getText().toString()).get();
                    if(resultDet.contains("ko")){
                        resultDet = "ko";
                    }else if(resultDet.contains("en")){
                        resultDet = "en";
                    }else if(resultDet.contains("ja")){
                        resultDet = "ja";
                    }
                    if(!resultDet.equals(userLang)){
                        //사용자 언어와 감지된 언거가 다를때만 버튼 표시
                        postRecyclerHolder.transBtn.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e){
                    System.out.println(e);
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //언어 감지 후 번역 이벤트
        postRecyclerHolder.transBtn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                try{
                    papagoNMT = new PapagoNMT();
                    papagoDetect = new PapagoDetect();
                    resultDet = papagoDetect.execute(postRecyclerHolder.contents.getText().toString()).get();
                    if(resultDet.contains("ko")){
                        resultDet = "ko";
                    }else if(resultDet.contains("en")){
                        resultDet = "en";
                    }else if(resultDet.contains("ja")){
                        resultDet = "ja";
                    }
                    resultNMT = papagoNMT.execute(postRecyclerHolder.contents.getText().toString(),resultDet,userLang).get();
                    resultNMT = resultNMT.split(":")[8];
                    resultNMT = resultNMT.split("\"")[1];

                    postRecyclerHolder.contents.animate().translationX(2000).setDuration(1000).withLayer();

                    postRecyclerHolder.tContents.setVisibility(View.VISIBLE);
                    postRecyclerHolder.tContents.setText(resultNMT);
                    postRecyclerHolder.tContents.animate().translationX(0).setDuration(1000).withLayer();
                    //postRecyclerHolder.contents.setText(resultNMT);
                    postRecyclerHolder.transBtn.setVisibility(View.GONE);
                    postRecyclerHolder.basicBtn.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });
        postRecyclerHolder.basicBtn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                postRecyclerHolder.contents.animate().translationX(0).setDuration(1000).withLayer();
                //postRecyclerHolder.contents.setText(postList.get(position).getPostContents());
                postRecyclerHolder.tContents.animate().translationX(-2000).setDuration(1000).withLayer();
                postRecyclerHolder.tContents.setVisibility(View.GONE);
                postRecyclerHolder.transBtn.setVisibility(View.VISIBLE);
                postRecyclerHolder.basicBtn.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}

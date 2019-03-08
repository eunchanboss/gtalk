package com.example.dldmd.gtalk.chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.PapagoDetect;
import com.example.dldmd.gtalk.PapagoNMT;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.profile.Profile;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class ChatInAdapter extends RecyclerView.Adapter<ChatInRecylerHolder> {
    Context context;
    private ArrayList<ChatVO> chatList;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userData = databaseReference.child("USER");
    UserVO userVO;

    String fimg;
    String fuid;
    String flang;
    String mlang;

    //번역
    PapagoNMT papagoNMT;
    PapagoDetect papagoDetect;

    String resultDet;
    String resultNMT;


    int po =1;

    public ChatInAdapter(ArrayList itemList){
        this.chatList = itemList;
    }
    @NonNull
    @Override
    public ChatInRecylerHolder onCreateViewHolder(@NonNull ViewGroup parant, int i) {
        View view = LayoutInflater.from(parant.getContext()).inflate(R.layout.fragment_chatin_item_list,parant,false);
        //뷰그룹에 접근
        context = parant.getContext();

        ChatInRecylerHolder holder = new ChatInRecylerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatInRecylerHolder chatInRecylerHolder, final int position) {



        if(chatList.get(position).getUserUid().equals(firebaseAuth.getUid())){
            chatInRecylerHolder.message.setText(chatList.get(position).getMessage());
            chatInRecylerHolder.nowTime.setText(chatList.get(position).getNowTime());
            chatInRecylerHolder.mLay.setVisibility(View.VISIBLE);
            chatInRecylerHolder.fLay.setVisibility(View.GONE);
        }else{
            chatInRecylerHolder.fMessage.setText(chatList.get(position).getMessage());
            chatInRecylerHolder.fnowTime.setText(chatList.get(position).getNowTime());
            chatInRecylerHolder.fLay.setVisibility(View.VISIBLE);
            chatInRecylerHolder.mLay.setVisibility(View.GONE);

            chatInRecylerHolder.userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Profile.class);
                    intent.putExtra("FRIEND_UID",chatList.get(position).getUserUid());
                    context.startActivity(intent);
                }
            });

        }
        //friend info
        userData.child(chatList.get(position).getUserUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userVO = dataSnapshot.getValue(UserVO.class);
                flang = userVO.getUserLang();
                fimg = userVO.getUserImg();

                Glide.with(chatInRecylerHolder.itemView)
                        .load(fimg)
                        .into(chatInRecylerHolder.userImg);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //my info
        userData.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userVO = dataSnapshot.getValue(UserVO.class);
                mlang = userVO.getUserLang();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        chatInRecylerHolder.fMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                        papagoNMT = new PapagoNMT();
                        papagoDetect = new PapagoDetect();
                        resultDet = papagoDetect.execute(chatInRecylerHolder.fMessage.getText().toString()).get();
                        if(resultDet.contains("ko")){
                            resultDet = "ko";
                        }else if(resultDet.contains("en")){
                            resultDet = "en";
                        }else if(resultDet.contains("ja")){
                            resultDet = "ja";
                        }else{
                            resultDet = resultDet.split("\"")[3];
                            resultDet = resultDet.split("\"")[0];
                        }
                        resultNMT = papagoNMT.execute(chatInRecylerHolder.fMessage.getText().toString(),resultDet,mlang).get();
                        resultNMT = resultNMT.split(":")[8];
                        resultNMT = resultNMT.split("\"")[1];

                        chatInRecylerHolder.fMessage.setText(resultNMT);


                }catch (Exception e){

                }
            }
        });

        chatInRecylerHolder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                        papagoNMT = new PapagoNMT();
                        papagoDetect = new PapagoDetect();
                        resultDet = papagoDetect.execute(chatInRecylerHolder.message.getText().toString()).get();

                        if(resultDet.contains("ko")){
                            resultDet = "ko";
                        }else if(resultDet.contains("en")){
                            resultDet = "en";
                        }else if(resultDet.contains("ja")){
                            resultDet = "ja";
                        }else{
                            resultDet = resultDet.split("\"")[3];
                            resultDet = resultDet.split("\"")[0];
                        }
                        resultNMT = papagoNMT.execute(chatInRecylerHolder.message.getText().toString(),resultDet,mlang).get();
                        resultNMT = resultNMT.split(":")[8];
                        resultNMT = resultNMT.split("\"")[1];
                        System.out.println(resultNMT);

                        chatInRecylerHolder.message.setText(resultNMT);


                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });
        chatInRecylerHolder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                chatInRecylerHolder.message.setText(chatList.get(position).getMessage());
                return false;
            }
        });

        chatInRecylerHolder.fMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                chatInRecylerHolder.fMessage.setText(chatList.get(position).getMessage());
                return false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

package com.example.dldmd.gtalk.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.post.PostRecyclerHolder;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomRecylerHolder> {
    private ArrayList<UserVO> chatUserList;
    Context context;
    private ArrayList<ChatVO> chatList = new ArrayList<>();
    ChatVO chatVO;


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference chatData = databaseReference.child("CHAT");
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    public ChatroomAdapter(ArrayList itemList){
        this.chatUserList = itemList;
    }

    @NonNull
    @Override
    public ChatroomRecylerHolder onCreateViewHolder(@NonNull ViewGroup parant, int i) {
        View view = LayoutInflater.from(parant.getContext()).inflate(R.layout.fragment_chat_room_item_list,parant,false);
        //뷰그룹에 접근
        context = parant.getContext();
        ChatroomRecylerHolder holder = new ChatroomRecylerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatroomRecylerHolder chatroomRecylerHolder, final int position) {
        chatroomRecylerHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("삭제")
                        .setMessage("Yes를 누르시면 대화가 삭제됩니다.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chatData.child(firebaseAuth.getUid()).child(chatUserList.get(position).getUserUid()).removeValue();
                                Intent intent = new Intent(context,MainActivity.class);
                                intent.putExtra("VP_RELOAD",2);
                                context.startActivity(intent);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });

        chatroomRecylerHolder.fname.setText(chatUserList.get(position).getUserName());
        Glide.with(chatroomRecylerHolder.itemView)
                .load(chatUserList.get(position).getUserImg())
                .into(chatroomRecylerHolder.fImg);

        chatData.child(firebaseAuth.getUid()).child(chatUserList.get(position).getUserUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ml : dataSnapshot.getChildren()){
                chatVO = ml.getValue(ChatVO.class);
                chatList.add(chatVO);
            }
            if(chatList.size()>0){
                chatroomRecylerHolder.latelyMessage.setText(chatList.get(chatList.size()-1).getMessage());
                chatroomRecylerHolder.nowTime.setText(chatList.get(chatList.size()-1).getNowTime());
            }
        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatroomRecylerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatRoom.class);
                intent.putExtra("FRIEND_UID",chatUserList.get(position).getUserUid());
                intent.putExtra("FRIEND_IMG",chatUserList.get(position).getUserImg());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }
}

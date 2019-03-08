package com.example.dldmd.gtalk.profile;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.chat.ChatRoom;
import com.example.dldmd.gtalk.friend.FriendVO;
import com.example.dldmd.gtalk.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity{

    ImageView userImg;
    TextView userNick;
    TextView userName;
    TextView userLang;

    Button sendBtn;
    Button deleteBtn;
    Button addBtn;

    UserVO userVO;
    FriendVO friendVO;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userData = databaseReference.child("USER");
    DatabaseReference friendData = databaseReference.child("FRIEND");
    DatabaseReference chatData = databaseReference.child("CHAT");


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    String fname;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://gtalk-9ff0a.appspot.com").child("image/");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_lay);

        userNick = findViewById(R.id.userNick);
        userName = findViewById(R.id.userName);
        userLang = findViewById(R.id.userLang);

        userImg = findViewById(R.id.userImg);
        deleteBtn = findViewById(R.id.deleteBtn);
        sendBtn = findViewById(R.id.sendBtn);
        addBtn = findViewById(R.id.addBtn);
        Intent gIntent = getIntent();
        fname = gIntent.getStringExtra("FRIEND_UID");

        friendData.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot fl : dataSnapshot.getChildren()){
                    friendVO = fl.getValue(FriendVO.class);
                    if(friendVO.getFriendUid().equals(fname)){
                        sendBtn.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.VISIBLE);
                        addBtn.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        userData.child(fname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userVO = dataSnapshot.getValue(UserVO.class);
                userNick.setText(userVO.getUserNick());
                userName.setText(userVO.getUserName());
                switch (userVO.getUserLang()){
                    case "ko":
                        userLang.setText("한국어 사용자");
                        break;
                    case "en":
                        userLang.setText("영어 사용자");
                        break;
                    case "ja":
                        userLang.setText("일본어 사용자");
                        break;
                }




                Glide.with(userImg)
                        .load(userVO.getUserImg())
                        .into(userImg);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        friendData.child(firebaseAuth.getUid()).child(fname).setValue(null);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("VP_RELOAD",1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    }
                });

                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("tt");
                        Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                        intent.putExtra("FRIEND_UID",userVO.getUserUid());
                        intent.putExtra("FRIEND_IMG",userVO.getUserImg());
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendVO = new FriendVO(userVO.userUid,userVO.userName,userVO.userImg);
                dataSave(friendVO);
                addBtn.setVisibility(View.GONE);
                sendBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            }
        });

    }

    //데이터 저장
    public void dataSave(FriendVO friendVO){
        firebaseAuth = FirebaseAuth.getInstance();
        Map<String,Object> frData = new HashMap<>();
        frData.put(firebaseAuth.getUid(),friendVO);

        friendData.child(firebaseAuth.getUid()).child(friendVO.getFriendUid()).setValue(friendVO);
    }
}

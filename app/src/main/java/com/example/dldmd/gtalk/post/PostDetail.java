package com.example.dldmd.gtalk.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.profile.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostDetail extends AppCompatActivity {

    //데이터를 담을 텍스트뷰
    TextView userNameTv;
    TextView userUidTv;
    TextView nowTimeTv;
    TextView contentsTv;


    ImageView loadImg;
    ImageView userImg;

    //받아온 글id 저장 변수
    String gContentsId;

    Button listBtn;

    //보이지 않는 버튼
    Button margeBtn;
    Button deleteBtn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference contentsData = databaseReference.child("CONTENTS");
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post_detail);

        userNameTv = findViewById(R.id.userName);
        userUidTv = findViewById(R.id.userUid);
        nowTimeTv = findViewById(R.id.nowTime);
        contentsTv = findViewById(R.id.contents);
        userImg = findViewById(R.id.userImg);
        loadImg = findViewById(R.id.loadImg);

        deleteBtn = findViewById(R.id.deleteBtn);
        margeBtn = findViewById(R.id.margeBtn);

        //글 아이디 받아오기
        Intent gIntent = getIntent();
        gContentsId = gIntent.getStringExtra("CONTENTS_ID");

        contentsData.child(gContentsId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostVO postVO = dataSnapshot.getValue(PostVO.class);
                try{
                    //받아온 값으로 set
                    userNameTv.setText(postVO.getUserName());
                    userUidTv.setText(postVO.getUserUid());
                    contentsTv.setText(postVO.getPostContents());
                    nowTimeTv.setText(postVO.getNowTime());
                    Glide.with(userImg)
                            .load(postVO.getUserImg())
                            .into(userImg);
                    Glide.with(loadImg)
                            .load(postVO.getPostContentsImg())
                            .into(loadImg);

                    firebaseAuth = FirebaseAuth.getInstance();
                    if(postVO.getUserUid().equals(firebaseAuth.getUid())){
                        deleteBtn.setVisibility(View.VISIBLE);
                        margeBtn.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listBtn = findViewById(R.id.listBtn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentsData.child(gContentsId).setValue(new PostVO());
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        margeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PostMarge.class);
                intent.putExtra("CONTENTS_ID",gContentsId);
                startActivity(intent);
            }
        });

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("FRIEND_UID",userUidTv.getText().toString());
                startActivity(intent);
            }
        });

        userNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("FRIEND_UID",userUidTv.getText().toString());
                startActivity(intent);
            }
        });

    }
}

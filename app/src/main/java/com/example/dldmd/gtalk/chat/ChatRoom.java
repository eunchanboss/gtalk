package com.example.dldmd.gtalk.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dldmd.gtalk.PapagoDetect;
import com.example.dldmd.gtalk.PapagoNMT;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoom extends AppCompatActivity {

    Button sendBtn;
    EditText editText;
    CheckBox checkBox;

    //안내메시지 다이어로그
    TextView chatInfo;
    Animation animation;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference chatData = databaseReference.child("CHAT");
    DatabaseReference userData = databaseReference.child("USER");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    String guid;
    String gimg;
    String gLang;
    String mLang;
    ChatVO chatVO;
    String trans;


    String nowTime;
    Date today = new Date();
    private ArrayList<ChatVO> chatList;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChatInAdapter chatInAdapter;

    PapagoDetect papagoDetect;
    PapagoNMT papagoNMT;

    String resultDet;
    String resultNMT;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chatin_list);

        sendBtn = findViewById(R.id.sendBtn);
        editText = findViewById(R.id.editText);
        checkBox = findViewById(R.id.transCheck);

        Intent gIntent = getIntent();
        guid = gIntent.getStringExtra("FRIEND_UID");


        chatInfo = findViewById(R.id.chatInfo);
        animation = AnimationUtils.loadAnimation(this,R.anim.chat_dialog);
        //chatInfo.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                chatInfo.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        chatInfo.setAnimation(animation);




        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        //리니어 레이아웃 객체 생성
        linearLayoutManager = new LinearLayoutManager(this);
        //리니어 레이아웃 리사이클러뷰에 세로로 붙일수 있게 지정
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatList = new ArrayList<>();
        chatList.clear();
        chatData.child(firebaseAuth.getUid()).child(guid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ml : dataSnapshot.getChildren()){
                    chatVO = ml.getValue(ChatVO.class);
                    chatList.add(chatVO);

                    chatInAdapter = new ChatInAdapter(chatList);
                    recyclerView.setAdapter(chatInAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userData.child(guid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserVO userVO = dataSnapshot.getValue(UserVO.class);
                gLang = userVO.getUserLang();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userData.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserVO userVO = dataSnapshot.getValue(UserVO.class);
                mLang = userVO.getUserLang();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        checkBox.setChecked(false);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString()!=null||!editText.getText().toString().equals("")) {
                    System.out.println(editText.getText().toString());
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                    nowTime = time.format(today);

                    if (checkBox.isChecked()) {
                        trans = editText.getText().toString();


                        try {
                            papagoNMT = new PapagoNMT();
                            papagoDetect = new PapagoDetect();
                            resultDet = papagoDetect.execute(trans).get();
                            if (resultDet.contains("ko")) {
                                resultDet = "ko";
                            } else if (resultDet.contains("en")) {
                                resultDet = "en";
                            } else if (resultDet.contains("ja")) {
                                resultDet = "ja";
                            }
                            resultNMT = papagoNMT.execute(trans, resultDet, gLang).get();
                            resultNMT = resultNMT.split(":")[8];
                            resultNMT = resultNMT.split("\"")[1];
                            trans = resultNMT;
                            chatVO = new ChatVO(firebaseAuth.getUid(),trans,nowTime);
                            String push = chatData.push().getKey();
                            chatData.child(firebaseAuth.getUid()).child(guid).child(push).setValue(chatVO);
                            chatData.child(guid).child(firebaseAuth.getUid()).child(push).setValue(chatVO);
                            editText.setText("");

                        } catch (Exception e) {
                            System.out.println(e);

                        }


                    } else {
                        trans = editText.getText().toString();
                        chatVO = new ChatVO(firebaseAuth.getUid(),trans,nowTime);
                        String push = chatData.push().getKey();
                        chatData.child(firebaseAuth.getUid()).child(guid).child(push).setValue(chatVO);
                        chatData.child(guid).child(firebaseAuth.getUid()).child(push).setValue(chatVO);
                        editText.setText("");
                    }


                }else{
                    Toast.makeText(getApplicationContext(),"메세지를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("VP_RELOAD",2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }
}

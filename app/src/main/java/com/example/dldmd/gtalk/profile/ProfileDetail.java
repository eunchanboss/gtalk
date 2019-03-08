package com.example.dldmd.gtalk.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileDetail extends AppCompatActivity {

    ImageView userImg;
    String uImg;
    Bitmap bitmap;

    TextView userNick;
    TextView userName;
    TextView userLang;


    Button modifyBtn;
    Button finishBtn;

    UserVO userVO;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userData = databaseReference.child("USER");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://gtalk-9ff0a.appspot.com").child("image/");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_detail_lay);

        userNick = findViewById(R.id.userNick);
        userName = findViewById(R.id.userName);
        userLang = findViewById(R.id.userLang);

        userImg = findViewById(R.id.userImg);

        //프로필수정 화면으로 이동
        modifyBtn = findViewById(R.id.modifyBtn);
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileMarge.class);
                startActivity(intent);
            }
        });

        //메인 화면으로 이동
        finishBtn = findViewById(R.id.finishBtn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        userData.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
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
                //userImg.setImageURI(Uri.parse(userVO.getUserImg()));
                uImg = userVO.getUserImg();
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try{
                            URL url = new URL(uImg);
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);
                        }catch (Exception e){
                            System.out.println(e+": 실패원인 입니다.");
                        }
                        super.run();
                    }
                };
                thread.start();

                try{
                    thread.join();
                    userImg.setImageBitmap(bitmap);
                }catch (Exception e){
                    System.out.println(e+": 실패원인 입니다2.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

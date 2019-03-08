package com.example.dldmd.gtalk.friend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FrAdd extends AppCompatActivity {

    EditText searchUserNick;
    Button searchBtn;
    ImageView searchUserImg;
    TextView searchUserName;
    Button addBtn;

    Bitmap bitmap;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference getUser = databaseReference.child("USER");
    UserVO userVO;
    //닉네임 조회 변수
    String lookup;

    //친구추가시 필요한 변수
    FriendVO friendVO;
    String fname;
    String fuid;
    String fimg;


    DatabaseReference getFriend = databaseReference.child("FRIEND");
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_friedn_add);
        //초기상태 visible
        searchUserNick = findViewById(R.id.searchUserNick);
        searchBtn = findViewById(R.id.searchBtn);
        //초기상태 gone
        searchUserImg = findViewById(R.id.searchUserImg);
        searchUserName = findViewById(R.id.searchUserName);
        addBtn = findViewById(R.id.addBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookup = searchUserNick.getText().toString();
                getUser.orderByChild("userNick").equalTo(lookup).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            String searchTest = dataSnapshot.getValue().toString().split("=")[0];
                            searchTest = searchTest.split("\\{")[1];

                            //조회한 아이디로 다시한번 데이터 조회한후 레이아웃에 set
                            getUser.child(searchTest).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    userVO = dataSnapshot.getValue(UserVO.class);
                                    fuid = userVO.getUserUid();
                                    fname = userVO.getUserName();
                                    fimg = userVO.getUserImg();

                                    searchUserName.setVisibility(View.VISIBLE);
                                    //이름 set, visible 상태변경
                                    //버튼 보이기
                                    firebaseAuth = FirebaseAuth.getInstance();

                                    if(!firebaseAuth.getUid().equals(fuid)){

                                        getFriend.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.getChildrenCount()==0){
                                                    addBtn.setVisibility(View.VISIBLE);
                                                    searchUserName.setText(userVO.getUserName());
                                                }else{
                                                    for(DataSnapshot fr : dataSnapshot.getChildren()){
                                                        friendVO = fr.getValue(FriendVO.class);
                                                        String key = fr.getKey();

                                                        if(fuid.equals(friendVO.getFriendUid())){
                                                            searchUserName.setText("이미 등록된 친구 입니다");
                                                            break;
                                                        }else{
                                                            addBtn.setVisibility(View.VISIBLE);
                                                            searchUserName.setText(userVO.getUserName());
                                                        }
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }else{
                                        searchUserName.setText("본인 프로필입니다.");
                                    }

                                    //이미지 보이기
                                    Glide.with(searchUserImg)
                                            .load(userVO.getUserImg())
                                            .into(searchUserImg);
                                    searchUserImg.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"존재하지 않는 닉네임 입니다",Toast.LENGTH_SHORT).show();
                            searchUserName.setVisibility(View.GONE);
                            searchUserImg.setVisibility(View.GONE);
                            addBtn.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("실패했다ㅠㅠ");
                    }
                });
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendVO = new FriendVO(fuid,fname,fimg);
                dataSave(friendVO);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("VP_RELOAD",1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    //데이터 저장
    public void dataSave(FriendVO friendVO){
        firebaseAuth = FirebaseAuth.getInstance();
        Map<String,Object> frData = new HashMap<>();
        frData.put(firebaseAuth.getUid(),friendVO);

        getFriend.child(firebaseAuth.getUid()).child(friendVO.getFriendUid()).setValue(friendVO);
    }
}

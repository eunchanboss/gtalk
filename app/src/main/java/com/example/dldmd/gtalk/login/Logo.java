package com.example.dldmd.gtalk.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.profile.ProfileMarge;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Logo extends AppCompatActivity {

    Animation animation;
    TextView logo;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference getUser = databaseReference.child("USER");
    UserVO userVO;

    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);


        logo = findViewById(R.id.logo);

        animation = AnimationUtils.loadAnimation(this,R.anim.loading);
        logo.setAnimation(animation);


    }

    @Override
    protected void onStart() {
        super.onStart();
        //자동 로그인
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = mAuth.getUid();
        if(currentUser!=null){ // 만약 로그인이 되어있으면 다음 액티비티 실행
            getUser.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userVO = dataSnapshot.getValue(UserVO.class);
                    if(userVO!=null){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }else{
                        Intent intent = new Intent(getApplicationContext(), ProfileMarge.class);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }

    }
}

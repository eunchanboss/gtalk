package com.example.dldmd.gtalk.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.friend.FrAdd;
import com.example.dldmd.gtalk.login.Login;
import com.example.dldmd.gtalk.post.PostMarge;
import com.example.dldmd.gtalk.profile.Profile;
import com.example.dldmd.gtalk.profile.ProfileMarge;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    int currentItem;    //화면 넘버 받아오기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_lay);
        //뷰페이저 생성
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager=findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);

        Intent gIntent = getIntent();
        currentItem = gIntent.getIntExtra("VP_RELOAD",0);
        viewPager.setCurrentItem(currentItem);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //툴바 생성
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);



    }

    //toolbar에 메뉴 레이아웃 넣어주기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    //toolbar레이아웃 셀렉트 이벤트 발생
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.profileMenuBtn:
                intent = new Intent(getApplicationContext(), ProfileMarge.class);
                startActivity(intent);
                break;

            case R.id.logoutBtn:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("종료")
                .setMessage("Yes를 누르시면 어플이 종료됩니다.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}

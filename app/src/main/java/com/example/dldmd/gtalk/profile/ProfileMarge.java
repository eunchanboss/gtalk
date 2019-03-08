package com.example.dldmd.gtalk.profile;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileMarge extends AppCompatActivity {

    //프로필 이미지
    TextView userImgBtn;
    ImageView userImg;
    private Uri imageUri;

    //프로필정보
    EditText userNick;
    EditText userName;
    RadioGroup languageGroup;

    RadioButton ko;
    RadioButton en;
    RadioButton ja;

    String langauge = "ko";

    Button profileSaveBtn;

    //requestResultCode
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    FirebaseStorage firebaseStorage;
    FirebaseAuth firebaseAuth;
    //권한
    final int REQ_CODE_SELECT_IMAGE=100;
    final int MY_PERMISSION_REQUEST_STORAGE =101;
    private static final String TAG = "PidMarge";

    UserVO userVO;
    String uid;
    String nick;
    String name;
    String lang;
    String imgPath;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference setUser = databaseReference.child("USER");
    DatabaseReference contentsData = databaseReference.child("CONTENTS");


//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    StorageReference storageReference = storage.getReferenceFromUrl("gs://gtalk-9ff0a.appspot.com").child("image/");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_marge_lay);

        checkPermission();

        userNick = findViewById(R.id.userNick);
        userName = findViewById(R.id.userName);
        languageGroup = findViewById(R.id.languageGroup);
        languageGroup.setOnCheckedChangeListener(rch);

        ko = findViewById(R.id.ko);
        en = findViewById(R.id.en);
        ja = findViewById(R.id.ja);

        userImg = findViewById(R.id.userImg);
        userImgBtn = findViewById(R.id.userImgBtn);
        firebaseAuth = FirebaseAuth.getInstance();

            setUser.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userVO = dataSnapshot.getValue(UserVO.class);
                    if(userVO!=null){
                        userNick.setText(userVO.getUserNick());
                        userName.setText(userVO.getUserName());
                        switch (userVO.getUserLang()){
                            case "ko":
                                ko.isChecked();
                                break;
                            case "en":
                                en.isChecked();
                                break;
                            case "ja":
                                ja.isChecked();
                                break;
                        }
                        try{
                            Glide.with(userImg)
                                    .load(userVO.getUserImg())
                                    .into(userImg);
                        }catch (Exception e){

                        }
                        userImg.setVisibility(View.VISIBLE);
                        userImgBtn.setText(userVO.getUserImg());
                        userImgBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });






        userImg = findViewById(R.id.userImg);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카메라 호출
                doTakePhoto();
            }
        });

        //프로필 저장
        profileSaveBtn = findViewById(R.id.profileSaveBtn);
        profileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                uid = firebaseAuth.getUid();
                nick = userNick.getText().toString().trim();
                name = userName.getText().toString().trim();
                lang = langauge;
                imgPath = userImgBtn.getText().toString().trim();
                if(nick==null||name==null||imgPath==null||nick.equals(null)||("").equals(nick)||name.equals(null)||("").equals(name)||imgPath.equals(null)||("").equals(imgPath)){
                    Toast.makeText(getApplicationContext(),"빈칸을 모두 채워주세요",Toast.LENGTH_SHORT).show();
                }else{
                    Uri file = Uri.fromFile(new File(imgPath));
                    //스토리지 생성
                    firebaseStorage = FirebaseStorage.getInstance();
                    //파일명 생성(유니크)
                    //String file_name = UUID.randomUUID().toString()+".png";
                    //프로필은 uid로 파일명 대체
                    System.out.println("test11");
                    userVO = new UserVO(uid, nick, name, langauge,"gs://gtalk-9ff0a.appspot.com/image/"+uid );
                    dataSave(userVO);
                    //firebase 공간 주소와 폴더 지정해주기
                    StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://gtalk-9ff0a.appspot.com").child("image/"+uid);
                    if(imageUri!=null) {
                        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                userVO = new UserVO(uid, nick, name, langauge, downloadUrl.toString());
                                dataSave(userVO);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("실패?");
                                        Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                    else{
                        userVO = new UserVO(uid, nick, name, langauge, imgPath);
                        dataSave(userVO);
                    }
                    Intent intent = new Intent(getApplicationContext(), ProfileDetail.class);
                    startActivity(intent);
                }
            }
        });


    }
    RadioGroup.OnCheckedChangeListener rch =
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.ko:
                            langauge = "ko";
                            break;
                        case R.id.en:
                            langauge = "en";
                            break;
                        case R.id.ja:
                            langauge = "ja";
                            break;
                    }
                }
            };

    //갤러리 호출
    public void doTakePhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;

        switch (requestCode){
            case PICK_FROM_ALBUM:
                imageUri = data.getData();
                userImg.setImageURI(imageUri);
                userImgBtn.setText(imageUri.toString());
                userImgBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }
    //데이터 저장
    public void dataSave(UserVO userVO){
        Map<String,Object> userData = new HashMap<>();
        userData.put(firebaseAuth.getUid(),userVO);

        setUser.child(uid).setValue(userVO);


    }


    //사진 권한 체크
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
            writeFile();
        }
    }
    private void writeFile() {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "temp.txt");
        try {
            Log.d(TAG, "create new File : " + file.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    writeFile();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d(TAG, "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
}

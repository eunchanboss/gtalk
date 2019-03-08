package com.example.dldmd.gtalk.post;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dldmd.gtalk.R;
import com.example.dldmd.gtalk.main.MainActivity;
import com.example.dldmd.gtalk.profile.UserVO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PostMarge extends AppCompatActivity {

    Button saveBtn; //저장
    Button listBtn;   //취소 --> 목록으로 돌아감
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    //이미지
    ImageButton callGallery;
    ImageView loadImg;
    TextView loadImgName;
    ImageView userImg;

    String contentsId;

    final int REQ_CODE_SELECT_IMAGE=100;

    TextView userName;

    EditText editContents;

    String uid;
    String userImgPath;
    String nowTime;
    PostVO postVO;
    Date today = new Date();

    Uri saveImg;
    String saveImgString;
    String gContentsId;

    FirebaseStorage firebaseStorage;

    private static final String TAG = "PostMarge.class";
    UserVO userVO;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userData = databaseReference.child("USER");
    DatabaseReference contentsData = databaseReference.child("CONTENTS");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_post_marge);

        userName = findViewById(R.id.userName);
        userImg = findViewById(R.id.userImg);
        editContents = findViewById(R.id.editContents);
        Intent gIntent = getIntent();
        gContentsId = gIntent.getStringExtra("CONTENTS_ID");

        userData.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userVO = dataSnapshot.getValue(UserVO.class);
                uid = userVO.getUserUid();
                userName.setText(userVO.getUserName());
                userImgPath = userVO.getUserImg();
                Glide.with(userImg)
                        .load(userVO.getUserImg())
                        .into(userImg);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(gContentsId!=null){
            contentsData.child(gContentsId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    postVO = dataSnapshot.getValue(PostVO.class);
                    if(postVO!=null){
                        editContents.setText(postVO.getPostContents());
                        try{
                            Glide.with(loadImg)
                                    .load(postVO.getPostContentsImg())
                                    .into(loadImg);
                        }catch (Exception e){

                        }
                        loadImg.setVisibility(View.VISIBLE);
                        saveImgString = postVO.getPostContentsImg();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        //저장버튼
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //컨텐츠 미입력 체크
               try{
                   //글id 생성
                   SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd hh:mm:dd");
                   nowTime = time.format(today);
                   if(gContentsId==null) {
                       contentsId = contentsData.push().getKey();

                       //firebase 스토리지 이용 사진 저장
                       firebaseStorage = FirebaseStorage.getInstance();
                       StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://gtalk-9ff0a.appspot.com").child("image/" + contentsId);
                       storageRef.putFile(saveImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                               Uri downloadUrl = taskSnapshot.getDownloadUrl();
                               Uri downloadUrl = taskSnapshot.getUploadSessionUri();

                               postVO = new PostVO(uid, userName.getText().toString(), userImgPath, editContents.getText().toString(), contentsId, downloadUrl.toString(), nowTime);
                               contentsData.child(contentsId).setValue(postVO);
                           }
                       });
                   }else{

                       firebaseStorage = FirebaseStorage.getInstance();
                       StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://gtalk-9ff0a.appspot.com").child("image/" + gContentsId);
                       if(saveImg!=null){
                           System.out.println("not null");
                           storageRef.putFile(saveImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                   Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                   Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                   postVO = new PostVO(uid, userName.getText().toString(), userImgPath, editContents.getText().toString(), gContentsId, downloadUrl.toString(), nowTime);
                                   contentsData.child(gContentsId).setValue(postVO);
                               }
                           });
                       }else{
                           System.out.println("null");
                           postVO = new PostVO(uid, userName.getText().toString(), userImgPath, editContents.getText().toString(), gContentsId,saveImgString, nowTime);
                           contentsData.child(gContentsId).setValue(postVO);
                       }

                   }


                   Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
                   finish();

               }catch (Exception e){
                   System.out.println(e);
                    Toast.makeText(getApplicationContext(),"빈칸을 입력해주세요",Toast.LENGTH_SHORT).show();
               }

            }
        });

        //목록버튼
        listBtn = findViewById(R.id.listBtn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });




        loadImg = findViewById(R.id.loadImg);

    }

    @Override
    protected void onStart() {
        super.onStart();
        callGallery = findViewById(R.id.callGallery);
        callGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {

                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //Uri에서 이미지 이름을 얻어온다.
                    String name = getImageNameToUri(data.getData());

                    loadImg = (ImageView)findViewById(R.id.loadImg);
                    loadImg.setVisibility(View.VISIBLE);
                    loadImgName = (TextView)findViewById(R.id.loadImgName);
                    loadImgName.setText(name);
                    saveImg = data.getData();

                    //배치해놓은 ImageView에 set
                    /*loadImg.setImageBitmap(image_bitmap);
                    System.out.println(MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData()));
                    System.out.println("test");*/
                    /*URL url = new URL(name);
                    Bitmap bitmap = BitmapFactory.decodeFile(name);
                    loadImg.setImageBitmap(bitmap);*/
                    loadImg.setImageURI(Uri.parse(name));


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgPath;
    }

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

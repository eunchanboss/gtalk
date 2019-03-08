package com.example.dldmd.gtalk.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dldmd.gtalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText userEmail;
    EditText userPwd;

    Button signUpBtn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_lay);

        userEmail = findViewById(R.id.userEmail);
        userPwd = findViewById(R.id.userPwd);

        signUpBtn = findViewById(R.id.signUpBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString().trim();
                String pwd = userPwd.getText().toString().trim();

                if(isValidEmail(email)){
                    if(email.equals(null)||pwd.equals(null)){
                        Toast.makeText(getApplicationContext(),"빈칸이 존재합니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        firebaseAuth.createUserWithEmailAndPassword(email,pwd)
                                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUp.this,Login.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(),"아이디 등록에 실패했습니다.",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"올바른 이메일 형태가 아닙니다.",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    //이메일 형태 검증(정규식)
    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true;
        }
        return err;
    }
}

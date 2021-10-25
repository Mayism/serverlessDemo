package com.example.covid19detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.PhoneAuthProvider;
import com.huawei.agconnect.auth.PhoneUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginwindow);

        final EditText editText_num = findViewById(R.id.phoneNumber);
        //final EditText psd_input = findViewById(R.id.psdtext);
        final EditText veri_input = findViewById(R.id.veritext);
//      final String vericode =veri_result.getText().toString().trim();
        final String countryCodeStr = "86";
        //获取当前用户
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        //用户不为空，打开主页面
        if(user!=null){
            openUserWindow();
        }

        //验证码登录方法
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String phoneNumberStr = editText_num.getText().toString().trim();
                    String userVeri = veri_input.getText().toString().trim();
                    //获取验证码登录认证凭据
                    AGConnectAuthCredential credential = PhoneAuthProvider.credentialWithVerifyCode(countryCodeStr, phoneNumberStr, null, userVeri);
                    //登录
                    AGConnectAuth.getInstance().signIn(credential)
                            .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                                @Override
                                public void onSuccess(SignInResult signInResult) {
                                    //获取登录信息
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                    //打开用户界面
                                    openUserWindow();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception exception) {
                                    //提示登录失败信息
                                    Toast.makeText(LoginActivity.this, "Login fail:" + exception, Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    Log.e("login result","login with error");
                    Toast.makeText(LoginActivity.this,"登录失败的原因为"+e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

//        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    String phoneNumberStr = editText_num.getText().toString().trim();
//                    String userPsd = psd_input.getText().toString().trim();
//                    //获取认证凭据
//                    AGConnectAuthCredential credential = PhoneAuthProvider.credentialWithPassword(countryCodeStr, phoneNumberStr, userPsd);
//                    //登录
//                    AGConnectAuth.getInstance().signIn(credential)
//                            .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
//                                @Override
//                                public void onSuccess(SignInResult signInResult) {
//                                    //获取登录信息
//                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
//                                    //打开用户界面
//                                    openUserWindow();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(Exception exception) {
//                                    //提示登录失败信息
//                                    Toast.makeText(LoginActivity.this, "Login fail:" + exception, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                } catch (Exception e) {
//                    Log.e("login result","login with error");
//                    Toast.makeText(LoginActivity.this,"登录失败的原因为"+e.toString(),Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        //发送验证码方法
        findViewById(R.id.sendVeri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumberStr = editText_num.getText().toString().trim();
                VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                        .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                        .sendInterval(30)
                        .locale(Locale.CHINA)
                        .build();
                Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(countryCodeStr, phoneNumberStr, settings);
                task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
                    @Override
                    public void onSuccess(VerifyCodeResult verifyCodeResult) {
                        //验证码申请成功
                        Toast.makeText(LoginActivity.this,"send phone verify code success",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(LoginActivity.this, "requestVerifyCode fail:" + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.nologin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openRegisterWindow();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void openUserWindow(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainwindowActivity.class);
        startActivity(intent);
    }

    public void openRegisterWindow(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
package com.example.covid19detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.PhoneUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);
        final EditText editText_num = findViewById(R.id.rephoneNumber);
        final String countryCodeStr = "86";
        final EditText veri_code = findViewById(R.id.verifyCode);

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
                        Toast.makeText(RegisterActivity.this,"send phone verify code success",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(RegisterActivity.this, "requestVerifyCode fail:" + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumberStr = editText_num.getText().toString().trim();
                String vericode = veri_code.getText().toString().trim();
                PhoneUser phoneUser = new PhoneUser.Builder()
                        .setCountryCode(countryCodeStr)
                        .setPhoneNumber(phoneNumberStr)
                        .setVerifyCode(vericode)
                        .build();
                AGConnectAuth.getInstance().createUser(phoneUser)
                        .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                            @Override
                            public void onSuccess(SignInResult signInResult) {
                                //创建帐号成功后，默认已登录,进入主页面
                                Intent intent = new Intent();
                                intent.setClass(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(RegisterActivity.this, "register fail:" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
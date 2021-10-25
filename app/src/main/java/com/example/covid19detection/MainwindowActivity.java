package com.example.covid19detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.covid19detection.model.ObjectTypeInfoHelper;
import com.example.covid19detection.model.userInfo;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainwindowActivity extends AppCompatActivity {
    AGConnectCloudDB mCloudDB;
    CloudDBZoneConfig mConfig;
    CloudDBZone mCloudDBZone;
    private static final String TAG = "CloudDBWrapper";
    private UiCallBack mUiCallBack = UiCallBack.DEFAULT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainwindow);
        initializeData();

        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        //获取输入的name和location
        final EditText name_input = findViewById(R.id.nameinput);
        final EditText location_input = findViewById(R.id.regioninput);

        ActivityCompat.requestPermissions(this, permissions, 1);

        //点击提交后将用户填写数据存储到云端
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先保存数据
                try {
                    String name = name_input.getText().toString().trim();
                    String location = location_input.getText().toString().trim();
                    userInfo userinfo = new userInfo();
                    userinfo.setName(name);
                    userinfo.setLocation(location);
                    saveData(userinfo);
                }catch (Exception e){
                    Log.e(TAG,"save data to clouddb failed");
                }
            }
        });
    }

    private void saveData(userInfo userinfo) {
        if (mCloudDBZone == null) {
            Log.w("check CloudDBZone", "CloudDBZone is null, try re-open it");
            return;
        }
        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(userinfo);
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.i("upsert data", "Upsert " + cloudDBZoneResult + " records");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                mUiCallBack.updateUiOnError("Insert book info failed");
            }
        });
    }

    private void initializeData() {
        //初始化云数据库
        AGConnectCloudDB.initialize(getApplicationContext());
        mCloudDB = AGConnectCloudDB.getInstance();
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
            mConfig = new CloudDBZoneConfig("userInfoDemo",
                    CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                    CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
            mConfig.setPersistenceEnabled(true);
            Task<CloudDBZone> openDBZoneTask = mCloudDB.openCloudDBZone2(mConfig, true);
            openDBZoneTask.addOnSuccessListener(new OnSuccessListener<CloudDBZone>() {
                @Override
                public void onSuccess(CloudDBZone cloudDBZone) {
                    Log.i("open clouddbzone", "open cloudDBZone success");
                    mCloudDBZone = cloudDBZone;
                    // Add subscription after opening cloudDBZone success
                    //addSubscription();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.w("open clouddbzone", "open cloudDBZone failed for " + e.getMessage());
                }
            });
        } catch (AGConnectCloudDBException e) {
            Toast.makeText(MainwindowActivity.this, "initialize CloudDB failed" + e, Toast.LENGTH_LONG).show();
        }
    }

    public interface UiCallBack {
        UiCallBack DEFAULT = new UiCallBack() {
            @Override
            public void onAddOrQuery(List<userInfo> bookInfoList) {
                Log.i(TAG, "Using default onAddOrQuery");
            }

            @Override
            public void onSubscribe(List<userInfo> bookInfoList) {
                Log.i(TAG, "Using default onSubscribe");
            }

            @Override
            public void onDelete(List<userInfo> bookInfoList) {
                Log.i(TAG, "Using default onDelete");
            }

            @Override
            public void updateUiOnError(String errorMessage) {
                Log.i(TAG, "Using default updateUiOnError");
            }
        };

        void onAddOrQuery(List<userInfo> userInfoList);

        void onSubscribe(List<userInfo> userInfoList);

        void onDelete(List<userInfo> userInfoList);

        void updateUiOnError(String errorMessage);
    }
}
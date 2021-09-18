package com.example.covid19detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.io.File;
import java.util.Locale;

public class MainwindowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainwindow);

        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        final String proof= "covid.jpg";
        String filename = "covid.jpg";
        final File file = new File(getDirPath(),filename);
        ActivityCompat.requestPermissions(this, permissions, 1);

        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建实例
                AGCStorageManagement storageManagement = AGCStorageManagement.getInstance();
                //创建对文件夹的引用
                StorageReference reference = storageManagement.getStorageReference(proof);
                UploadTask task = reference.putFile(file);
                task.addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //上传失败
                        Toast.makeText(MainwindowActivity.this,"upload failed",Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.UploadResult>(){
                    @Override
                    public void onSuccess(UploadTask.UploadResult uploadResult) {
                        //上传成功
                        Toast.makeText(MainwindowActivity.this,"upload success!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private String getDirPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        System.out.println("path=" + path);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }
}
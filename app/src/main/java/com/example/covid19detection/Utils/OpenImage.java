package com.example.covid19detection.Utils;

import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;

public class OpenImage {
    Intent intent;
    private void openImageUtils() {

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
    }
}

package com.duohuan.device.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

import java.io.ByteArrayOutputStream;
import java.io.File;

import io.reactivex.Observable;

@SuppressLint("CheckResult")
public class UpdateImage {

    private static final String AK = "jlSVag2dnUima5TERVFn9SVirAKy7Hr7QZjy1_fv";
    private static final String SK = "ybTmJacdY_4XUDK_b7rxxNy5LA29HzB2fbl6czAh";
    private static final String BUCKET = "productimages";
    private static final Object obj = new Object();
    private static UpdateImage updateImage;
    private UploadManager uploadManager;
    private Auth auth;


    public Observable<String> upimage(Bitmap bitmap) {
        return Observable.defer(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            String token = auth.uploadToken(BUCKET);
            ResponseInfo responseInfo = uploadManager.syncPut(data, null, token, null);
            if (responseInfo.isOK()) {
                return Observable.just(responseInfo.response.getString("key"));
            } else {
                throw new Exception("Image Upload Error ->" + responseInfo.error);
            }
        });
    }


    public Observable<String> upimage(byte[] data) {
        return Observable.defer(() -> {
            String token = auth.uploadToken(BUCKET);
            ResponseInfo responseInfo = uploadManager.syncPut(data, null, token, null);
            if (responseInfo.isOK()) {
                return Observable.just(responseInfo.response.getString("key"));
            } else {
                throw new Exception("Image Upload Error ->" + responseInfo.error);
            }
        });
    }


    public Observable<String> upimage(File data) {
        return Observable.defer(() -> {
            String token = auth.uploadToken(BUCKET);
            ResponseInfo responseInfo = uploadManager.syncPut(data, null, token, null);
            if (responseInfo.isOK()) {
                return Observable.just(responseInfo.response.getString("key"));
            } else {
                throw new Exception("Image Upload Error ->" + responseInfo.error);
            }
        });
    }

    private UpdateImage() {
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
//                .recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
//                .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone2)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        uploadManager = new UploadManager(config);
        auth = Auth.create(AK, SK);
    }

    public static UpdateImage getInstance() {
        if (updateImage == null) {
            synchronized (obj) {
                if (updateImage == null) {
                    updateImage = new UpdateImage();
                }
            }
        }
        return updateImage;
    }

}

package com.duohuan.device;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cjt2325.cameralibrary.CameraInterface;
import com.duohuan.device.activity.BaseActivity;
import com.duohuan.device.entity.DeviceEntity;
import com.duohuan.device.entity.RequestEntity;
import com.duohuan.device.entity.UserBitmap;
import com.duohuan.device.util.Config;
import com.google.gson.Gson;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.tozmart.tozisdk.constant.Gender;
import com.tozmart.tozisdk.constant.Language;
import com.tozmart.tozisdk.constant.Unit;
import com.tozmart.tozisdk.entity.Profile2ResultData;
import com.tozmart.tozisdk.sdk.OneMeasureSDKLite;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint({"CheckResult", "SetTextI18n"})
public class MainActivity extends BaseActivity {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("my-lib");
    }

    private static LinkedList<String> messages = new LinkedList<>();

    //摄像头在使用
    private static final int CAMERA_USE = 1;

    //正面
    private static final int MODE_POSITIVE = 0x01;
    //侧面
    private static final int MODE_SIDE = 0x02;

    private VideoView mVideoView;
    private ImageView mImageView;
    private TextView mTextView;
    private RelativeLayout mLayout;

    private static Gson gson = new Gson();
    private static final String TAG = "MainActivity";
    private WebSocket webSocket;
    private WebSocket webSocketDevice;
    private CameraInterface mCameraInterface;
    private float screenProp;

    private boolean isUse = false;

    private int takePhotoTime = 5;
    private int undressingTime = 15;
    private int successTime = 5;
    private int errorTime = 5;
    private int inputTime = 30;

    private int current_mode = -1;
    private String current_userID;
    private int current_picture_mode = -1;

    private UserBitmap userBitmap = new UserBitmap();

    private Toast mToast;

    private Disposable showInputImage;
    private Disposable showUndressing;
    private Disposable pictureDisposable;
    private Disposable checkBitmapDisposable;
    private Disposable errorDisposable;
    private Disposable successDisposable;
    private Disposable heartBeatPag;
    private Disposable messageDisposable;


    private int retry = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.video_view);
        mTextView = findViewById(R.id.tv_number);
        mImageView = findViewById(R.id.image_view);
        mLayout = findViewById(R.id.rl_camera);
        checkPermission();
        initVideoView();
        openWebSocket();
        openDeviceWebSocket();

//        Observable.timer(5, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(l -> {
//                    DeviceEntity data = new DeviceEntity();
//                    data.setHeight(177);
//                    data.setGender(1);
//                    data.setWeight(88);
//                    data.setId("ID_New_2");
//                    startCreatePic(data, false);
//                });

        messageDisposable = Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .filter(l -> messages.size() > 0)
                .flatMap(l -> Observable.just(messages.removeFirst()))
                .subscribe(this::sendMessage);

        heartBeatPag = Observable.interval(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(l -> {
                    RequestEntity entity = new RequestEntity();
                    entity.setMode(-1);
                    if (webSocket != null && webSocket.isOpen()) {
//                        webSocket.send("{\"deviceId\":\"1\",\"entity\":\"{\\\"measurementEntities\\\":[{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize01_010.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":93.9655,\\\"meaValueInch\\\":\\\"36(7/8)\\\",\\\"sizeIntro\\\":\\\"The maximum horizontal girth with tape-measure passing over the shoulder blades (scapulae) and nipples (the most prominent protrusion of the bra cap for ladies).\\\",\\\"sizeName\\\":\\\"Bust girth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize01_030.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":81.58112,\\\"meaValueInch\\\":\\\"32(1/8)\\\",\\\"sizeIntro\\\":\\\"The horizontal girth of natural waistline between top of the hip bones (iliac crests) and the lower ribs.\\\",\\\"sizeName\\\":\\\"Waist girth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize01_032.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":83.33293,\\\"meaValueInch\\\":\\\"32(3/4)\\\",\\\"sizeIntro\\\":\\\"The horizontal girth of the line 1 inch higher than natural waistline\\\",\\\"sizeName\\\":\\\"Upper Waist girth EK\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize01_033.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":86.50346,\\\"meaValueInch\\\":\\\"34(1/8)\\\",\\\"sizeIntro\\\":\\\"The horizontal girth of the line 1 inch belower than natural waistline\\\",\\\"sizeName\\\":\\\"Lower Waist girth EK\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize01_040.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":106.55093,\\\"meaValueInch\\\":\\\"41(7/8)\\\",\\\"sizeIntro\\\":\\\"The maximum horizontal girth around the torsa taken at the greatest portrusion of the buttocks as viewed from the side.\\\",\\\"sizeName\\\":\\\"Hip girth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize03_010.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":46.723152,\\\"meaValueInch\\\":\\\"18(3/8)\\\",\\\"sizeIntro\\\":\\\"Horizontal length with tape-measure between two shoulder points (top of the shoulder joint).\\\",\\\"sizeName\\\":\\\"Across shoulder\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize04_020.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":40.29461,\\\"meaValueInch\\\":\\\"15(7/8)\\\",\\\"sizeIntro\\\":\\\"The girth of the neck at 2.5cm (1 inch) above the neck base.\\\",\\\"sizeName\\\":\\\"Neck girth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize07_010.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":30.228575,\\\"meaValueInch\\\":\\\"11(7/8)\\\",\\\"sizeIntro\\\":\\\"The girth of the upper arm under armpit.\\\",\\\"sizeName\\\":\\\"Upper arm girth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize08_010.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":68.16128,\\\"meaValueInch\\\":\\\"26(7/8)\\\",\\\"sizeIntro\\\":\\\"With arm bend at 90 degree and measure from shoulder point over elbow bone (olecranon) and end at wrist bone (distal end of ulna).\\\",\\\"sizeName\\\":\\\"Arm length\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize09_010.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":58.54308,\\\"meaValueInch\\\":\\\"23(1/8)\\\",\\\"sizeIntro\\\":\\\"The girth of the leg below crotch.\\\",\\\"sizeName\\\":\\\"Max. thigh girth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize09_030.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":42.81964,\\\"meaValueInch\\\":\\\"16(7/8)\\\",\\\"sizeIntro\\\":\\\"The girth of the leg at fullest part of the calf.\\\",\\\"sizeName\\\":\\\"Calf girth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize11_030.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":115.70234,\\\"meaValueInch\\\":\\\"45(1/2)\\\",\\\"sizeIntro\\\":\\\"The vertical distance with tape-measure from natural waist level to the floor at side.\\\",\\\"sizeName\\\":\\\"Side seam\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize11_050.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":64.36755,\\\"meaValueInch\\\":\\\"25(3/8)\\\",\\\"sizeIntro\\\":\\\"With tape-measure from undercrotch to ankle along the inleg.\\\",\\\"sizeName\\\":\\\"Inseam/Inleg\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize13_013.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":21.391153,\\\"meaValueInch\\\":\\\"8(3/8)\\\",\\\"sizeIntro\\\":\\\"The horizontal distance between front and back at waist level\\\",\\\"sizeName\\\":\\\"Waist depth\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize13_030.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":24.117065,\\\"meaValueInch\\\":\\\"9(1/2)\\\",\\\"sizeIntro\\\":\\\"The horizontal distance between the chest and back at chest level\\\",\\\"sizeName\\\":\\\"Chest width\\\"},{\\\"cmUnit\\\":\\\"cm\\\",\\\"imageUrl\\\":\\\"https://www.tozmart.com/bndsrv/asset/icon/icon190102/fsize99_001.png\\\",\\\"inchUnit\\\":\\\"\\\",\\\"meaValueCM\\\":61.750053,\\\"meaValueInch\\\":\\\"24(1/4)\\\",\\\"sizeIntro\\\":\\\"The veritical distance between the neck and hip\\\",\\\"sizeName\\\":\\\"cloth length\\\"}],\\\"serverStatusCode\\\"\",\"errorCode\":0,\"mode\":200}:1,\"userId\":\"oiExp5G3wuf0aY1Ecn9TFyH5fKh8\"}");
//                        sendMessage(gson.toJson(entity));
                    } else {
                        openWebSocket();
                    }
                    if (webSocketDevice != null && webSocketDevice.isOpen()) {
                        webSocketDevice.send(gson.toJson(entity));
                    } else {
                        openDeviceWebSocket();
                    }
                }, Throwable::printStackTrace);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraInterface.doDestroyCamera();
        if (showInputImage != null) {
            showInputImage.dispose();
        }
        if (showUndressing != null) {
            showUndressing.dispose();
        }
        if (pictureDisposable != null) {
            pictureDisposable.dispose();
        }
        if (checkBitmapDisposable != null) {
            checkBitmapDisposable.dispose();
        }
        if (errorDisposable != null) {
            errorDisposable.dispose();
        }
        if (successDisposable != null) {
            successDisposable.dispose();
        }
        if (heartBeatPag != null) {
            heartBeatPag.dispose();
        }
        if (messageDisposable != null) {
            messageDisposable.dispose();
        }
    }


    private WebSocket.StringCallback stringCallback = (str) -> {
        Log.e(TAG, "ThreadID=" + Thread.currentThread().getId());
        Log.e(TAG, str);
        RequestEntity entity = null;
        try {
            entity = gson.fromJson(str, RequestEntity.class);
        } catch (Exception e) {
            return;
        }
        DeviceEntity data = null;
        switch (entity.getMode()) {
            case 999://激光器
                if (entity.getErrorCode() != 0) {
                    showErrorImage(2, "激光器故障");
                }
                if (checkBitmapDisposable != null) {
                    checkBitmapDisposable.dispose();
                    checkBitmapDisposable = null;
                }
                if (current_picture_mode == MODE_POSITIVE) {
                    checkBitmapDisposable = Observable.interval(1, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.newThread())
                            .filter(l -> userBitmap.getPositive() != null && userBitmap.getPositive().getWidth() > 0)
                            .subscribe(l -> {
                                checkBitmapDisposable.dispose();
                                checkBitmapDisposable = null;
                                startPicture(MODE_SIDE);
                            }, throwable -> {
                                showErrorImage(1002, "正面照异常" + throwable.getMessage());
                                throwable.printStackTrace();
                            });
                } else {
                    checkBitmapDisposable = Observable.interval(1, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.newThread())
                            .filter(l -> userBitmap.getSide() != null && userBitmap.getSide().getWidth() > 0)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(l -> {
                                checkBitmapDisposable.dispose();
                                checkBitmapDisposable = null;
                                current_picture_mode = -1;
                                sendBitmap();
                                new Thread(() -> {
                                    saveBitmap(userBitmap.getPositive());
                                    saveBitmap(userBitmap.getSide());
                                }).start();
                                stopCamera();
                            }, throwable -> {
                                showErrorImage(1003, "侧面照异常" + throwable.getMessage());
                                throwable.printStackTrace();
                            });
                }
                break;
            case 1:
                showInputImage();
                break;
            case 2://服务器推送 开始拍照
                data = gson.fromJson(entity.getEntity(), DeviceEntity.class);
                if (isUse) {
                    sendError(CAMERA_USE, getString(R.string.camera_use));
                    return;
                }
                current_mode = entity.getMode();
                current_userID = data.getId();
                startCreatePic(data, false);
                break;
            case 3:
                data = gson.fromJson(entity.getEntity(), DeviceEntity.class);
                if (isUse) {
                    sendError(CAMERA_USE, getString(R.string.camera_use));
                    return;
                }
                current_mode = entity.getMode();
                current_userID = data.getId();
                startCreatePic(data, true);
                break;
        }
        current_picture_mode = entity.getMode();
    };

    /**
     * 填写资料  等待
     * 超时则返回主页面
     */
    private void showInputImage() {
        if (showInputImage != null) {
            showInputImage.dispose();
            showInputImage = null;
        }
        showInputImage = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> mImageView.setImageResource(R.mipmap.input_data))
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(l -> l > inputTime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> {
                    showInputImage.dispose();
                    showInputImage = null;
                    mImageView.setImageResource(R.mipmap.main);
                });
    }

    /**
     * 放置私人物品
     */
    private void showUndressingImage() {
        if (showUndressing != null) {
            showUndressing.dispose();
            showUndressing = null;
        }
        isUse = true;
        showUndressing = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> {
                    mImageView.setImageResource(R.mipmap.undressing);
                    mTextView.setVisibility(View.VISIBLE);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(l -> {
                    mTextView.setText("" + (undressingTime - l % undressingTime));
                    if (l > 0 && l % undressingTime == 0) {
                        mTextView.setText("");
                        mTextView.setVisibility(View.GONE);
                        return true;
                    }
                    return false;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> {
                    showUndressing.dispose();
                    showUndressing = null;
                    startPicture(MODE_POSITIVE);
                });
    }

    /**
     * 拍照
     *
     * @param mode
     */
    private synchronized void startPicture(int mode) {
        if (pictureDisposable != null) {
            pictureDisposable.dispose();
            pictureDisposable = null;
        }
        current_picture_mode = mode;
        pictureDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> {
                    showCamera();
                    if (current_picture_mode == MODE_POSITIVE) {
                        mImageView.setImageResource(R.mipmap.front_photo);
                    } else {
                        mImageView.setImageResource(R.mipmap.side_view);
                    }
                    mTextView.setVisibility(View.VISIBLE);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(l -> {
                    mTextView.setText("" + (takePhotoTime - l % takePhotoTime));
                    if (l > 0 && l % takePhotoTime == 0) {
                        mTextView.setText("");
                        mTextView.setVisibility(View.GONE);
                        return true;
                    }
                    return false;
                })
                .observeOn(Schedulers.newThread())
                .subscribe(l -> {
                    pictureDisposable.dispose();
                    pictureDisposable = null;
                    mCameraInterface.takePicture(callback);
                    RequestEntity entity = new RequestEntity();
                    entity.setMode(0);
                    webSocketDevice.send(gson.toJson(entity));
                }, Throwable::printStackTrace);
    }


    private synchronized void startCreatePic(DeviceEntity data, boolean isAgain) {
        if (TextUtils.isEmpty(data.getId())) {
            return;
        }
        new OneMeasureSDKLite.Builder()
                .withActivity(MainActivity.this)
                .setCorpId("f9kssg0cjtyay26c")
                .setUserId(data.getId())
                .setName(data.getId())
                .setGender(data.getGender())
                .setHeight(data.getHeight())
                .setWeight(data.getWeight())
                .setLanguage(Language.CHINESE)
                .setUnit(Unit.METRIC)
                .build();
        if (!isAgain) {//第一次
            showUndressingImage();
        } else {//不是第一次
            isUse = true;
            startPicture(MODE_POSITIVE);
        }

        if (isAgain) {
            startPicture(MODE_POSITIVE);
        } else {
            if (pictureDisposable != null) {
                pictureDisposable.dispose();
                pictureDisposable = null;
            }
            pictureDisposable = Observable.interval(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .doOnSubscribe(disposable -> mImageView.setImageResource(R.mipmap.undressing))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(l -> {
                        if (l > undressingTime) {
                            startPicture(MODE_POSITIVE);
                            pictureDisposable.dispose();
                            pictureDisposable = null;
                        }
                    });
        }
    }


    private void sendError(int i, String message) {
        RequestEntity entity = new RequestEntity();
        entity.setMode(current_mode);
        entity.setErrorCode(i);
        entity.setErrorMessage(message);
        sendMessage(gson.toJson(entity));
    }


    private DataCallback dataCallback = (emitter, byteBufferList) -> {
        Log.i(TAG, "DataCallback");
        for (ByteBuffer byteBuffer : byteBufferList.getAllArray()) {
            Log.i(TAG, new String(byteBuffer.array(), StandardCharsets.UTF_8));
        }
        byteBufferList.recycle();
    };

    private CameraInterface.TakePictureCallback callback = (bitmap, isVertical) -> {
        if (current_picture_mode == MODE_POSITIVE) {
            if (userBitmap.getPositive() != null && userBitmap.getPositive().getWidth() > 0) {
                userBitmap.getPositive().recycle();
                userBitmap.setPositive(null);
            }
            userBitmap.setPositive(bitmap);
        } else {
            if (userBitmap.getSide() != null && userBitmap.getSide().getWidth() > 0) {
                userBitmap.getSide().recycle();
                userBitmap.setSide(null);
            }
            userBitmap.setSide(bitmap);
        }
    };

    private void sendBitmap() {
        Observable.just(userBitmap)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> {
                    mImageView.setImageResource(R.mipmap.generating_data);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(bitmapEntity -> {
                    if (bitmapEntity.getPositive() != null && bitmapEntity.getSide() != null) {
                        return bitmapEntity.getPositive().getWidth() > 0 && bitmapEntity.getSide().getWidth() > 0;
                    }
                    showErrorImage(1001, "拍照失败");
                    return false;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmapEntity -> OneMeasureSDKLite.getInstance().getImagesProfile(
                        MainActivity.this, bitmapEntity.getPositive(),
                        bitmapEntity.getSide(), null, null, this::getPeopleData)
                        , throwable -> {
                            throwable.printStackTrace();
                            showErrorImage(1000, "程序异常");
                        });
    }

    private void showErrorImage(int error, String message) {
        if (errorDisposable != null) {
            errorDisposable.dispose();
            errorDisposable = null;
        }
        errorDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> {
                    mImageView.setImageResource(R.mipmap.error);
                    mTextView.setVisibility(View.VISIBLE);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(l -> {
                    mTextView.setText("" + (errorTime - l % errorTime));
                    if (l > 0 && l % errorTime == 0) {
                        mTextView.setText("");
                        mTextView.setVisibility(View.GONE);
                        return true;
                    }
                    return false;
                })
                .subscribe(l -> {
                    errorDisposable.dispose();
                    errorDisposable = null;
                    mImageView.setImageResource(R.mipmap.main);
                    isUse = false;
                });
        RequestEntity entity = new RequestEntity();
        entity.setMode(current_mode);
        entity.setUserId(current_userID);
        entity.setErrorCode(error);
        entity.setErrorMessage(message);
        sendMessage(gson.toJson(entity));
    }


    public void sendMessage(String string) {
        Log.e(TAG, "sendMessage=" + string);
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.send(string);
            retry = 0;
        } else {
            if (retry > 10) {
                messages.removeAll(messages);
                retry = 0;
                return;
            }
            retry += 1;
            messages.add(string);
        }
    }

    private static final int SDK_SUCCESS = 200;//服务器请求成功
    private static final int SDKCORPIDNOT_VALID = 20;//⽆效的企业ID
    private static final int SDKUSAGELIMIT = 21;//接⼝使⽤次数已⽤完
    private static final int SDKACCOUNTEXPIRED = 22;//企业账户已到期
    private static final int SDKIMAGEERROR = 100;//上传的照⽚不符合要求
    private static final int SDKUNKNOWNSERVER_ERROR = 50;//未知错误


    private void getPeopleData(Profile2ResultData profile2ResultData) {
        OneMeasureSDKLite.getInstance().getMeasurements(this, profile2ResultData,
                measurementsData -> Observable.just(measurementsData)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .filter(data -> {
                            if (data.getServerStatusCode() != SDK_SUCCESS) {
                                Toast toast = Toast.makeText(MainActivity.this, data.getServerStatusText(), Toast.LENGTH_SHORT);
                                synchronized (MainActivity.this) {
                                    if (mToast != null) {
                                        mToast.cancel();
                                    }
                                    toast.show();
                                    mToast = toast;
                                }
                                showErrorImage(data.getServerStatusCode(), data.getServerStatusText());
                                return false;
                            }
                            return true;
                        })
                        .subscribe(data -> {
                            RequestEntity entity = new RequestEntity();
                            entity.setMode(1);
                            entity.setUserId(current_userID);
                            entity.setErrorCode(0);
                            entity.setUserId(OneMeasureSDKLite.getInstance().getOneMeasureSDKInfo().getUserId());
                            entity.setEntity(gson.toJson(data));
                            String str = gson.toJson(entity);
                            Log.e(TAG, str);
                            sendMessage(str);
                            startSuccess();
                        }, Throwable::printStackTrace));
    }

    private synchronized void startSuccess() {
        successDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> {
                    mImageView.setImageResource(R.mipmap.success);
                    mTextView.setVisibility(View.VISIBLE);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(l -> {
                    mTextView.setText("" + (successTime - l % successTime));
                    if (l > 0 && l % successTime == 0) {
                        mTextView.setText("");
                        mTextView.setVisibility(View.GONE);
                        return true;
                    }
                    return false;
                })
                .subscribe(l -> {
                    mImageView.setImageResource(R.mipmap.main);
                    successDisposable.dispose();
                    successDisposable = null;
                    isUse = false;
                });
    }


    /**
     * 停止预览
     */
    private synchronized void stopCamera() {
        mCameraInterface.doStopPreview();
        mVideoView.setVisibility(View.GONE);
    }

    /**
     * 启动摄像机预览
     */
    private synchronized void showCamera() {
        if (mVideoView.getVisibility() == View.GONE) {
            mVideoView.setVisibility(View.VISIBLE);
        }
        mCameraInterface.doStartPreview(mVideoView.getHolder(), screenProp);
    }

    /**
     * 初始化摄像头
     */
    private void initVideoView() {
        mCameraInterface = CameraInterface.getInstance();
        mVideoView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCameraInterface.doOpenCamera(() -> {
                    float widthSize = mVideoView.getMeasuredWidth();
                    float heightSize = mVideoView.getMeasuredHeight();
                    if (screenProp == 0) {
                        screenProp = heightSize / widthSize;
                    }
                    mVideoView.setVisibility(View.GONE);
                });
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
//                mCameraInterface.doDestroyCamera();
            }
        });
    }

    /**
     * 后台通讯
     */
    private void openWebSocket() {
        AsyncHttpClient.getDefaultInstance().websocket(Config.WEBSOCKET_URL + Config.DEVICE_ID,
                null, (ex, webSocket) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    if (this.webSocket != null) {
                        this.webSocket.close();
                    }
                    this.webSocket = webSocket;
                    webSocket.setStringCallback(stringCallback);
                    webSocket.setDataCallback(dataCallback);
                });
    }


    /**
     * 硬件通讯
     */
    private void openDeviceWebSocket() {
        AsyncHttpClient.getDefaultInstance().websocket(Config.WEBSOCKET_DEVICE_URL, null, (ex, webSocket) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            this.webSocketDevice = webSocket;
            webSocket.setStringCallback(stringCallback);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

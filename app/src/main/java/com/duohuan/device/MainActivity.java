package com.duohuan.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.cjt2325.cameralibrary.CameraInterface;
import com.duohuan.device.activity.BaseActivity;
import com.duohuan.device.entity.DeviceEntity;
import com.duohuan.device.entity.RequestEntity;
import com.duohuan.device.entity.UserBitmap;
import com.duohuan.device.mode.Countdown;
import com.duohuan.device.mode.DeviceSocketMode;
import com.duohuan.device.mode.WebSocketMode;
import com.duohuan.device.util.Config;
import com.duohuan.device.util.HttpUtil;
import com.duohuan.device.util.UpdateImage;
import com.google.gson.Gson;
import com.tozmart.tozisdk.constant.Language;
import com.tozmart.tozisdk.constant.Unit;
import com.tozmart.tozisdk.entity.Profile2ResultData;
import com.tozmart.tozisdk.sdk.OneMeasureSDKLite;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressLint({"CheckResult", "SetTextI18n"})
public class MainActivity extends BaseActivity {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("my-lib");
    }

    private static final String TAG = "MainActivity";

    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.rl_camera)
    RelativeLayout rlCamera;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.tv_number)
    TextView tvNumber;


    private Gson gson = new Gson();
    private boolean isRunning = false;
    private RequestEntity currentEntity;


    private DeviceSocketMode.DeviceListener findFaceListener = new DeviceSocketMode.DeviceListener() {
        @Override
        public void onSuccess() {
            //TODO 启动人脸收集成功
        }

        @Override
        public void onError(String msg) {
            //TODO 启动人脸收集失败  设备未连接
        }
    };

    private final WebSocketMode.ServerListener serverListener = new WebSocketMode.ServerListener() {
        @Override
        public void onInputMsg() {
            synchronized (serverListener) {
                //TODO 此处缺少等待判断   需要添加等待用户  超时则返回  未超时则进入拍照流程
                if (isRunning) {
                    runningError(Config.INPUT_MESSAGE);
                    return;
                }
                //输入等待
                Countdown.getInstance().startCountdown(30, R.mipmap.input_data, () -> {
                    imageView.setImageResource(R.mipmap.main);
                });
            }
        }

        @Override
        public void onTakePhoto(RequestEntity entity, DeviceEntity data) {
            synchronized (MainActivity.this) {
                if (isRunning) {
                    runningError(Config.TAKE_PHOTO);
                } else {//拍照流程
                    isRunning = true;
                    currentEntity = entity;
                    takePhoto(data);
                }
            }
        }

        @Override
        public void onRetakePhoto(RequestEntity entity, DeviceEntity data) {
            synchronized (MainActivity.this) {
                if (isRunning) {
                    runningError(Config.RETAKE_PHOTO);
                } else {
                    isRunning = true;
                    currentEntity = entity;
                    retakePhoto(data);
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (checkPermission()) {
            init();
        }
    }


    @Override
    public void onDestroy() {
        WebSocketMode.getInstance().close();
        DeviceSocketMode.getInstance().close();
        mCameraInterface.doDestroyCamera();
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                init();
            }
        }
    }


    /**
     * 重新拍照流程
     */
    private void retakePhoto(DeviceEntity data) {
        //TODO 重新拍照流程
        new OneMeasureSDKLite.Builder()
                .withActivity(MainActivity.this)
                .setCorpId(Config.CORP_ID)
                .setUserId(data.getId())
                .setName(data.getId())
                .setGender(data.getGender())
                .setHeight(data.getHeight())
                .setWeight(data.getWeight())
                .setLanguage(Language.CHINESE)
                .setUnit(Unit.METRIC)
                .build();
        runOnUiThread(this::startCamera);
        UserBitmap userBitmap = new UserBitmap();
        takePic(userBitmap, false);
    }

    /**
     * 拍照流程
     */
    private void takePhoto(DeviceEntity data) {
        new OneMeasureSDKLite.Builder()
                .withActivity(MainActivity.this)
                .setCorpId(Config.CORP_ID)
                .setUserId(data.getId())
                .setName(data.getId())
                .setGender(data.getGender())
                .setHeight(data.getHeight())
                .setWeight(data.getWeight())
                .setLanguage(Language.CHINESE)
                .setUnit(Unit.METRIC)
                .build();
        runOnUiThread(this::startCamera);
        UserBitmap userBitmap = new UserBitmap();
        //更衣等待
        Countdown.getInstance().startCountdown(15, R.mipmap.undressing, () -> takePic(userBitmap, false));
    }

    /**
     * 拍照
     *
     * @param userBitmap
     * @param isSide     是否是侧面
     */
    private void takePic(UserBitmap userBitmap, boolean isSide) {
        //拍照等待
        Countdown.getInstance().startCountdown(5, isSide ? R.mipmap.side_view : R.mipmap.front_photo, () ->
                mCameraInterface.takePicture(((bitmap, isVertical) -> {
                    if (bitmap == null) {
                        showErrorImage(Config.TAKE_PIC_ERROR, Config.TAKE_PIC_ERROR_MSG);
                        mCameraInterface.doOpenCamera(null);
                        return;
                    }
                    if (isSide) {
                        runOnUiThread(this::stopCamera);
                        userBitmap.setSide(bitmap);
                        sendBitmap(userBitmap);
                    } else {
                        userBitmap.setPositive(bitmap);
                    }
                    DeviceSocketMode.getInstance().startLaser(new DeviceSocketMode.DeviceListener() {//启动激光
                        @Override
                        public void onSuccess() {
                            if (!isSide) {
                                takePic(userBitmap, true);
                                DeviceSocketMode.getInstance().startFindFace(currentEntity, findFaceListener);
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            showErrorImage(Config.GUIDE_ERROR, Config.GUIDE_ERROR_MSG);
                        }
                    });
                })));
    }

    /**
     * 发送图片
     *
     * @param userBitmap
     */
    private void sendBitmap(UserBitmap userBitmap) {
        Observable.just(userBitmap)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> imageView.setImageResource(R.mipmap.generating_data))
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(bitmapEntity -> {
                    if (bitmapEntity.getPositive() != null && bitmapEntity.getSide() != null) {
                        return bitmapEntity.getPositive().getWidth() > 0 && bitmapEntity.getSide().getWidth() > 0;
                    }
                    showErrorImage(Config.TAKE_PIC_ERROR, Config.TAKE_PIC_ERROR_MSG);
                    return false;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmapEntity -> {
                            //TODO 上传图片
                            uploadImage(bitmapEntity);
                            OneMeasureSDKLite.getInstance().getImagesProfile(
                                    MainActivity.this, bitmapEntity.getPositive(),
                                    bitmapEntity.getSide(), null, null, this::getPeopleData);
                        }
                        , throwable -> {
                            throwable.printStackTrace();
                            showErrorImage(Config.RUNNING_ERROR, throwable.getMessage());
                        });
    }

    private void uploadImage(UserBitmap bitmapEntity) {
        Observable<String> image1 = UpdateImage.getInstance().upimage(bitmapEntity.getPositive());
        Observable<String> image2 = UpdateImage.getInstance().upimage(bitmapEntity.getSide());
        Observable.zip(image1, image2, (imageStr1, imageStr2) -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("userId", currentEntity.getUserId());
            map.put("messageId", currentEntity.getMessageId());
            map.put("body_photo", imageStr1);
            map.put("side_photo", imageStr2);
            return HttpUtil.getInstance().getApi().upImage(map);
        })
                .subscribeOn(Schedulers.io())
                .subscribe(this::upDateUrl, Throwable::printStackTrace);
    }

    private void upDateUrl(Observable<ResponseBody> observable) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody-> Log.e(TAG, responseBody.string()),Throwable::printStackTrace);
    }

    private static final int SDK_SUCCESS = 200;//服务器请求成功

    /**
     * 发送剪辑后的图片进行预测三围
     *
     * @param profile2ResultData
     */
    private void getPeopleData(Profile2ResultData profile2ResultData) {
        OneMeasureSDKLite.getInstance().getMeasurements(this, profile2ResultData,
                measurementsData -> Observable.just(measurementsData)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .filter(data -> {
                            if (data.getServerStatusCode() != SDK_SUCCESS) {
                                showErrorImage(data.getServerStatusCode(), data.getServerStatusText());
                                return false;
                            }
                            return true;
                        })
                        .subscribe(data -> {
                            currentEntity.setErrorCode(Config.SUCCESS);
                            currentEntity.setEntity(gson.toJson(data));
                            WebSocketMode.getInstance().sendMessage(currentEntity);
                            Countdown.getInstance().startCountdown(5, R.mipmap.success, () -> {
                                imageView.setImageResource(R.mipmap.main);
                                isRunning = false;
                            });

                        }, Throwable::printStackTrace));
    }

    /**
     * 向服务器发送异常
     *
     * @param error
     * @param msg
     */
    private void showErrorImage(int error, String msg) {
        //异常等待
        Countdown.getInstance().startCountdown(5, R.mipmap.error, () -> {
            imageView.setImageResource(R.mipmap.main);
            isRunning = false;
        });
        if (currentEntity != null) {
            currentEntity.setErrorCode(error);
            currentEntity.setErrorMessage(msg);
            WebSocketMode.getInstance().sendMessage(currentEntity);
            currentEntity = null;
        }
    }


    /**
     * 停止预览
     */
    private synchronized void stopCamera() {
        mCameraInterface.doStopPreview();
        videoView.setVisibility(View.GONE);
    }

    /**
     * 开始预览
     */
    private synchronized void startCamera() {
        if (videoView.getVisibility() == View.GONE) {
            videoView.setVisibility(View.VISIBLE);
        }
        mCameraInterface.doStartPreview(videoView.getHolder(), screenProp);
    }

    /**
     * 机器正在使用返回异常
     */
    private void runningError(@Config.PhotoMode int mode) {
        RequestEntity entity = new RequestEntity();
        entity.setMode(Config.INPUT_MESSAGE);
        entity.setErrorCode(Config.DEVICE_IS_USE);
        entity.setErrorMessage(Config.DEVICE_IS_USE_MSG);
        WebSocketMode.getInstance().sendMessage(entity);
    }


    //-----------------start 初始化长连接  摄像头----------------------

    private float screenProp;
    private CameraInterface mCameraInterface;

    private void init() {
        DeviceSocketMode.getInstance();
        WebSocketMode.getInstance().setListener(serverListener);
        Countdown.getInstance().initView(imageView, tvNumber);
        mCameraInterface = CameraInterface.getInstance();
        initScreen();
    }


    /**
     * 初始化显示宽高
     */
    private void initScreen() {
        videoView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCameraInterface.doOpenCamera(() -> {
                    float widthSize = videoView.getMeasuredWidth();
                    float heightSize = videoView.getMeasuredHeight();
                    if (screenProp == 0) {
                        screenProp = heightSize / widthSize;
                    }
                    videoView.setVisibility(View.GONE);
                });
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

    }
    //-----------------end 初始化长连接  摄像头----------------------


    public native String stringFromJNI();
}

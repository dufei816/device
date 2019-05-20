package com.duohuan.device.entity;

import android.widget.ImageView;

import com.koushikdutta.async.http.WebSocket;
import com.tozmart.tozisdk.activity.RxAppCompatActivity;

/**
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　             ┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 * 创建人: 杜
 * 日期: 2019/5/16
 * 时间: 14:51
 */
public class MainModeEntity {

    private RxAppCompatActivity context;
    private ImageView imageView;
    private WebSocket webSocket;
    private int takePhotoTime;
    private int undressingTime;
    private int successTime;
    private int errorTime;
    private int inputTime;

    public MainModeEntity() {
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public RxAppCompatActivity getContext() {
        return context;
    }

    public void setContext(RxAppCompatActivity context) {
        this.context = context;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getTakePhotoTime() {
        return takePhotoTime;
    }

    public void setTakePhotoTime(int takePhotoTime) {
        this.takePhotoTime = takePhotoTime;
    }

    public int getUndressingTime() {
        return undressingTime;
    }

    public void setUndressingTime(int undressingTime) {
        this.undressingTime = undressingTime;
    }

    public int getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(int successTime) {
        this.successTime = successTime;
    }

    public int getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(int errorTime) {
        this.errorTime = errorTime;
    }

    public int getInputTime() {
        return inputTime;
    }

    public void setInputTime(int inputTime) {
        this.inputTime = inputTime;
    }
}

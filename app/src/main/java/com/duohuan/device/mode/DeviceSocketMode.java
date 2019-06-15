package com.duohuan.device.mode;

import android.annotation.SuppressLint;
import android.util.Log;

import com.duohuan.device.entity.DeviceEntity;
import com.duohuan.device.entity.RequestEntity;
import com.duohuan.device.util.Config;
import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class DeviceSocketMode {

    private static final String TAG = "DeviceSocketMode";
    public static final Object waitObject = new Object();
    private static DeviceSocketMode deviceWebSocket;
    private Disposable dis;
    private SendThread sendThread;
    private WebSocket webSocket;
    private LinkedList<String> messageList = new LinkedList<>();

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, DeviceListener> messagesListener = new HashMap<>();

    private Gson gson = new Gson();
    private int number = 0;

    private class SendThread extends Thread {

        @Override
        public void run() {
            while (true) {
                if (interrupted()) {
                    return;
                }
                if (messageList.size() > 0) {
                    if (webSocket != null && webSocket.isOpen()) {
                        String msg = messageList.remove();
                        Log.e(TAG, "WebSocket send->" + msg);
                        webSocket.send(msg);
                    } else {
                        openSocket();
                        try {
                            synchronized (waitObject) {
                                waitObject.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        synchronized (waitObject) {
                            waitObject.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public interface DeviceListener {

        void onSuccess();

        void onError(String msg);

    }


    private WebSocket.StringCallback stringCallback = msg -> {
        Log.e(TAG, msg);
        RequestEntity entity;
        try {
            entity = gson.fromJson(msg, RequestEntity.class);
        } catch (Exception e) {
            return;
        }
        if (entity.getNumber() == -1) return;
        DeviceListener listener = messagesListener.remove(entity.getNumber());
        if (listener == null) return;
        switch (entity.getMode()) {
            case Config.START_LASER:
                if (entity.getErrorCode() != Config.SUCCESS) {
                    listener.onError(entity.getErrorMessage());
                } else {
                    listener.onSuccess();
                }
                break;
        }
    };

    @SuppressLint("CheckResult")
    private DeviceSocketMode() {
        openSocket();
        sendThread = new SendThread();
        sendThread.start();
        dis = Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(l -> sendMessage(Config.HEARTBEAT, null));
    }

    /**
     * 启动激光
     */
    public void startLaser(DeviceListener listener) {
        sendMessage(Config.START_LASER, listener);
    }

    /**
     * 启动人脸收集
     */
    public void startFindFace(RequestEntity entity, DeviceListener listener) {
        entity.setMode(Config.START_FIND_FACE);
        putListener(listener, entity);
        sendMessage(entity);
    }

    private void sendMessage(int mode, DeviceListener listener) {
        RequestEntity entity = new RequestEntity();
        entity.setMode(mode);
        putListener(listener, entity);
        sendMessage(entity);
    }

    private void putListener(DeviceListener listener, RequestEntity entity) {
        if (listener != null) {
            number += 1;
            if (number < 0) {
                number = 1;
            }
            entity.setNumber(number);
            messagesListener.put(number + 1, listener);
        }
    }


    private void sendMessage(RequestEntity message) {
        String str = gson.toJson(message);
        messageList.add(str);
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
    }

    public void close() {
        if (sendThread != null) sendThread.interrupt();
        if (webSocket != null) webSocket.close();
        if (dis != null) dis.dispose();
        dis = null;
        sendThread = null;
        webSocket = null;
        deviceWebSocket = null;
    }

    private void openSocket() {
        AsyncHttpClient.getDefaultInstance().websocket(Config.WEBSOCKET_DEVICE_URL,
                Config.PROTOCOL, (ex, webSocket) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    if (this.webSocket != null) {
                        this.webSocket.close();
                    }
                    this.webSocket = webSocket;
                    webSocket.setStringCallback(stringCallback);
                    if (sendThread != null) sendThread.notifyAll();
                });
    }

    public static DeviceSocketMode getInstance() {
        synchronized (DeviceSocketMode.class) {
            if (deviceWebSocket == null) {
                deviceWebSocket = new DeviceSocketMode();
            }
        }
        return deviceWebSocket;
    }
}

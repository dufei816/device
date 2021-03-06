package com.duohuan.device.mode;

import android.annotation.SuppressLint;
import android.util.Log;

import com.duohuan.device.entity.DeviceEntity;
import com.duohuan.device.entity.RequestEntity;
import com.duohuan.device.util.Config;
import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class WebSocketMode {

    private static final String TAG = "WebSocketMode";
    public static final Object waitObject = new Object();
    private static WebSocketMode webSocketMode;
    private final Disposable heartbeatDis;
    private SendThread sendThread;
    private WebSocket webSocket;
    private LinkedList<String> messageList = new LinkedList<>();
    private Gson gson = new Gson();
    private ServerListener listener;


    public void setListener(ServerListener listener) {
        this.listener = listener;
    }

    private WebSocket.StringCallback stringCallback = msg -> {
        if (listener == null) return;
        Log.e(TAG, msg);
        RequestEntity entity;
        DeviceEntity data;
        try {
            entity = gson.fromJson(msg, RequestEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        switch (entity.getMode()) {
            case Config.INPUT_MESSAGE:
                listener.onInputMsg();
                break;
            case Config.TAKE_PHOTO:
                data =  gson.fromJson(entity.getEntity(), DeviceEntity.class);
                listener.onTakePhoto(entity, data);
                break;
            case Config.RETAKE_PHOTO:
                data =  gson.fromJson(entity.getEntity(), DeviceEntity.class);
                listener.onRetakePhoto(entity, data);
                break;
            case Config.HEARTBEAT://心跳发送
                break;
            case Config.HEARTBEAT_REPLY://心跳回复  服务器
                break;
        }
    };


    public interface ServerListener {
        /**
         * 信息输入
         */
        void onInputMsg();

        /**
         * 拍照
         */
        void onTakePhoto(RequestEntity entity, DeviceEntity data);

        /**
         * 重新拍照
         */
        void onRetakePhoto(RequestEntity entity, DeviceEntity data);
    }

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

    @SuppressLint("CheckResult")
    private WebSocketMode() {
        openSocket();
        sendThread = new SendThread();
        sendThread.start();
        heartbeatDis = Observable.interval(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(l -> {
                    RequestEntity entity = new RequestEntity();
                    entity.setMode(Config.HEARTBEAT);
                    sendMessage(entity);
                });
    }

    public void sendMessage(RequestEntity message) {
        String str = gson.toJson(message);
        messageList.add(str);
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
    }

    public void close() {
        if (sendThread != null) sendThread.interrupt();
        if (webSocket != null) webSocket.close();
        if(heartbeatDis!=null) heartbeatDis.dispose();
        sendThread = null;
        webSocket = null;
        webSocketMode = null;
    }

    private void openSocket() {
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
                    if (sendThread != null) sendThread.notifyAll();
                });
    }

    public static WebSocketMode getInstance() {
        synchronized (WebSocketMode.class) {
            if (webSocketMode == null) {
                webSocketMode = new WebSocketMode();
            }
        }
        return webSocketMode;
    }
}

package com.duohuan.device.util;

import android.support.annotation.IntDef;

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
 * 日期: 2019/5/11
 * 时间: 17:52
 */
public class Config {
    public static final long OUT_TIME = 10;
    public static final String BASE_URL = "";


    //------------start Things 100~200--------------
    //启动激光
    public static final int START_LASER = 101;
    //动作结束
//    public static final int END_LASER = 102;


    //启动导轨
    public static final int START_GUIDE = 103;
    //停止导轨
    public static final int END_GUIDE = 104;
    //导轨返回原地
    public static final int RETURN_ZERO = 105;

    public static final int START_FIND_FACE = 106;

    //------------start 异常编号----------------
    public static final int SUCCESS = 0;
    public static final String SUCCESS_MSG = "完成";

    //激光正在运行
    public static final int LASER_RUNNING_ERROR = 1;
    public static final String LASER_RUNNING_ERROR_MSG = "激光正在运行";
    public static final int LASER_ERROR = 2;

    //发送消息异常
    public static final int SEND_ERROR = 3;
    //------------end 异常编号----------------


    //------------end   Things 100~200--------------


    //-----------start 心跳 ----------
    public static final int HEARTBEAT_REPLY = -2;
    public static final int HEARTBEAT = -1;
    //-----------end  心跳 -----------

    //------------start 拍照 1~100 ---------

    //信息输入
    public static final int INPUT_MESSAGE = 1;
    //拍照
    public static final int TAKE_PHOTO = 2;
    //重新拍照
    public static final int RETAKE_PHOTO = 3;


    @IntDef({INPUT_MESSAGE, TAKE_PHOTO, RETAKE_PHOTO})
    public @interface PhotoMode {
    }

    //------------start 异常----------------
    //机器正在使用
    public static final int DEVICE_IS_USE = 1;
    public static final String DEVICE_IS_USE_MSG = "机器正在使用！";

    //相机异常
    public static final int TAKE_PIC_ERROR = 2;
    public static final String TAKE_PIC_ERROR_MSG = "相机异常";

    public static final int GUIDE_ERROR = 4;
    public static final String GUIDE_ERROR_MSG = "激光头异常";


    //拍照是发生异常
    public static final int RUNNING_ERROR = 3;

    //------------end 异常----------------

    //------------end   拍照 1~100 ---------

    public static final String PROTOCOL = "TakePic";
    public static final String WEBSOCKET_URL = "wss://gateway.duohuan.net/websocket/";
    public static final String WEBSOCKET_DEVICE_URL = "http://192.168.1.128:5000/ws";
    public static final String CORP_ID = "f9kssg0cjtyay26c";

    public static final String DEVICE_ID = "1";


}

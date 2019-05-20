package com.cjt2325.cameralibrary.listener;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/6/5
 * 描    述：
 * =====================================
 */
public interface ErrorListener {
    /**
     * @param code 1没有摄像头权限 2打开摄像头失败 3启动录像失败
     */
    void onError(int code);
}

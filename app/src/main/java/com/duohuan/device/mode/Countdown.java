package com.duohuan.device.mode;

import android.annotation.SuppressLint;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duohuan.device.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint({"CheckResult", "SetTextI18n"})
public class Countdown {

    private static final Object obj = new Object();
    private static Countdown countdown;
    private Disposable timeDisposable;
    private ImageView imageView;
    private TextView textView;

    public interface TimeOverListener {
        void onTimeOver();
    }


    public void startCountdown(int time, @DrawableRes int imageId, TimeOverListener listener) {
        if (imageView == null) throw new RuntimeException("Countdown init()!");
        if (timeDisposable != null) {
            timeDisposable.dispose();
            timeDisposable = null;
        }
        timeDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> {
                    imageView.setImageResource(imageId);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("" + time);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(l -> {
                    long overTime = l % time;
                    textView.setText("" + (time - overTime));
                    if (l > 0 && overTime == 0) {
                        textView.setText("");
                        textView.setVisibility(View.GONE);
                        return true;
                    }
                    return false;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> {
                    timeDisposable.dispose();
                    timeDisposable = null;
                    if (listener != null) {
                        listener.onTimeOver();
                    }
                });
    }


    public void initView(@NonNull ImageView imageView, @NonNull TextView textView) {
        this.imageView = imageView;
        this.textView = textView;
    }

    public static Countdown getInstance() {
        if (countdown == null) {
            synchronized (obj) {
                if (countdown == null) {
                    countdown = new Countdown();
                }
            }
        }
        return countdown;
    }

}

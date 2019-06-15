package com.duohuan.device.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.duohuan.device.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ErrorActivity extends BaseActivity {


    @BindView(R.id.tv_text)
    TextView tvText;
    private Disposable errorDis;
    private static final int TIME_OUT = 10;

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);

        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config == null) {
            //This should never happen - Just finish the activity to avoid a recursive crash.
            finish();
            return;
        }

        errorDis = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(l -> {
                    tvText.setText((TIME_OUT - l % TIME_OUT) + "秒后重启");
                    if (l > 0 && l % TIME_OUT == 0) {
                        tvText.setText("");
                        return true;
                    }
                    return false;
                })
                .subscribe(l -> {
                    errorDis.dispose();
                    CustomActivityOnCrash.restartApplication(this, config);
                }, Throwable::printStackTrace);

    }
}

package com.duohuan.device.util;

import android.annotation.SuppressLint;
import android.util.Log;


import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public class HttpUtil {

    private static Object obj = new Object();
    private static HttpUtil myHttpUtil;
    private static final String TAG = "HttpUtil";
    private API api;


    public static HttpUtil getInstance() {
        if (myHttpUtil == null) {
            synchronized (obj) {
                if (myHttpUtil == null) {
                    myHttpUtil = new HttpUtil();
                }
            }
        }
        return myHttpUtil;
    }

    public API getApi() {
        return api;
    }

    private HttpUtil() {
        init();
    }

    public boolean isConnetction() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec("ping -c 3 www.baidu.com");
        int ret = p.waitFor();
        if (ret == 0) {
            return true;
        }
        return false;
    }

    @SuppressLint({"CheckResult", "SimpleDateFormat"})
    private void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(Config.OUT_TIME, TimeUnit.SECONDS);//连接 超时时间
        builder.writeTimeout(Config.OUT_TIME, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(Config.OUT_TIME, TimeUnit.SECONDS);//读操作 超时时间
        builder.retryOnConnectionFailure(true);//错误重连

        Interceptor tor = chain -> {
            Request request = chain.request();
            Log.e(TAG, request.url().toString());
            return chain.proceed(request);
        };

        builder.addInterceptor(tor);
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Config.BASE_URL)
                .build();
        api = retrofit.create(API.class);
        Observable.just("http://www.baidu.com")
                .subscribeOn(Schedulers.io())
                .flatMap(url -> Observable.just(new URL(url)))
                .flatMap(url -> {
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect(); //发出连接
                    long ld = uc.getDate(); //取得网站日期时间
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(ld);
                    final String format = formatter.format(calendar.getTime());
                    return Observable.just(format);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> Log.e(TAG, time));
    }

    public interface API {

        @POST("http://192.168.133.159:8085/info/saveImage")
        Observable<ResponseBody> upImage(@QueryMap Map<String, String> map);
    }

}
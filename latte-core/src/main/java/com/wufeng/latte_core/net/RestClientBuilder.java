package com.wufeng.latte_core.net;

import android.content.Context;

import java.io.File;
import java.util.WeakHashMap;

import app.dinus.com.loadingdrawable.LoadingView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RestClientBuilder {
    private String mUrl = null;
    private final WeakHashMap<String, Object> PARAMS = new WeakHashMap<>();
    private RequestBody mBody = null;
    private Context mContext = null;
    private File mFile = null;
    private ISuccess mSUCCESS = null;
    private IError mERROR = null;

    RestClientBuilder(){
    }

    public final RestClientBuilder url(String url){
        this.mUrl = url;
        return this;
    }

    public final RestClientBuilder params(WeakHashMap<String, Object> params){
        PARAMS.putAll(params);
        return this;
    }

    public final RestClientBuilder params(String key, Object value){
        PARAMS.put(key, value);
        return this;
    }

    public final RestClientBuilder file(File file){
        this.mFile = file;
        return this;
    }

    public final RestClientBuilder raw(String raw){
        this.mBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), raw);
        return this;
    }

    public final RestClientBuilder success(ISuccess iSuccess){
        this.mSUCCESS = iSuccess;
        return this;
    }

    public final RestClientBuilder error(IError iError){
        this.mERROR = iError;
        return this;
    }

    public final RestClientBuilder loading(Context context){
        mContext = context;
        return this;
    }

    public final RestClient build(){
        return new RestClient(mUrl, PARAMS, mBody, mFile, mContext, mSUCCESS, mERROR);
    }
}

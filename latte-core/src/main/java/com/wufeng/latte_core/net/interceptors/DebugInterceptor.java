package com.wufeng.latte_core.net.interceptors;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.wufeng.latte_core.util.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Yangya on 2018/7/5
 */
public class DebugInterceptor extends BaseInterceptor {

    private String DEBUG_URL;
    private int DEBUG_RAW_ID;

    public DebugInterceptor(String debugUrl, int debugRawID) {
        this.DEBUG_URL = debugUrl;
        this.DEBUG_RAW_ID = debugRawID;
    }

    public DebugInterceptor() {
    }

    private Response getResponse(Chain chain, String json) {
        return new Response.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body(ResponseBody.create(MediaType.parse("application/json"), json))
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .build();
    }

    private Response debugResponse(Chain chain, @RawRes int rawId) {
        //final String json = FileUtil.getRawFile(rawId);
        //return getResponse(chain, json);
        return null;
    }

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        final String url = chain.request().url().toString();
        LogUtil.d("DebugInterceptor", url+"       "+chain.request().method());
        if ("debug".equals(DEBUG_URL)) {
            LogUtil.d("DebugInterceptor", getRequestString(chain));
        }
        return chain.proceed(chain.request());
    }
}

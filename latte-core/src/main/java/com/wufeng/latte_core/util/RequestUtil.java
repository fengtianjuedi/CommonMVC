package com.wufeng.latte_core.util;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

public class RequestUtil {
    public static void setMerchantAndTerminal(String merchantCode, String terminalCode, Context context){
        String params = "data={'merchantId':'" + merchantCode + "','terminalId':'" + terminalCode + "'}";
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/setTerminal")
                .xwwwformurlencoded(params)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {

                    }
                })
                .loading(context)
                .build()
                .post();
    }

    public static void checkIn(String merchantCode, String terminalCode, Context context){
        String params = "data={'merchantId':'" + merchantCode + "','terminalId':'" + terminalCode + "'}";
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/checkIn")
                .xwwwformurlencoded(params)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {

                    }
                })
                .loading(context)
                .build()
                .post();
    }
}

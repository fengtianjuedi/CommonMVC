package com.wufeng.latte_core.util;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

public class RequestUtil {
    public static void setMerchantAndTerminal(String merchantCode, String terminalCode, final Context context){
        String params = "data={'merchantId':'" + merchantCode + "','terminalId':'" + terminalCode + "'}";
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/setTerminal")
                .raw(params)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        //if (jsonObject.getString("resultCode"))
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(context, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
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

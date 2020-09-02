package com.wufeng.latte_core.util;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

public class RequestUtil {

    //设置终端
    public static void setMerchantAndTerminal(String merchantCode, String terminalCode, final Context context){
        String params = "data={'merchantId':'" + merchantCode + "','terminalId':'" + terminalCode + "'}";
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/setTerminal")
                .raw(params)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){

                        }else{
                            Toast.makeText(context, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
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

    //签到
    public static void checkIn(String merchantCode, String terminalCode, final Context context){
        String params = "data={'merchantId':'" + merchantCode + "','terminalId':'" + terminalCode + "'}";
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/checkIn")
                .raw(params)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            Toast.makeText(context, "签到成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
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
}

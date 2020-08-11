package com.wufeng.latte_core.util;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.net.RestClientBuilder;

public class RequestUtil {
    public static void setMerchantAndTerminal(String merchantCode, String terminalCode){
        JSONObject params = new JSONObject();
        params.put("merchantId", merchantCode);
        params.put("terminalId", terminalCode);
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/setTerminal")
                .raw(params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {

                    }
                })
                .build()
                .post();
    }
}

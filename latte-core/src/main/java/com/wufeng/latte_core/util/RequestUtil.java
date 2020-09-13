package com.wufeng.latte_core.util;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.callback.ICallbackTwoParams;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.entity.CategoryRecordInfo;
import com.wufeng.latte_core.entity.TradeRecordInfo;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

import java.util.HashMap;
import java.util.Map;

public class RequestUtil {
    //根据卡号查询商户
    public static void queryMerchantByCardNo(final Context context, final String cardNo, final ICallback<MerchantCard> callback) {
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/getCustomerCard")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))) {
                            MerchantCard merchantCard = new MerchantCard();
                            merchantCard.setCardNo(cardNo);
                            merchantCard.setCardName(jsonObject.getJSONObject("merchant").getString("cname"));
                            merchantCard.setMerchantCode(jsonObject.getJSONObject("merchant").getString("merchantcode"));
                            merchantCard.setAccountCode(jsonObject.getJSONObject("account").getString("account"));
                            merchantCard.setIsCollectionAccount(false);
                            if (callback != null)
                                callback.callback(merchantCard);
                        } else {
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

    //批发交易请求
    public static void wholesaleTrade(final Context context, TradeRecordInfo tradeRecordInfo, final ICallback<Boolean> callback){
        JSONObject params = new JSONObject();
        params.put("terminalOrderCode", IdGenerate.getInstance().getId());
        params.put("inMerchantCardId", tradeRecordInfo.getSellerCardNo());
        params.put("inMerchantCardAccount", tradeRecordInfo.getSellerAccount());
        params.put("inMerchantCode", tradeRecordInfo.getSellerCode());
        params.put("inMerchantName", tradeRecordInfo.getSellerName());
        params.put("outMerchantCardId", tradeRecordInfo.getBuyerCardNo());
        params.put("outMerchantCardAccount", tradeRecordInfo.getBuyerAccount());
        params.put("outMerchantCode", tradeRecordInfo.getBuyerCode());
        params.put("outMerchantName", tradeRecordInfo.getBuyerName());
        params.put("pwdString", tradeRecordInfo.getBuyerPassword());
        params.put("payType", tradeRecordInfo.getPayType());
        params.put("originalTotalAmount", tradeRecordInfo.getReceivableAmount());
        params.put("actualTransactionAmount", tradeRecordInfo.getActualAmount());
        TerminalInfo terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        params.put("terminalMerchantCode", terminalInfo.getMerchantCode());
        params.put("terminalId", terminalInfo.getTerminalCode());
        params.put("transTime", TimeUtil.currentDateYMDHMS());
        params.put("signString", "");
        JSONArray goodsList = new JSONArray();
        for (int i = 0; i < tradeRecordInfo.getCategoryRecordInfoList().size(); i++){
            CategoryRecordInfo info = tradeRecordInfo.getCategoryRecordInfoList().get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsid", info.getGoodsId());
            jsonObject.put("goodsname", info.getGoodsName());
            jsonObject.put("price", info.getGoodsPrice());
            jsonObject.put("goodsnum", info.getGoodsNumber());
            jsonObject.put("goodsmoney", info.getGoodsAmount());
            goodsList.add(jsonObject);
        }
        params.put("commodityList", goodsList);
        RestClient.builder()
                .url("/pgcore-pos/PosTrade/posTrade")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))) {
                            if (callback != null)
                                callback.callback(true);
                        } else {
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

    //检查更新
    public static void checkUpdate(final Context context, final ICallbackTwoParams<Boolean, Map<String, Object>> callback){
        JSONObject params = new JSONObject();
        params.put("type", "pos");
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/AppUpgrade")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            int newVersion = jsonObject.getJSONArray("data").getJSONObject(0).getInteger("newestCode");
                            int currentVersion = VersionUtil.getVersionCode(context);
                            if (newVersion > currentVersion){
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("downloadUrl", jsonObject.getJSONArray("data").getJSONObject(0).getString("directUrl"));
                                updateMap.put("isForceUpgrade", true);
                                updateMap.put("title", jsonObject.getJSONArray("data").getJSONObject(0).getString("title"));
                                updateMap.put("content", jsonObject.getJSONArray("data").getJSONObject(0).getString("content"));
                                if (callback != null)
                                    callback.callback(true, updateMap);
                            }else {
                                if (callback != null)
                                    callback.callback(false, null);
                            }
                        }else{
                            if (callback != null)
                                callback.callback(false, null);
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        if (callback != null){
                            callback.callback(false, null);
                        }
                        Toast.makeText(context, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(context)
                .build()
                .post();
    }

    //签到
    public static void signIn(final Context context, final ICallback<Boolean> callback){
        TerminalInfo terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        String time = TimeUtil.currentDateYMDHMS();
        String signString = ThreeDesUtil.encode3Des(terminalInfo.getMasterKey(), terminalInfo.getTerminalCode() + terminalInfo.getMerchantCode() + time);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId", terminalInfo.getMerchantCode());
        jsonObject.put("terminalId", terminalInfo.getTerminalCode());
        jsonObject.put("threeDESTime", time);
        jsonObject.put("signString", signString);
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/checkIn")
                .xwwwformurlencoded("data=" + jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            if (callback != null)
                                callback.callback(true);
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

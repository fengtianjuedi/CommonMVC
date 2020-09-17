package com.wufeng.latte_core.util;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.callback.ICallbackTwoParams;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.entity.CategoryNode;
import com.wufeng.latte_core.entity.CategoryRecordInfo;
import com.wufeng.latte_core.entity.TradeRecordInfo;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static void wholesaleTrade(final Context context, final TradeRecordInfo tradeRecordInfo, final ICallback<Boolean> callback){
        TerminalInfo terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        String encryptPassword = ThreeDesUtil.encode3Des(terminalInfo.getMasterKey(), tradeRecordInfo.getBuyerPassword());
        String time = TimeUtil.currentDateYMDHMS();
        String signString = ThreeDesUtil.encode3Des(terminalInfo.getMasterKey(), terminalInfo.getTerminalCode() + terminalInfo.getMerchantCode() + time);
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
        params.put("pwdString", encryptPassword);
        params.put("payType", tradeRecordInfo.getPayType());
        params.put("originalTotalAmount", tradeRecordInfo.getReceivableAmount());
        params.put("actualTransactionAmount", tradeRecordInfo.getActualAmount());
        params.put("terminalMerchantCode", terminalInfo.getMerchantCode());
        params.put("terminalId", terminalInfo.getTerminalCode());
        params.put("transTime", time);
        params.put("signString", signString);
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
                            JSONObject data = jsonObject.getJSONObject("data");
                            tradeRecordInfo.setTradeOrderCode(data.getString("transnoCenter"));
                            tradeRecordInfo.setTradeTime(data.getString("transdateCenterStr"));
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
                        if (callback != null)
                            callback.callback(false);
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

    //余额查询
    public static void queryCardBalance(final Context context, String cardNo, String password, final ICallback<String> callback){
        TerminalInfo terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        String encryptPassword = ThreeDesUtil.encode3Des(terminalInfo.getMasterKey(), password);
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        params.put("password", encryptPassword);
        RestClient.builder()
                .url("/pgcore-pos/PosTrade/withdrawQuery")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            String balance = jsonObject.getJSONObject("data").getString("accountBalance");
                            if (callback != null)
                                callback.callback(balance);
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

    //交易状态查询确认
    public static void tradeStatusConfirm(final Context context, String terminalOrderCode, final ICallbackTwoParams<Integer, TradeRecordInfo> callback){
        JSONObject params = new JSONObject();
        params.put("terminalOrderCode", terminalOrderCode);
        RestClient.builder()
                .url("/pgcore-pos/PosTrade/transactionConfirm")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            TradeRecordInfo tradeRecordInfo = new TradeRecordInfo();
                            JSONObject data = jsonObject.getJSONObject("data");
                            tradeRecordInfo.setTradeOrderCode(data.getString("transnoCenter"));
                            tradeRecordInfo.setTradeTime(data.getString("transdateCenterStr"));
                            if (callback != null)
                                callback.callback(0, tradeRecordInfo);
                        }else{
                            if (callback != null)
                                callback.callback(1, null);
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        if (callback != null)
                            callback.callback(2, null);
                    }
                })
                .loading(context)
                .build()
                .post();
    }

    //查询交易记录
    public static void queryTradeRecord(final Context context, String startDate, String endDate, final ICallback<List<TradeRecordInfo>> callback){
        TerminalInfo terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        JSONObject params = new JSONObject();
        params.put("merchantCode", terminalInfo.getMerchantCode());
        params.put("transNoPos", terminalInfo.getTerminalCode());
        params.put("startTime", startDate);
        params.put("endTime", endDate);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/queryInformation")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<TradeRecordInfo> list = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++){
                                TradeRecordInfo tradeRecordInfo = new TradeRecordInfo();
                                tradeRecordInfo.setTradeOrderCode(data.getJSONObject(i).getString("transnoCenter"));
                                tradeRecordInfo.setSellerCardNo(data.getJSONObject(i).getString("cardCode11"));
                                tradeRecordInfo.setSellerName(data.getJSONObject(i).getString("rname1"));
                                tradeRecordInfo.setBuyerCardNo(data.getJSONObject(i).getString("cardCode12"));
                                tradeRecordInfo.setBuyerName(data.getJSONObject(i).getString("rname2"));
                                tradeRecordInfo.setReceivableAmount(data.getJSONObject(i).getString("transamt"));
                                tradeRecordInfo.setActualAmount(data.getJSONObject(i).getString("transamtall"));
                                tradeRecordInfo.setTradeTime(data.getJSONObject(i).getString("transdateCenterStr"));
                                tradeRecordInfo.setPayType(data.getJSONObject(i).getIntValue("payType"));
                                JSONArray goods = data.getJSONObject(i).getJSONArray("transMxList");
                                List<CategoryRecordInfo> categoryRecordInfoList = new ArrayList<>();
                                for (int n = 0; n < goods.size(); n++){
                                    CategoryRecordInfo categoryRecordInfo = new CategoryRecordInfo();
                                    categoryRecordInfo.setGoodsId(goods.getJSONObject(n).getString("goodsid"));
                                    categoryRecordInfo.setGoodsName(goods.getJSONObject(n).getString("goodsname"));
                                    categoryRecordInfo.setGoodsPrice(goods.getJSONObject(n).getString("price"));
                                    categoryRecordInfo.setGoodsNumber(goods.getJSONObject(n).getString("goodsnum"));
                                    categoryRecordInfo.setGoodsAmount(goods.getJSONObject(n).getString("goodsmoney"));
                                    categoryRecordInfoList.add(categoryRecordInfo);
                                }
                                tradeRecordInfo.getCategoryRecordInfoList().addAll(categoryRecordInfoList);
                                list.add(tradeRecordInfo);
                            }
                            if (callback != null)
                                callback.callback(list);
                        }else
                            Toast.makeText(context, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
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

    //查询商户绑定品种
    public static void queryCategoryByCardNo(final Context context, String cardNo, final ICallback<List<CategoryInfo>> callback){
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/queryBinDing")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        List<CategoryInfo> list = new ArrayList<>();
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.size(); i++){
                                JSONObject item = data.getJSONObject(i);
                                CategoryInfo categoryInfo = new CategoryInfo();
                                categoryInfo.setId(item.getString("id"));
                                categoryInfo.setName(item.getString("goodsname"));
                                list.add(categoryInfo);
                            }
                        }else{
                            Toast.makeText(context, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                        if (callback != null)
                            callback.callback(list);
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

    //品种绑定
    public static void bindCategory(final Context context, String cardNo, String goodsId, final ICallback<Boolean> callback){
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        params.put("goodsid", goodsId);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/binDing")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            if (callback != null){
                                callback.callback(true);
                            }
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

    //模糊查询，根据品种名称进行模糊查询
    public static void queryCategoryByName(final Context context, String name, final ICallback<List<CategoryNode>> callback){
        TerminalInfo terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        JSONObject params = new JSONObject();
        params.put("goodsId", "");
        params.put("merchantId", terminalInfo.getMerchantCode());
        params.put("terminalId", terminalInfo.getTerminalCode());
        params.put("firstFight", name);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/operationManagement")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<CategoryNode> list= new ArrayList<>();
                            for (int i = 0; i < data.size(); i++){
                                CategoryNode node = new CategoryNode();
                                node.setNodeId("0" + i);
                                node.setLevel(0);
                                node.setId(data.getJSONObject(i).getString("id"));
                                node.setName(data.getJSONObject(i).getString("goodsname"));
                                node.setEndNode("0".equals(data.getJSONObject(i).getString("lower")));
                                list.add(node);
                            }
                            if (callback != null)
                                callback.callback(list);
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

    //根据品种Id查询子品种 传空查询所有一级品类
    public static void queryCategoryById(final Context context, String id, final ICallback<List<CategoryNode>> callback){
        JSONObject params = new JSONObject();
        params.put("goodsId", id);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/operationManagement")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            List<CategoryNode> list= new ArrayList<>();
                            for (int i = 0; i < jsonArray.size(); i++){
                                CategoryNode node = new CategoryNode();
                                node.setNodeId("0" + i);
                                node.setLevel(0);
                                node.setId(jsonArray.getJSONObject(i).getString("id"));
                                node.setName(jsonArray.getJSONObject(i).getString("goodsname"));
                                node.setEndNode("0".equals(jsonArray.getJSONObject(i).getString("lower")));
                                list.add(node);
                            }
                            if (callback != null)
                                callback.callback(list);
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

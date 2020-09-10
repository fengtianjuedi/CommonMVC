package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.databinding.ActivityHomeBinding;
import com.wufeng.commonmvc.dialog.TipOneDialog;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.ThreeDesUtil;
import com.wufeng.latte_core.util.TimeUtil;
import com.wufeng.latte_core.util.UpdateUtil;
import com.wufeng.latte_core.util.VersionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    private UpdateUtil updateUtil;
    private Map<String, Object> updateMap;
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        updateUtil = new UpdateUtil(this, this);
    }

    //region 初始化
    private void initClickEvent(){
        mBinding.llWholesaleTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wholesaleTrade();
            }
        });
        mBinding.llCategoryManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryManager();
            }
        });
        mBinding.llBindCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindCard();
            }
        });
        mBinding.llTradeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tradeRecord();
            }
        });
        mBinding.llSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mBinding.llSetTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTerminal();
            }
        });
        mBinding.llTerminalParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminalParams();
            }
        });
    }

    //批发交易
    private void wholesaleTrade(){
        checkUpdate(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    updateUtil.checkUpdate(updateMap);
                }else{
                    if (checkTerminalInfo() && checkCollectionAccount()){
                        Intent intent = new Intent(HomeActivity.this, WholesaleTradeActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    //品种管理
    private void categoryManager(){
        checkUpdate(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    updateUtil.checkUpdate(updateMap);
                }else{
                    if (checkTerminalInfo()  && checkCollectionAccount()){
                        Intent intent = new Intent(HomeActivity.this, CategoryManagerActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    //绑定商户卡
    private void bindCard(){
        checkUpdate(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    updateUtil.checkUpdate(updateMap);
                }else{
                    if (checkTerminalInfo()){
                        Intent intent = new Intent(HomeActivity.this, BindCardActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    //交易记录
    private void tradeRecord(){
        checkUpdate(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    updateUtil.checkUpdate(updateMap);
                }else{
                    if (checkTerminalInfo() && checkCollectionAccount()){
                        Intent intent = new Intent(HomeActivity.this, TradeRecordActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    //签到
    private void signIn(){
        checkUpdate(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    updateUtil.checkUpdate(updateMap);
                }else{
                    if (!checkTerminalInfo()) return;
                    signInRequest();
                }
            }
        });

    }

    //设置终端
    private void setTerminal(){
        checkUpdate(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    updateUtil.checkUpdate(updateMap);
                }else{
                    Intent intent = new Intent(HomeActivity.this, SetTerminalActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //参数设置
    private void terminalParams(){
        checkUpdate(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    updateUtil.checkUpdate(updateMap);
                }else{
                    Intent intent = new Intent(HomeActivity.this, TerminalParamsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    //endregion

    //检查商户终端是否设置
    private boolean checkTerminalInfo(){
        TerminalInfo info = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        if (info == null){
            TipOneDialog dialog = new TipOneDialog("提示", "终端信息未设置，请先进入终端设置中进行设置");
            dialog.show(getSupportFragmentManager(), "home");
            return false;
        }
        return true;
    }

    //检查收款账户是否设置
    private boolean checkCollectionAccount(){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        if (merchantCard == null){
            TipOneDialog dialog = new TipOneDialog("提示", "收款账户未设置，请先进入绑卡中进行设置");
            dialog.show(getSupportFragmentManager(), "home");
            return false;
        }
        return true;
    }

    //region 网络请求
    //签到请求
    private void signInRequest(){
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
                            TipOneDialog tipOneDialog = new TipOneDialog("提示", "签到成功");
                            tipOneDialog.show(getSupportFragmentManager(), null);
                        }else{
                            Toast.makeText(HomeActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(HomeActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(HomeActivity.this)
                .build()
                .post();
    }

    //检查更新
    private void checkUpdate(final ICallback<Boolean> callback){
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
                            int currentVersion = VersionUtil.getVersionCode(getBaseContext());
                            if (newVersion > currentVersion){
                                updateMap = new HashMap<>();
                                updateMap.put("downloadUrl", jsonObject.getJSONArray("data").getJSONObject(0).getString("directUrl"));
                                updateMap.put("isForceUpgrade", true);
                                updateMap.put("title", jsonObject.getJSONArray("data").getJSONObject(0).getString("title"));
                                updateMap.put("content", jsonObject.getJSONArray("data").getJSONObject(0).getString("content"));
                                if (callback != null)
                                    callback.callback(true);
                            }else {
                                if (callback != null)
                                    callback.callback(false);
                            }
                        }else{
                            if (callback != null)
                                callback.callback(false);
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        if (callback != null){
                            callback.callback(false);
                        }
                        Toast.makeText(HomeActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(HomeActivity.this)
                .build()
                .post();
    }
    //endregion
}

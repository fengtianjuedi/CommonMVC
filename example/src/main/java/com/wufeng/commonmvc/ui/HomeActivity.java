package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.databinding.ActivityHomeBinding;
import com.wufeng.commonmvc.dialog.TipOneDialog;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.callback.ICallbackTwoParams;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.RequestUtil;
import com.wufeng.latte_core.util.ThreeDesUtil;
import com.wufeng.latte_core.util.TimeUtil;
import com.wufeng.latte_core.util.UpdateUtil;
import com.wufeng.latte_core.util.VersionUtil;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    private UpdateUtil updateUtil;
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
        RequestUtil.checkUpdate(HomeActivity.this, new ICallbackTwoParams<Boolean, Map<String, Object>>() {
            @Override
            public void callback(Boolean aBoolean, Map<String, Object> stringObjectMap) {
                if (aBoolean){
                    updateUtil.checkUpdate(stringObjectMap);
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
        RequestUtil.checkUpdate(HomeActivity.this, new ICallbackTwoParams<Boolean, Map<String, Object>>() {
            @Override
            public void callback(Boolean aBoolean, Map<String, Object> stringObjectMap) {
                if (aBoolean){
                    updateUtil.checkUpdate(stringObjectMap);
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
        RequestUtil.checkUpdate(HomeActivity.this, new ICallbackTwoParams<Boolean, Map<String, Object>>() {
            @Override
            public void callback(Boolean aBoolean, Map<String, Object> stringObjectMap) {
                if (aBoolean){
                    updateUtil.checkUpdate(stringObjectMap);
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
        RequestUtil.checkUpdate(HomeActivity.this, new ICallbackTwoParams<Boolean, Map<String, Object>>() {
            @Override
            public void callback(Boolean aBoolean, Map<String, Object> stringObjectMap) {
                if (aBoolean){
                    updateUtil.checkUpdate(stringObjectMap);
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
        RequestUtil.checkUpdate(HomeActivity.this, new ICallbackTwoParams<Boolean, Map<String, Object>>() {
            @Override
            public void callback(Boolean aBoolean, Map<String, Object> stringObjectMap) {
                if (aBoolean){
                    updateUtil.checkUpdate(stringObjectMap);
                }else{
                    if (!checkTerminalInfo()) return;
                    RequestUtil.signIn(HomeActivity.this, new ICallback<Boolean>() {
                        @Override
                        public void callback(Boolean aBoolean) {
                            if (aBoolean){
                                TipOneDialog tipOneDialog = new TipOneDialog("提示", "签到成功");
                                tipOneDialog.show(getSupportFragmentManager(), null);
                            }
                        }
                    });
                }
            }
        });
    }

    //设置终端
    private void setTerminal(){
        RequestUtil.checkUpdate(HomeActivity.this, new ICallbackTwoParams<Boolean, Map<String, Object>>() {
            @Override
            public void callback(Boolean aBoolean, Map<String, Object> stringObjectMap) {
                if (aBoolean){
                    updateUtil.checkUpdate(stringObjectMap);
                }else{
                    Intent intent = new Intent(HomeActivity.this, SetTerminalActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //参数设置
    private void terminalParams(){
        RequestUtil.checkUpdate(HomeActivity.this, new ICallbackTwoParams<Boolean, Map<String, Object>>() {
            @Override
            public void callback(Boolean aBoolean, Map<String, Object> stringObjectMap) {
                if (aBoolean){
                    updateUtil.checkUpdate(stringObjectMap);
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
}

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
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.ThreeDesUtil;
import com.wufeng.latte_core.util.TimeUtil;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
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
    }

    //批发交易
    private void wholesaleTrade(){
        if (checkTerminalInfo()){
            Intent intent = new Intent(HomeActivity.this, WholesaleTradeActivity.class);
            startActivity(intent);
        }
    }

    //品种管理
    private void categoryManager(){
        if (checkTerminalInfo()){
            Intent intent = new Intent(HomeActivity.this, CategoryManagerActivity.class);
            startActivity(intent);
        }
    }

    //绑定商户卡
    private void bindCard(){
        if (checkTerminalInfo()){
            Intent intent = new Intent(HomeActivity.this, BindCardActivity.class);
            startActivity(intent);
        }
    }

    //交易记录
    private void tradeRecord(){
        if (checkTerminalInfo()){
            Intent intent = new Intent(HomeActivity.this, TradeRecordActivity.class);
            startActivity(intent);
        }
    }

    //签到
    private void signIn(){
        if (!checkTerminalInfo())
            return;
        signInRequest();
    }

    //设置终端
    private void setTerminal(){
        Intent intent = new Intent(HomeActivity.this, SetTerminalActivity.class);
        startActivity(intent);
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
                            Toast.makeText(HomeActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
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
}

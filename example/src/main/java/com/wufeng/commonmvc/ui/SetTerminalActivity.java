package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.databinding.ActivitySetTerminalBinding;
import com.wufeng.commonmvc.dialog.TipOneDialog;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.RequestUtil;

public class SetTerminalActivity extends BaseActivity<ActivitySetTerminalBinding> {
    private TerminalInfo terminalInfo;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        initTerminalInfo();
    }

    //region 初始化
    //初始化点击事件
    private void initClickEvent(){
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTerminal();
            }
        });
    }

    //初始化终端信息
    private void initTerminalInfo(){
        terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        if (terminalInfo != null){
            mBinding.etMerchantCode.setText(terminalInfo.getMerchantCode());
            mBinding.etTerminalCode.setText(terminalInfo.getTerminalCode());
        }
    }
    //endregion

    //设置终端
    private void setTerminal(){
        String merchantCode = mBinding.etMerchantCode.getText().toString();
        String terminalCode = mBinding.etTerminalCode.getText().toString();
        if (TextUtils.isEmpty(merchantCode) || TextUtils.isEmpty(terminalCode)){
            Toast.makeText(SetTerminalActivity.this, "商户号终端号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestUtil.setTerminal(SetTerminalActivity.this, merchantCode, terminalCode, new ICallback<TerminalInfo>() {
            @Override
            public void callback(TerminalInfo info) {
                saveTerminalInfo(info);
            }
        });
    }

    //保存终端信息
    private void saveTerminalInfo(TerminalInfo info){
        if(TerminalInfoManager.getInstance().deleteTerminalInfo()){
            if(TerminalInfoManager.getInstance().insert(info)){
                TipOneDialog tipOneDialog = new TipOneDialog("提示", "终端设置成功");
                tipOneDialog.setOnOkClickListener(new TipOneDialog.OnOkClickListener() {
                    @Override
                    public void onOkClick() {
                        finish();
                    }
                });
                tipOneDialog.show(getSupportFragmentManager(), "setTerminal");
                return;
            }
        }
        TipOneDialog tipOneDialog = new TipOneDialog("提示", "终端设置失败");
        tipOneDialog.show(getSupportFragmentManager(), "setTerminal");
    }
}

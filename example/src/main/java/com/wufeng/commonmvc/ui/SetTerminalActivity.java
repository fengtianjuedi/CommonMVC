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
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

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
        final String merchantCode = mBinding.etMerchantCode.getText().toString();
        final String terminalCode = mBinding.etTerminalCode.getText().toString();
        if (TextUtils.isEmpty(merchantCode) || TextUtils.isEmpty(terminalCode)){
            Toast.makeText(SetTerminalActivity.this, "商户号终端号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId", merchantCode);
        jsonObject.put("terminalId", terminalCode);
        //String params = "data={'merchantId':'" + merchantCode + "','terminalId':'" + terminalCode + "'}";
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/setTerminal")
                .xwwwformurlencoded("data=" + jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                                TerminalInfo info = new TerminalInfo();
                                info.setMerchantCode(merchantCode);
                                info.setTerminalCode(terminalCode);
                                info.setMasterKey(jsonObject.getString("masterkey"));
                                saveTerminalInfo(info);
                        }else{
                            Toast.makeText(SetTerminalActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(SetTerminalActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(SetTerminalActivity.this)
                .build()
                .post();

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

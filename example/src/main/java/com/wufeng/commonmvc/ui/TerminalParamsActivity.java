package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.vanstone.trans.api.SystemApi;
import com.wufeng.commonmvc.databinding.ActivityTerminalParamsBinding;
import com.wufeng.commonmvc.dialog.TipOneDialog;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.device.PosDevice;
import com.wufeng.latte_core.util.StringUtil;

import java.math.BigDecimal;

public class TerminalParamsActivity extends BaseActivity<ActivityTerminalParamsBinding> {
    private String deviceModel;//当前设备型号

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        initDeviceModel();
        initPrintNumber();
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
        mBinding.cbDeviceLiandiA8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.cbDeviceAisinoA90.setChecked(!isChecked);
                deviceModel = PosDevice.LIANDIA8;
            }
        });
        mBinding.cbDeviceAisinoA90.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.cbDeviceLiandiA8.setChecked(!isChecked);
                deviceModel = PosDevice.AISINOA90;
            }
        });
        mBinding.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParams();
            }
        });
    }

    //初始化设备型号
    private void initDeviceModel(){
        deviceModel = ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL);
        if (PosDevice.AISINOA90.equals(deviceModel)){
            mBinding.cbDeviceAisinoA90.setChecked(true);
        }else if (PosDevice.LIANDIA8.equals(deviceModel)){
            mBinding.cbDeviceLiandiA8.setChecked(true);
        }
    }

    //初始化打印数量
    private void initPrintNumber(){
        int number= ConfigManager.getInstance().getConfig(ConfigKeys.PRINTNUMBER);
        mBinding.fetPrintNumber.setText(String.valueOf(number));
    }
    //endregion

    //region
    //保存配置参数
    private void saveParams(){
        try{
            String number = mBinding.fetPrintNumber.getText().toString();
            if (!TextUtils.isEmpty(number))
                ConfigManager.getInstance().withPrintNumber(Integer.parseInt(number));
            if (!deviceModel.equals(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL))){
                PosDevice.Exit();
                ConfigManager.getInstance().withPosModel(deviceModel);
                PosDevice.Init((Context)ConfigManager.getInstance().getConfig(ConfigKeys.CONTEXT));
            }
            TipOneDialog dialog = new TipOneDialog("提示", "保存成功");
            dialog.setOnOkClickListener(new TipOneDialog.OnOkClickListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        }catch (Exception ex){
            TipOneDialog dialog = new TipOneDialog("提示", "保存参数失败，" + ex.getMessage());
            dialog.show(getSupportFragmentManager(), null);
        }
    }
    //endrgion
}

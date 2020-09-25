package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.databinding.ActivityAddMerchantCardBinding;
import com.wufeng.latte_core.entity.CardInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.device.card.ReadCard;
import com.wufeng.latte_core.device.card.ReadCardFactory;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.RequestUtil;

public class AddMerchantCardActivity extends BaseActivity<ActivityAddMerchantCardBinding> {

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.etMerchantCardNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadCard readCard = ReadCardFactory.getReadCard(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL).toString(), AddMerchantCardActivity.this);
                if (readCard != null)
                    readCard.stop();
                readCard();
            }
        });
        mBinding.buttonBinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingMerchantCard();
            }
        });
        readCard();
    }

    //读卡
    private void readCard(){
        String model = ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL);
        ReadCard readCard = ReadCardFactory.getReadCard(model, AddMerchantCardActivity.this);
        if (readCard == null){
            Toast.makeText(AddMerchantCardActivity.this, "获取读卡设备失败", Toast.LENGTH_SHORT).show();
            return;
        }
        readCard.read(new ReadCard.ReadCardCallback() {
            @Override
            public void result(boolean success, String cardNo) {
                if (success){
                    mBinding.etMerchantCardNo.setText(cardNo);
                }
                else
                    Toast.makeText(AddMerchantCardActivity.this, "读卡失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //绑定商户卡
    private void bindingMerchantCard(){
        Editable cardNo = mBinding.etMerchantCardNo.getText();
        if (TextUtils.isEmpty(cardNo)){
            Toast.makeText(this, "卡号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (MerchantCardManager.getInstance().queryByCardNo(cardNo.toString()) != null){
            Toast.makeText(this, "该卡已绑定", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestUtil.queryMerchantByCardNo(AddMerchantCardActivity.this, cardNo.toString(), new ICallback<MerchantCard>() {
            @Override
            public void callback(MerchantCard merchantCard) {
                if(!MerchantCardManager.getInstance().insert(merchantCard)){
                    Toast.makeText(AddMerchantCardActivity.this, "卡片绑定失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(AddMerchantCardActivity.this, "卡片绑定成功", Toast.LENGTH_SHORT).show();
                mBinding.etMerchantCardNo.setText("");
            }
        });
    }
}

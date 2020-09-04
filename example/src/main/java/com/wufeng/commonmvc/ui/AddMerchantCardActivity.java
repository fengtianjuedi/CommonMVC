package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.databinding.ActivityAddMerchantCardBinding;
import com.wufeng.commonmvc.entity.CardInfo;
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

public class AddMerchantCardActivity extends BaseActivity<ActivityAddMerchantCardBinding> {
    private CardInfo cardInfo;

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
                readCard();
            }
        });
        mBinding.buttonBinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingMerchantCard();
            }
        });
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
        queryMerchantByCardNo(cardNo.toString(), new ICallback<MerchantCard>() {
            @Override
            public void callback(MerchantCard merchantCard) {
                if(!MerchantCardManager.getInstance().insert(merchantCard)){
                    Toast.makeText(AddMerchantCardActivity.this, "卡片绑定失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(AddMerchantCardActivity.this, "卡片绑定成功", Toast.LENGTH_SHORT).show();
                mBinding.etMerchantCardNo.setText("");
                cardInfo = null;
            }
        });
    }

    //根据卡号查询商户
    private void queryMerchantByCardNo(final String cardNo, final ICallback<MerchantCard> callback){
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        RestClient.builder()
                .url("/pgcore-pos/PosTerminal/getCustomerCard")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            MerchantCard merchantCard = new MerchantCard();
                            merchantCard.setCardNo(cardNo);
                            merchantCard.setCardName(cardNo);
                            merchantCard.setIsCollectionAccount(false);
                            if (callback != null)
                                callback.callback(merchantCard);
                        }else{
                            Toast.makeText(AddMerchantCardActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(AddMerchantCardActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(AddMerchantCardActivity.this)
                .build()
                .post();
    }
}

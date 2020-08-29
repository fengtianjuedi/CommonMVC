package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.wufeng.commonmvc.databinding.ActivityAddMerchantCardBinding;
import com.wufeng.commonmvc.entity.CardInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;

public class AddMerchantCardActivity extends BaseActivity<ActivityAddMerchantCardBinding> {

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.buttonBinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingMerchantCard();
            }
        });
    }

    //绑定商户卡
    private void bindingMerchantCard(){
        Editable cardNo = mBinding.fetMerchantCardNo.getText();
        if (TextUtils.isEmpty(cardNo)){
            Toast.makeText(this, "卡号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (MerchantCardManager.getInstance().queryByCardNo(cardNo.toString()) != null){
            Toast.makeText(this, "该卡已绑定", Toast.LENGTH_SHORT).show();
            return;
        }
        MerchantCard merchantCard = new MerchantCard();
        merchantCard.setCardNo(cardNo.toString());
        merchantCard.setCardName(cardNo.toString().substring(cardNo.length() - 4));
        merchantCard.setIsCollectionAccount(false);
        if(!MerchantCardManager.getInstance().insert(merchantCard)){
            Toast.makeText(this, "卡片绑定失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "卡片绑定成功", Toast.LENGTH_SHORT).show();
        mBinding.fetMerchantCardNo.setText("");
    }
}

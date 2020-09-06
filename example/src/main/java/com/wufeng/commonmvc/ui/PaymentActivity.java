package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.wufeng.commonmvc.databinding.ActivityPaymentBinding;
import com.wufeng.commonmvc.dialog.PayCardDialog;
import com.wufeng.commonmvc.entity.TradeRecordInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.database.TerminalInfoManager;

public class PaymentActivity extends BaseActivity<ActivityPaymentBinding> {
    private TradeRecordInfo tradeRecordInfo;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        tradeRecordInfo = getIntent().getParcelableExtra("tradeRecord");
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
        mBinding.cbPayCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.cbPayCash.setChecked(!isChecked);
                if (isChecked)
                    payCard();
            }
        });
        mBinding.cbPayCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.cbPayCard.setChecked(!isChecked);
            }
        });
    }

    //endregion

    //region 功能函数
    //一卡通支付
    private void payCard(){
        PayCardDialog dialog = new PayCardDialog();
        dialog.show(getSupportFragmentManager(), "payment");
    }
    //endregion
}
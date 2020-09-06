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

import java.math.BigDecimal;

public class PaymentActivity extends BaseActivity<ActivityPaymentBinding> {
    private TradeRecordInfo tradeRecordInfo;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        tradeRecordInfo = getIntent().getParcelableExtra("tradeRecord");
        String payText = tradeRecordInfo.getCategoryRecordInfoList().size() + "件商品，应收" + tradeRecordInfo.getReceivableAmount() + "元";
        mBinding.tvTradeStatistics.setText(payText);
        mBinding.fetActualAmount.setText(tradeRecordInfo.getActualAmount());
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
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //一卡通支付
                mBinding.cbPayCash.setChecked(!isChecked);
                if (isChecked)
                    payCard();
            }
        });
        mBinding.cbPayCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //现金支付
                mBinding.cbPayCard.setChecked(!isChecked);
            }
        });
        mBinding.tvIgnoreDecimals.setOnClickListener(new View.OnClickListener() { //抹零
            @Override
            public void onClick(View v) {
                BigDecimal actualAmount = new BigDecimal(tradeRecordInfo.getActualAmount());
                tradeRecordInfo.setActualAmount(String.valueOf(actualAmount.intValue()));
                mBinding.fetActualAmount.setText(tradeRecordInfo.getActualAmount());
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
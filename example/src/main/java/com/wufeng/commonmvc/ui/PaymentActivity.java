package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.databinding.ActivityPaymentBinding;
import com.wufeng.commonmvc.dialog.PayCardDialog;
import com.wufeng.latte_core.entity.CategoryRecordInfo;
import com.wufeng.latte_core.entity.TradeRecordInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.RequestUtil;
import com.wufeng.latte_core.util.TimeUtil;

import java.math.BigDecimal;

public class PaymentActivity extends BaseActivity<ActivityPaymentBinding> {
    private TradeRecordInfo tradeRecordInfo;
    private int payType; //支付方式

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        payType = -1;
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
                payType = 0;
                if (isChecked)
                    payCard();
            }
        });
        mBinding.cbPayCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //现金支付
                mBinding.cbPayCard.setChecked(!isChecked);
                payType = 1;
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
        mBinding.tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();
            }
        });
    }

    //endregion

    //region 功能函数
    //一卡通支付
    private void payCard(){
        PayCardDialog dialog = new PayCardDialog(PaymentActivity.this);
        dialog.setOnClickListener(new PayCardDialog.OnClickListener() {
            @Override
            public void onOkClick(final String cardNo, final String password) {
                RequestUtil.queryMerchantByCardNo(PaymentActivity.this, cardNo, new ICallback<MerchantCard>() {
                    @Override
                    public void callback(MerchantCard merchantCard) {
                        tradeRecordInfo.setBuyerCardNo(merchantCard.getCardNo());
                        tradeRecordInfo.setBuyerName(merchantCard.getCardName());
                        tradeRecordInfo.setBuyerCode(merchantCard.getMerchantCode());
                        tradeRecordInfo.setBuyerAccount(merchantCard.getAccountCode());
                        tradeRecordInfo.setBuyerPassword(password);
                    }
                });
            }

            @Override
            public void onCancelClick() { }
        });
        dialog.show(getSupportFragmentManager(), "payment");
    }

    //确认收款
    private void pay(){
        if (payType == -1){
            Toast.makeText(PaymentActivity.this, "请先选择支付方式", Toast.LENGTH_SHORT).show();
            return;
        }
        tradeRecordInfo.setPayType(payType);
        if (payType == 0 && TextUtils.isEmpty(tradeRecordInfo.getBuyerAccount())){
            payCard();
            return;
        }
        tradeRecordInfo.setActualAmount(mBinding.fetActualAmount.getText().toString());
        //提交交易请求
        RequestUtil.wholesaleTrade(PaymentActivity.this, tradeRecordInfo, new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {

            }
        });
    }
    //endregion
}
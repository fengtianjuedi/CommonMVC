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
                        tradeRecordInfo.setBuyerName(merchantCard.getCardName());
                        tradeRecordInfo.setBuyerCode(merchantCard.getMerchantCode());
                        tradeRecordInfo.setBuyerAccount(cardNo);
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
        wholesaleTradeRequest(new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {

            }
        });
    }
    //endregion

    //region 网络请求

    //批发交易请求请求
    private void wholesaleTradeRequest(final ICallback<Boolean> callback){
        JSONObject params = new JSONObject();
        params.put("inMerchantCardAccount", tradeRecordInfo.getSellerAccount());
        params.put("inMerchantCode", tradeRecordInfo.getSellerCode());
        params.put("inMerchantName", tradeRecordInfo.getSellerName());
        params.put("outMerchantCardAccount", tradeRecordInfo.getBuyerAccount());
        params.put("outMerchantCode", tradeRecordInfo.getBuyerCode());
        params.put("outMerchantName", tradeRecordInfo.getBuyerName());
        params.put("pwdString", tradeRecordInfo.getBuyerPassword());
        params.put("payType", tradeRecordInfo.getPayType());
        params.put("originalTotalAmount", tradeRecordInfo.getReceivableAmount());
        params.put("actualTransactionAmount", tradeRecordInfo.getActualAmount());
        TerminalInfo terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        params.put("merchantCode", terminalInfo.getMerchantCode());
        params.put("terminalId", terminalInfo.getTerminalCode());
        params.put("transTime", TimeUtil.currentDateYMDHMS());
        params.put("signString", "");
        JSONArray goodsList = new JSONArray();
        for (int i = 0; i < tradeRecordInfo.getCategoryRecordInfoList().size(); i++){
            CategoryRecordInfo info = tradeRecordInfo.getCategoryRecordInfoList().get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsid", info.getGoodsId());
            jsonObject.put("goodsname", info.getGoodsName());
            jsonObject.put("goodsprice", info.getGoodsPrice());
            jsonObject.put("goodsnum", info.getGoodsNumber());
            jsonObject.put("goodsmoney", info.getGoodsAmount());
            goodsList.add(jsonObject);
        }
        params.put("commodityList", goodsList);
        RestClient.builder()
                .url("/pgcore-pos/PosTrade/posTrade")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))) {
                            if (callback != null)
                                callback.callback(true);
                        } else {
                            Toast.makeText(PaymentActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(PaymentActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(PaymentActivity.this)
                .build()
                .post();
    }
    //endregion
}
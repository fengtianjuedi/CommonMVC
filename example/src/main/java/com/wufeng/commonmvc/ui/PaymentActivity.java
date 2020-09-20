package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.databinding.ActivityPaymentBinding;
import com.wufeng.commonmvc.dialog.PayCardDialog;
import com.wufeng.commonmvc.dialog.TipTwoDialog;
import com.wufeng.latte_core.callback.ICallbackTwoParams;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.database.MerchantTrade;
import com.wufeng.latte_core.database.MerchantTradeGoods;
import com.wufeng.latte_core.database.MerchantTradeManager;
import com.wufeng.latte_core.device.card.ReadCard;
import com.wufeng.latte_core.device.card.ReadCardFactory;
import com.wufeng.latte_core.device.print.PrintTemplate;
import com.wufeng.latte_core.device.print.Printer;
import com.wufeng.latte_core.device.print.PrinterFactory;
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
import com.wufeng.latte_core.util.MediaPlayerUtil;
import com.wufeng.latte_core.util.RequestUtil;
import com.wufeng.latte_core.util.TimeUtil;

import java.math.BigDecimal;

public class PaymentActivity extends BaseActivity<ActivityPaymentBinding> {
    public static final int REQUESTCODE = 3;
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
                if (isChecked){
                    payType = 0;
                    mBinding.fetCardNo.setVisibility(View.VISIBLE);
                    mBinding.fetPassword.setVisibility(View.VISIBLE);
                    MediaPlayerUtil.pleaseBrushCard(PaymentActivity.this);
                    payCard();
                }
            }
        });
        mBinding.cbPayCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //现金支付
                mBinding.cbPayCard.setChecked(!isChecked);
                if (isChecked){
                    payType = 1;
                    //清空卡支付信息
                    tradeRecordInfo.setBuyerCardNo("");
                    tradeRecordInfo.setBuyerName("");
                    tradeRecordInfo.setBuyerCode("");
                    tradeRecordInfo.setBuyerAccount("");
                    mBinding.fetCardNo.setText("");
                    mBinding.fetPassword.setText("");
                    mBinding.fetCardNo.setVisibility(View.INVISIBLE);
                    mBinding.fetPassword.setVisibility(View.INVISIBLE);
                    ReadCardFactory.getReadCard(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL).toString(), PaymentActivity.this).stop();
                }
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
        ReadCard readCard = ReadCardFactory.getReadCard(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL).toString(), PaymentActivity.this);
        readCard.read(new ReadCard.ReadCardCallback() {
            @Override
            public void result(boolean success, String cardNo) {
                if (success){
                    mBinding.fetCardNo.setText(cardNo);
                    RequestUtil.queryMerchantByCardNo(PaymentActivity.this, cardNo, new ICallback<MerchantCard>() {
                        @Override
                        public void callback(MerchantCard merchantCard) {
                            tradeRecordInfo.setBuyerCardNo(merchantCard.getCardNo());
                            tradeRecordInfo.setBuyerName(merchantCard.getCardName());
                            tradeRecordInfo.setBuyerCode(merchantCard.getMerchantCode());
                            tradeRecordInfo.setBuyerAccount(merchantCard.getAccountCode());
                            mBinding.fetCardNo.setText(merchantCard.getCardNo() + "  " + merchantCard.getCardName());
                            mBinding.fetPassword.requestFocus();
                            //请输入密码
                        }
                    });
                }

            }
        });
    }

    //确认收款
    private void pay(){
        if (payType == -1){
            Toast.makeText(PaymentActivity.this, "请先选择支付方式", Toast.LENGTH_SHORT).show();
            return;
        }
        tradeRecordInfo.setPayType(payType);
        tradeRecordInfo.setBuyerPassword(mBinding.fetPassword.getText().toString());
        if (payType == 0 && TextUtils.isEmpty(tradeRecordInfo.getBuyerAccount())){
            Toast.makeText(PaymentActivity.this, "请买家刷卡", Toast.LENGTH_SHORT).show();
            return;
        }
        if (payType == 0 && TextUtils.isEmpty(tradeRecordInfo.getBuyerPassword())){
            Toast.makeText(PaymentActivity.this, "请买家输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        tradeRecordInfo.setActualAmount(mBinding.fetActualAmount.getText().toString());
        //提交交易请求
        RequestUtil.wholesaleTrade(PaymentActivity.this, tradeRecordInfo, new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    Toast.makeText(PaymentActivity.this, "交易成功", Toast.LENGTH_SHORT).show();
                    Printer printer = PrinterFactory.getPrinter(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL).toString(), PaymentActivity.this);
                    if (printer != null){
                        PrintTemplate printTemplate = new PrintTemplate(printer);
                        printTemplate.tradeTemplate(tradeRecordInfo, new PrintTemplate.PrintResultCallback() {
                            @Override
                            public void result(int code, String message) {
                                if (code != 0)
                                    Toast.makeText(PaymentActivity.this, message, Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                }else{ //未获取到交易状态，将交易数据记录到数据库中，等待查询确认
                    final MerchantTrade merchantTrade = new MerchantTrade();
                    merchantTrade.setTerminalOrderCode(tradeRecordInfo.getTerminalOrderCode());
                    merchantTrade.setSellerAccount(tradeRecordInfo.getSellerAccount());
                    merchantTrade.setSellerCode(tradeRecordInfo.getSellerCode());
                    merchantTrade.setSellerName(tradeRecordInfo.getSellerName());
                    merchantTrade.setBuyerAccount(tradeRecordInfo.getBuyerAccount());
                    merchantTrade.setBuyerCode(tradeRecordInfo.getBuyerCode());
                    merchantTrade.setBuyerName(tradeRecordInfo.getBuyerName());
                    merchantTrade.setReceivableAmount(tradeRecordInfo.getReceivableAmount());
                    merchantTrade.setActualAmount(tradeRecordInfo.getActualAmount());
                    merchantTrade.setTradeTime(tradeRecordInfo.getTradeTime());
                    merchantTrade.setPayType(tradeRecordInfo.getPayType());
                    merchantTrade.setTradeStatus(2); //状态未知
                    MerchantTradeManager.getInstance().insert(merchantTrade);
                    TipTwoDialog tipTwoDialog = new TipTwoDialog("提示", "交易状态未知是否进行交易查询确认？");
                    tipTwoDialog.setOnClickListener(new TipTwoDialog.OnClickListener() {
                        @Override
                        public void onOkClick() {
                            RequestUtil.tradeStatusConfirm(PaymentActivity.this, merchantTrade.getTerminalOrderCode(), new ICallbackTwoParams<Integer, TradeRecordInfo>() {
                                @Override
                                public void callback(Integer integer, TradeRecordInfo tradeRecordInfo) {
                                    if (integer == 0){ //交易成功
                                        tradeRecordInfo.setSellerCardNo(merchantTrade.getSellerAccount());
                                        tradeRecordInfo.setSellerName(merchantTrade.getSellerName());
                                        tradeRecordInfo.setBuyerCardNo(merchantTrade.getBuyerAccount());
                                        tradeRecordInfo.setBuyerName(merchantTrade.getBuyerName());
                                        tradeRecordInfo.setReceivableAmount(merchantTrade.getReceivableAmount());
                                        tradeRecordInfo.setActualAmount(merchantTrade.getActualAmount());
                                        tradeRecordInfo.setPayType(merchantTrade.getPayType());
                                        for (MerchantTradeGoods item : merchantTrade.getMerchantTradeGoodsList()){
                                            CategoryRecordInfo categoryRecordInfo = new CategoryRecordInfo();
                                            categoryRecordInfo.setGoodsName(item.getGoodsName());
                                            categoryRecordInfo.setGoodsPrice(item.getGoodsPrice());
                                            categoryRecordInfo.setGoodsNumber(item.getGoodsNumber());
                                            categoryRecordInfo.setGoodsAmount(item.getGoodsAmount());
                                            tradeRecordInfo.getCategoryRecordInfoList().add(categoryRecordInfo);
                                        }
                                        PrintTemplate printTemplate = new PrintTemplate(PrinterFactory.getPrinter(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL).toString(), PaymentActivity.this));
                                        printTemplate.tradeTemplate(tradeRecordInfo, new PrintTemplate.PrintResultCallback() {
                                            @Override
                                            public void result(int code, String message) {
                                                if (code != 0)
                                                    Toast.makeText(PaymentActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        MerchantTradeManager.getInstance().delete(merchantTrade);
                                    }else if (integer == 1){ //交易失败
                                        Toast.makeText(PaymentActivity.this, "交易失败", Toast.LENGTH_SHORT).show();
                                        MerchantTradeManager.getInstance().delete(merchantTrade);
                                    }else if (integer == 2){ //交易状态未知
                                        Toast.makeText(PaymentActivity.this, "交易状态查询失败", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelClick() {
                            Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    });
                    tipTwoDialog.show(getSupportFragmentManager(), null);
                }
            }
        });
    }
    //endregion
}
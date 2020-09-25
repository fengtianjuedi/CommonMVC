package com.wufeng.commonmvc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wufeng.commonmvc.adapter.CategoryRecordAdapter;
import com.wufeng.commonmvc.adapter.TradeCategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityWholesaleTradeBinding;
import com.wufeng.commonmvc.dialog.AddCategoryRecordDialog;
import com.wufeng.commonmvc.dialog.TipTwoDialog;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.callback.ICallbackTwoParams;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.control.SpaceItemDecoration;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.database.MerchantTrade;
import com.wufeng.latte_core.database.MerchantTradeGoods;
import com.wufeng.latte_core.database.MerchantTradeManager;
import com.wufeng.latte_core.device.print.PrintTemplate;
import com.wufeng.latte_core.device.print.PrinterFactory;
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.entity.CategoryRecordInfo;
import com.wufeng.latte_core.entity.TradeRecordInfo;
import com.wufeng.latte_core.util.BigDecimalUtil;
import com.wufeng.latte_core.util.IdGenerate;
import com.wufeng.latte_core.util.RequestUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WholesaleTradeActivity extends BaseActivity<ActivityWholesaleTradeBinding>
        implements TradeCategoryAdapter.OnItemClickListener, CategoryRecordAdapter.OnItemDeleteListener {
    public static final  int MAXAMOUNT = 999999999; //单笔最大金额
    private List<CategoryInfo> mCategoryData; //商品数据
    private List<CategoryRecordInfo> mCategoryRecordData; //商品记录
    private TradeCategoryAdapter tradeCategoryAdapter;
    private CategoryRecordAdapter categoryRecordAdapter;
    private BigDecimal receivableAmount; //应收金额

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mCategoryData = new ArrayList<>();
        mCategoryRecordData = new ArrayList<>();
        receivableAmount = new BigDecimal(0);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mBinding.rlvBindCategoryList.addItemDecoration(new SpaceItemDecoration(10));
        mBinding.rlvBindCategoryList.setLayoutManager(gridLayoutManager);
        tradeCategoryAdapter = new TradeCategoryAdapter(mCategoryData, this);
        mBinding.rlvBindCategoryList.setAdapter(tradeCategoryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rlvCategoryRecordList.addItemDecoration(new SpaceItemDecoration(0, 0, 0, 10));
        mBinding.rlvCategoryRecordList.setLayoutManager(linearLayoutManager);
        categoryRecordAdapter = new CategoryRecordAdapter(mCategoryRecordData, this);
        mBinding.rlvCategoryRecordList.setAdapter(categoryRecordAdapter);
        initCategoryList();
        checkoutTradeResult();
    }

    //region 初始化
    //绑定界面点击事件
    private void initClickEvent(){
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.tvAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoreCategory();
            }
        });
        mBinding.tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();
            }
        });
        mBinding.tvAddBindCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCategory();
            }
        });
    }

    //初始化品种列表
    private void initCategoryList(){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        RequestUtil.queryCategoryByCardNo(WholesaleTradeActivity.this, merchantCard.getCardNo(), new ICallback<List<CategoryInfo>>() {
            @Override
            public void callback(List<CategoryInfo> categoryInfos) {
                mCategoryData.clear();
                if(categoryInfos.size() > 7){
                    List<CategoryInfo> data = categoryInfos.subList(0, 7);
                    mCategoryData.addAll(data);
                }else{
                    mCategoryData.addAll(categoryInfos);
                }
                if (mCategoryData.size() == 0){
                    mBinding.tvAddMore.setVisibility(View.INVISIBLE);
                    mBinding.rlvBindCategoryList.setVisibility(View.GONE);
                    mBinding.tvAddBindCategory.setVisibility(View.VISIBLE);
                }else{
                    mBinding.tvAddMore.setVisibility(View.VISIBLE);
                    mBinding.rlvBindCategoryList.setVisibility(View.VISIBLE);
                    mBinding.tvAddBindCategory.setVisibility(View.GONE);
                    tradeCategoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //确认交易结果
    private void checkoutTradeResult(){
        final MerchantTrade merchantTrade = MerchantTradeManager.getInstance().queryUnKnowStatusTrade();
        if (merchantTrade != null) {
            TipTwoDialog tipTwoDialog = new TipTwoDialog("提示", "有交易状态未知的交易，是否进行交易查询确认?");
            tipTwoDialog.show(getSupportFragmentManager(), null);
            tipTwoDialog.setOnClickListener(new TipTwoDialog.OnClickListener() {
                @Override
                public void onOkClick() {
                    RequestUtil.tradeStatusConfirm(WholesaleTradeActivity.this, merchantTrade.getTerminalOrderCode(), new ICallbackTwoParams<Integer, TradeRecordInfo>() {
                        @Override
                        public void callback(Integer integer, TradeRecordInfo tradeRecordInfo) {
                            if (integer == 0) { //交易成功
                                tradeRecordInfo.setSellerCardNo(merchantTrade.getSellerAccount());
                                tradeRecordInfo.setSellerName(merchantTrade.getSellerName());
                                tradeRecordInfo.setBuyerCardNo(merchantTrade.getBuyerAccount());
                                tradeRecordInfo.setBuyerName(merchantTrade.getBuyerName());
                                tradeRecordInfo.setReceivableAmount(merchantTrade.getReceivableAmount());
                                tradeRecordInfo.setActualAmount(merchantTrade.getActualAmount());
                                tradeRecordInfo.setPayType(merchantTrade.getPayType());
                                for (MerchantTradeGoods item : merchantTrade.getMerchantTradeGoodsList()) {
                                    CategoryRecordInfo categoryRecordInfo = new CategoryRecordInfo();
                                    categoryRecordInfo.setGoodsName(item.getGoodsName());
                                    categoryRecordInfo.setGoodsPrice(item.getGoodsPrice());
                                    categoryRecordInfo.setGoodsNumber(item.getGoodsNumber());
                                    categoryRecordInfo.setGoodsAmount(item.getGoodsAmount());
                                    tradeRecordInfo.getCategoryRecordInfoList().add(categoryRecordInfo);
                                }
                                PrintTemplate printTemplate = new PrintTemplate(PrinterFactory.getPrinter(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL).toString(), WholesaleTradeActivity.this));
                                printTemplate.tradeTemplate(tradeRecordInfo, new PrintTemplate.PrintResultCallback() {
                                    @Override
                                    public void result(int code, String message) {
                                        if (code != 0)
                                            Toast.makeText(WholesaleTradeActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                MerchantTradeManager.getInstance().delete(merchantTrade);
                            } else if (integer == 1) { //交易失败
                                Toast.makeText(WholesaleTradeActivity.this, "交易失败", Toast.LENGTH_SHORT).show();
                                MerchantTradeManager.getInstance().delete(merchantTrade);
                            } else if (integer == 2) { //交易状态未知
                                Toast.makeText(WholesaleTradeActivity.this, "交易状态查询失败", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WholesaleTradeActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

                @Override
                public void onCancelClick() {
                    Intent intent = new Intent(WholesaleTradeActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
    //endregion

    //region 功能函数
    //去支付
    private void pay(){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        TradeRecordInfo tradeRecordInfo = new TradeRecordInfo();
        tradeRecordInfo.setReceivableAmount(receivableAmount.toPlainString());
        tradeRecordInfo.setActualAmount(receivableAmount.toPlainString());
        tradeRecordInfo.getCategoryRecordInfoList().addAll(mCategoryRecordData);
        tradeRecordInfo.setSellerCardNo(merchantCard.getCardNo());
        tradeRecordInfo.setSellerAccount(merchantCard.getAccountCode());
        tradeRecordInfo.setSellerName(merchantCard.getCardName());
        tradeRecordInfo.setSellerCode(merchantCard.getMerchantCode());
        Intent intent = new Intent(WholesaleTradeActivity.this, PaymentActivity.class);
        intent.putExtra("tradeRecord", tradeRecordInfo);
        startActivityForResult(intent, PaymentActivity.REQUESTCODE);
    }
    //打开更多品种页面
    private void openMoreCategory(){
        Intent intent = new Intent(WholesaleTradeActivity.this, AllBindCategoryActivity.class);
        startActivityForResult(intent, AllBindCategoryActivity.REQUESTCODE);
    }

    //打开添加品种页面
    private void openAddCategory(){
        Intent intent = new Intent(WholesaleTradeActivity.this, AddCategoryActivity.class);
        startActivityForResult(intent, AddCategoryActivity.REQUESTCODE);
    }

    //打开品种记录输入弹窗
    private void openCategoryInputDialog(CategoryInfo categoryInfo){
        AddCategoryRecordDialog addCategoryRecordDialog = new AddCategoryRecordDialog(categoryInfo, new AddCategoryRecordDialog.OnAddCategoryRecordListener() {
            @Override
            public void onAddCategoryRecord(CategoryRecordInfo categoryRecordInfo) {
                BigDecimal sum = BigDecimalUtil.sumB(receivableAmount.toPlainString(), categoryRecordInfo.getGoodsAmount());
                if (sum.compareTo(new BigDecimal(MAXAMOUNT)) > 0){
                    Toast.makeText(WholesaleTradeActivity.this, "单笔交易限额" + MAXAMOUNT, Toast.LENGTH_SHORT).show();
                    return;
                }
                mCategoryRecordData.add(categoryRecordInfo);
                categoryRecordAdapter.notifyItemInserted(mCategoryRecordData.size() - 1);
                receivableAmount = sum;
                String payText = mCategoryRecordData.size() + "件商品，共记" + receivableAmount.toPlainString() + "元，去收款";
                mBinding.tvPay.setText(payText);
            }
        });
        addCategoryRecordDialog.show(getSupportFragmentManager(), null);
    }

    //添加品种
    private void addCategory(final CategoryInfo categoryInfo){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        RequestUtil.bindCategory(WholesaleTradeActivity.this, merchantCard.getCardNo(), categoryInfo.getId(), new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    mCategoryData.add(categoryInfo);
                    mBinding.tvAddMore.setVisibility(View.VISIBLE);
                    mBinding.rlvBindCategoryList.setVisibility(View.VISIBLE);
                    mBinding.tvAddBindCategory.setVisibility(View.GONE);
                    tradeCategoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //清理数据
    private void clearData(){
        receivableAmount = new BigDecimal(0);
        mBinding.tvPay.setText("去收款");
        mCategoryRecordData.clear();
        categoryRecordAdapter.notifyDataSetChanged();
    }
    //endregion

    //品种子项点击事件
    @Override
    public void onItemClick(CategoryInfo categoryInfo) {
        openCategoryInputDialog(categoryInfo);
    }

    @Override
    public void onItemDelete(int position, CategoryRecordInfo categoryRecordInfo) {
        mCategoryRecordData.remove(position);
        categoryRecordAdapter.notifyItemRemoved(position);
        receivableAmount = BigDecimalUtil.subB(receivableAmount.toPlainString(), categoryRecordInfo.getGoodsAmount());
        String payText = mCategoryRecordData.size() + "件商品，共记" + receivableAmount.toPlainString() + "元，去收款";
        mBinding.tvPay.setText(payText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AllBindCategoryActivity.REQUESTCODE && resultCode == RESULT_OK) {
            CategoryInfo info = new CategoryInfo();
            info.setId(data.getStringExtra("id"));
            info.setName(data.getStringExtra("name"));
            mCategoryData.add(info);
            tradeCategoryAdapter.notifyItemInserted(mCategoryData.size() - 1);
            openCategoryInputDialog(info);
        }else if (requestCode == AllBindCategoryActivity.REQUESTCODE && resultCode == RESULT_CANCELED){
            initCategoryList();
        }
        else if (requestCode == AddCategoryActivity.REQUESTCODE && resultCode == RESULT_OK){
            CategoryInfo info = new CategoryInfo();
            info.setId(data.getStringExtra("id"));
            info.setName(data.getStringExtra("name"));
            addCategory(info);
        }else if (requestCode == PaymentActivity.REQUESTCODE && resultCode == RESULT_OK){
            clearData();
        }else if (requestCode == PaymentActivity.REQUESTCODE && resultCode == RESULT_CANCELED){
            clearData();
        }
    }
}
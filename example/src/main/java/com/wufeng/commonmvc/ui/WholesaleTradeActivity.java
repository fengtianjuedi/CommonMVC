package com.wufeng.commonmvc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wufeng.commonmvc.adapter.CategoryRecordAdapter;
import com.wufeng.commonmvc.adapter.TradeCategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityWholesaleTradeBinding;
import com.wufeng.commonmvc.dialog.AddCategoryRecordDialog;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.commonmvc.entity.CategoryRecordInfo;
import com.wufeng.commonmvc.entity.TradeRecordInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.control.SpaceItemDecoration;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.util.BigDecimalUtil;
import com.wufeng.latte_core.util.IdGenerate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WholesaleTradeActivity extends BaseActivity<ActivityWholesaleTradeBinding>
        implements TradeCategoryAdapter.OnItemClickListener, CategoryRecordAdapter.OnItemDeleteListener {
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
        for(int i = 0; i < 10; i++){
            CategoryInfo info = new CategoryInfo();
            info.setName("苹果" + i);
            mCategoryData.add(info);
        }
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
    }
    //endregion

    //region 功能函数
    //去支付
    private void pay(){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        TradeRecordInfo tradeRecordInfo = new TradeRecordInfo();
        tradeRecordInfo.setTerminalOrderCode(IdGenerate.getInstance().getId());
        tradeRecordInfo.setReceivableAmount(receivableAmount.toPlainString());
        tradeRecordInfo.setActualAmount(receivableAmount.toPlainString());
        tradeRecordInfo.getCategoryRecordInfoList().addAll(mCategoryRecordData);
        tradeRecordInfo.setSellerAccount(merchantCard.getCardNo());
        tradeRecordInfo.setSellerName(merchantCard.getCardName());
        Intent intent = new Intent(WholesaleTradeActivity.this, PaymentActivity.class);
        intent.putExtra("tradeRecord", tradeRecordInfo);
        startActivity(intent);
    }
    //打开更多品种页面
    private void openMoreCategory(){
        Intent intent = new Intent(WholesaleTradeActivity.this, AllBindCategoryActivity.class);
        startActivityForResult(intent, AllBindCategoryActivity.REQUESTCODE);
    }
    //endregion

    //品种子项点击事件
    @Override
    public void onItemClick(CategoryInfo categoryInfo) {
        AddCategoryRecordDialog addCategoryRecordDialog = new AddCategoryRecordDialog(categoryInfo, new AddCategoryRecordDialog.OnAddCategoryRecordListener() {
            @Override
            public void onAddCategoryRecord(CategoryRecordInfo categoryRecordInfo) {
                mCategoryRecordData.add(categoryRecordInfo);
                categoryRecordAdapter.notifyItemInserted(mCategoryRecordData.size() - 1);
                receivableAmount = BigDecimalUtil.sumB(receivableAmount.toPlainString(), categoryRecordInfo.getGoodsAmount());
                String payText = mCategoryRecordData.size() + "件商品，共记" + receivableAmount.toPlainString() + "元，去收款";
                mBinding.tvPay.setText(payText);
            }
        });
        addCategoryRecordDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCategoryData.size() == 0){
            mBinding.tvAddMore.setVisibility(View.INVISIBLE);
            mBinding.rlvBindCategoryList.setVisibility(View.GONE);
            mBinding.tvAddBindCategory.setVisibility(View.VISIBLE);
        }else{
            mBinding.tvAddMore.setVisibility(View.VISIBLE);
            mBinding.rlvBindCategoryList.setVisibility(View.VISIBLE);
            mBinding.tvAddBindCategory.setVisibility(View.GONE);
        }
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
        }
    }
}
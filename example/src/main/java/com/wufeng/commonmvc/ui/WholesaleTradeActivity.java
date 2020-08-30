package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.adapter.CategoryRecordAdapter;
import com.wufeng.commonmvc.adapter.MerchantCardAdapter;
import com.wufeng.commonmvc.adapter.TradeCategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityWholesaleTradeBinding;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.commonmvc.entity.CategoryRecordInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.control.SpaceItemDecoration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WholesaleTradeActivity extends BaseActivity<ActivityWholesaleTradeBinding>
        implements TradeCategoryAdapter.OnItemClickListener, CategoryRecordAdapter.OnItemDeleteListener {
    private List<CategoryInfo> mCategoryData; //商品数据
    private List<CategoryRecordInfo> mCategoryRecordData; //商品记录
    private TradeCategoryAdapter tradeCategoryAdapter;
    private CategoryRecordAdapter categoryRecordAdapter;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mCategoryData = new ArrayList<>();
        mCategoryRecordData = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            CategoryInfo info = new CategoryInfo();
            info.setName("苹果" + i);
            mCategoryData.add(info);
        }
        for(int i = 0; i < 10; i++){
            CategoryRecordInfo info = new CategoryRecordInfo();
            info.setGoodsName("苹果" + i);
            info.setGoodsPrice(new BigDecimal(10));
            info.setGoodsNumber(i);
            info.setGoodsAmount(new BigDecimal(18888));
            mCategoryRecordData.add(info);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mBinding.rlvBindCategoryList.addItemDecoration(new SpaceItemDecoration(5));
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
    }
    //endregion

    //品种子项点击事件
    @Override
    public void onItemClick(CategoryInfo categoryInfo) {

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
    public void onItemDelete(CategoryRecordInfo categoryRecordInfo) {

    }
}
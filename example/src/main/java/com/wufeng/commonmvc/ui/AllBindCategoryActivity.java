package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.adapter.TradeCategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityAllBindCategoryBinding;
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.control.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AllBindCategoryActivity extends BaseActivity<ActivityAllBindCategoryBinding> implements TradeCategoryAdapter.OnItemClickListener {
    public static final int REQUESTCODE = 2;
    private List<CategoryInfo> mData;
    private TradeCategoryAdapter tradeCategoryAdapter;
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mData = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            CategoryInfo info = new CategoryInfo();
            info.setName("苹果" + i);
            mData.add(info);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mBinding.rlvBindCategoryList.addItemDecoration(new SpaceItemDecoration(10));
        mBinding.rlvBindCategoryList.setLayoutManager(gridLayoutManager);
        tradeCategoryAdapter = new TradeCategoryAdapter(mData, this);
        mBinding.rlvBindCategoryList.setAdapter(tradeCategoryAdapter);
    }

    //region 初始化
    //绑定界面点击事件
    private void initClickEvent(){
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        mBinding.tvAddBindCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCategory();
            }
        });
    }

    //返回上一级
    private void back(){
        setResult(RESULT_CANCELED);
        finish();
    }

    //endregion

    //打开添加品种页面
    private void openAddCategory(){
        Intent intent = new Intent(AllBindCategoryActivity.this, AddCategoryActivity.class);
        startActivityForResult(intent, AddCategoryActivity.REQUESTCODE);
    }

    //品种子项选中事件
    @Override
    public void onItemClick(CategoryInfo categoryInfo) {
        Intent intent = new Intent();
        intent.putExtra("id", categoryInfo.getId());
        intent.putExtra("name", categoryInfo.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddCategoryActivity.REQUESTCODE && resultCode == RESULT_OK) {
            CategoryInfo info = new CategoryInfo();
            info.setId(data.getStringExtra("id"));
            info.setName(data.getStringExtra("name"));
            mData.add(info);
            tradeCategoryAdapter.notifyDataSetChanged();
        }
    }
}
package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.adapter.CategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityCategoryManagerBinding;
import com.wufeng.commonmvc.dialog.TipOneDialog;
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.control.SpaceItemDecoration;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.RequestUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CategoryManagerActivity extends BaseActivity<ActivityCategoryManagerBinding> implements CategoryAdapter.OnDeleteItemListener {
    private List<CategoryInfo> mData; //品种列表
    private CategoryAdapter categoryAdapter; //品种列表适配器

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mData = new LinkedList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rlvCategoryList.setLayoutManager(linearLayoutManager);
        mBinding.rlvCategoryList.addItemDecoration(new SpaceItemDecoration(0,0,0,30));
        categoryAdapter = new CategoryAdapter(mData);
        categoryAdapter.setOnDeleteItemListener(this);
        mBinding.rlvCategoryList.setAdapter(categoryAdapter);
        initCategoryList();
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
        mBinding.itvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCategory();
            }
        });
    }

    //打开添加品种页面
    private void openAddCategory() {
        Intent intent = new Intent(CategoryManagerActivity.this, AddCategoryActivity.class);
        startActivityForResult(intent, AddCategoryActivity.REQUESTCODE);
    }

    //初始化收款卡号品种列表
    private void initCategoryList(){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        if (merchantCard == null){
            TipOneDialog dialog = new TipOneDialog("提示", "收款账户未设置，请先设置！");
            dialog.show(getSupportFragmentManager(), "categoryManager");
            return;
        }
        RequestUtil.queryCategoryByCardNo(CategoryManagerActivity.this,merchantCard.getCardNo(), new ICallback<List<CategoryInfo>>() {
            @Override
            public void callback(List<CategoryInfo> categoryInfos) {
                if (categoryInfos != null){
                    mData.addAll(categoryInfos);
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    //endregion


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddCategoryActivity.REQUESTCODE && resultCode == RESULT_OK) {
            CategoryInfo info = new CategoryInfo();
            info.setId(data.getStringExtra("id"));
            info.setName(data.getStringExtra("name"));
            bindCategory(info);
        }
    }

    //删除列表子项事件
    @Override
    public void onDeleteItem(final int position, CategoryInfo categoryInfo) {
        String cardNo = MerchantCardManager.getInstance().queryCollectionAccount().getCardNo();
        RequestUtil.deleteBindCategory(CategoryManagerActivity.this, cardNo, categoryInfo.getId(), new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    mData.remove(position);
                    categoryAdapter.notifyItemRemoved(position);
                }
            }
        });
    }

    //region 功能函数
    //绑定品种
    private void bindCategory(final CategoryInfo categoryInfo){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        if (merchantCard == null){
            TipOneDialog dialog = new TipOneDialog("提示", "添加品种失败！请先设置收款账户");
            dialog.show(getSupportFragmentManager(), "categoryManager");
            return;
        }
        for (CategoryInfo item : mData) {
            if (item.getId().equals(categoryInfo.getId())){
                Toast.makeText(CategoryManagerActivity.this, "品种已绑定", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        RequestUtil.bindCategory(CategoryManagerActivity.this, merchantCard.getCardNo(), categoryInfo.getId(), new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    mData.add(categoryInfo);
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //endregion
}

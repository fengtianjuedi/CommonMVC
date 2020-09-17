package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.adapter.CategoryTreeAdapter;
import com.wufeng.commonmvc.databinding.ActivityAddCategoryBinding;
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.entity.CategoryNode;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.control.DrawableEditText;
import com.wufeng.latte_core.database.TerminalInfo;
import com.wufeng.latte_core.database.TerminalInfoManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;
import com.wufeng.latte_core.util.RequestUtil;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryActivity extends BaseActivity<ActivityAddCategoryBinding> implements CategoryTreeAdapter.OnEndNodeClickListener {
    public static final int REQUESTCODE = 1; //请求码
    private List<CategoryNode> mData; //品种树数据
    private CategoryTreeAdapter categoryTreeAdapter; //品种树适配器
    private TerminalInfo terminalInfo; //终端信息

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        terminalInfo = TerminalInfoManager.getInstance().queryLastTerminalInfo();
        mData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        categoryTreeAdapter = new CategoryTreeAdapter(AddCategoryActivity.this, mData, this);
        mBinding.rlvCategoryTree.setLayoutManager(linearLayoutManager);
        mBinding.rlvCategoryTree.setAdapter(categoryTreeAdapter);
        RequestUtil.queryCategoryById(AddCategoryActivity.this,"", new ICallback<List<CategoryNode>>() {
            @Override
            public void callback(List<CategoryNode> categoryNodes) {
                mData.addAll(categoryNodes);
                categoryTreeAdapter.notifyDataSetChanged();
            }
        });
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
        mBinding.detSort.setOnDrawableRightListener(new DrawableEditText.OnDrawableRightListener() {
            @Override
            public void onDrawableRightClick() {
                sortCategory();
            }
        });
    }
    //endregion

    //品种树终节点点击事件
    @Override
    public void onEndNodeClick(CategoryInfo categoryInfo) {
        backCategory(categoryInfo);
    }

    //region 功能函数
    //返回上一级
    private void back(){
        setResult(RESULT_CANCELED);
        finish();
    }

    //返回要添加的品种
    private void backCategory(CategoryInfo categoryInfo){
        Intent intent = new Intent();
        intent.putExtra("id", categoryInfo.getId());
        intent.putExtra("name", categoryInfo.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    //品种搜索
    private void sortCategory(){
        String content = mBinding.detSort.getText().toString();
        if (TextUtils.isEmpty(content))
            return;
        RequestUtil.queryCategoryByName(AddCategoryActivity.this, content, new ICallback<List<CategoryNode>>() {
            @Override
            public void callback(List<CategoryNode> categoryNodes) {
                mData.clear();
                mData.addAll(categoryNodes);
                categoryTreeAdapter.notifyDataSetChanged();
            }
        });
    }
    //endregion
}
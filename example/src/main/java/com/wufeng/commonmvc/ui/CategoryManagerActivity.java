package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.adapter.CategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityCategoryManagerBinding;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.control.SpaceItemDecoration;

import java.util.LinkedList;
import java.util.List;

public class CategoryManagerActivity extends BaseActivity<ActivityCategoryManagerBinding> {
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
        mBinding.rlvCategoryList.setAdapter(categoryAdapter);
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
    //endregion


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddCategoryActivity.REQUESTCODE && resultCode == RESULT_OK) {
            CategoryInfo info = new CategoryInfo();
            info.setId(data.getStringExtra("id"));
            info.setName(data.getStringExtra("name"));
            mData.add(info);
            categoryAdapter.notifyDataSetChanged();
        }
    }
}
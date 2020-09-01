package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.adapter.CategoryTreeAdapter;
import com.wufeng.commonmvc.databinding.ActivityAddCategoryBinding;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.commonmvc.entity.CategoryNode;
import com.wufeng.latte_core.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryActivity extends BaseActivity<ActivityAddCategoryBinding> implements CategoryTreeAdapter.OnEndNodeClickListener {
    public static final int REQUESTCODE = 1; //请求码
    private List<CategoryNode> mData; //品种树数据
    private CategoryInfo currentSelectedCategory; //当前选中的品种
    private CategoryTreeAdapter categoryTreeAdapter; //品种树适配器

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mData = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            CategoryNode node = new CategoryNode();
            node.setNodeId("0" + i);
            node.setLevel(0);
            node.setName("品类" + i);
            mData.add(node);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        categoryTreeAdapter = new CategoryTreeAdapter(mData, this);
        mBinding.rlvCategoryTree.setLayoutManager(linearLayoutManager);
        mBinding.rlvCategoryTree.setAdapter(categoryTreeAdapter);
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
    }

    //返回上一级
    private void back(){
        setResult(RESULT_CANCELED);
        finish();
    }

    //返回要添加的品种
    private void backCategory(){
        Intent intent = new Intent();
        intent.putExtra("id", currentSelectedCategory.getId());
        intent.putExtra("name", currentSelectedCategory.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    //品种树终节点点击事件
    @Override
    public void onEndNodeClick(CategoryInfo categoryInfo) {
        currentSelectedCategory = categoryInfo;
        backCategory();
    }
    //endregion
}
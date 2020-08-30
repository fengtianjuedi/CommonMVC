package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.databinding.ActivityAddCategoryBinding;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.latte_core.activity.BaseActivity;

public class AddCategoryActivity extends BaseActivity<ActivityAddCategoryBinding> {
    public static final int REQUESTCODE = 1; //请求码
    private CategoryInfo currentSelectedCategory; //当前选中的品种

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        currentSelectedCategory = new CategoryInfo();
        currentSelectedCategory.setName("苹果");
        currentSelectedCategory.setId("0001");
    }

    //region 初始化
    //绑定界面点击事件
    private void initClickEvent(){
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backCategory();
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
    //endregion
}
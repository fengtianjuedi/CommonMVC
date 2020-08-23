package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.databinding.ActivitySetTerminalBinding;
import com.wufeng.latte_core.activity.BaseActivity;

public class SetTerminalActivity extends BaseActivity<ActivitySetTerminalBinding> {

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();

    }

    //region 初始化点击事件
    private void initClickEvent(){
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    //设置终端
    private void back(){
        finish();
    }
    //endregion
}
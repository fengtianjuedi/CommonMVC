package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.databinding.ActivityHomeBinding;
import com.wufeng.latte_core.activity.BaseActivity;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
    }

    //region 初始化点击事件
    private void initClickEvent(){
        mBinding.llSetTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTerminal();
            }
        });
        mBinding.llBindCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindCard();
            }
        });
    }

    //设置终端
    private void setTerminal(){
        Intent intent = new Intent(HomeActivity.this, SetTerminalActivity.class);
        startActivity(intent);
    }

    //绑定商户卡
    private void bindCard(){
        Intent intent = new Intent(HomeActivity.this, BindCardActivity.class);
        startActivity(intent);
    }

    //endregion
}

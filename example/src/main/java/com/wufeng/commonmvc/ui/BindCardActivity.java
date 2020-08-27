package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.adapter.MerchantCardAdapter;
import com.wufeng.commonmvc.databinding.ActivityBindCardBinding;
import com.wufeng.commonmvc.entity.CardInfo;
import com.wufeng.latte_core.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class BindCardActivity extends BaseActivity<ActivityBindCardBinding> implements MerchantCardAdapter.CollectionAccountChangedLister {

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        CardInfo cardInfo1 = new CardInfo();
        cardInfo1.setCardNo("612349999999999");
        cardInfo1.setName("张三");
        CardInfo cardInfo2 = new CardInfo();
        cardInfo2.setCardNo("612340000000000");
        cardInfo2.setName("李四");
        List<CardInfo> data = new ArrayList<>();
        data.add(cardInfo1);
        data.add(cardInfo2);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rlvBindCardList.setLayoutManager(linearLayoutManager);
        MerchantCardAdapter merchantCardAdapter = new MerchantCardAdapter(data, this);
        mBinding.rlvBindCardList.setAdapter(merchantCardAdapter);
    }

    //region 初始化点击事件
    private void initClickEvent(){
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        mBinding.itvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMerchantCard();
            }
        });
        mBinding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCollectionAccount();
            }
        });
    }

    //返回上一级
    private void back(){
        finish();
    }

    //新增绑定商户卡
    private void addMerchantCard() {
        Intent intent = new Intent(BindCardActivity.this, AddMerchantCardActivity.class);
        startActivity(intent);
    }

    //删除收款账户
    private void deleteCollectionAccount(){
        mBinding.tvCollectionAccountNo.setText("");
        mBinding.tvCollectionAccountName.setText("");
    }

    //设置收款账户
    @Override
    public void setCollectionAccount(CardInfo cardInfo) {
        mBinding.tvCollectionAccountNo.setText(cardInfo.getCardNo());
        mBinding.tvCollectionAccountName.setText(cardInfo.getName());
    }
    //endregion
}
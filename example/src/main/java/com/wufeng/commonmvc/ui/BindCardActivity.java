package com.wufeng.commonmvc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wufeng.commonmvc.adapter.MerchantCardAdapter;
import com.wufeng.commonmvc.databinding.ActivityBindCardBinding;
import com.wufeng.commonmvc.entity.CardInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;

import java.util.ArrayList;
import java.util.List;

public class BindCardActivity extends BaseActivity<ActivityBindCardBinding> implements MerchantCardAdapter.CollectionAccountChangedLister {
    private List<CardInfo> mData; //绑定卡列表
    private CardInfo mCollectionAccountCard; //当前收款卡
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        initBindCard();
        if (mCollectionAccountCard != null){
            mBinding.tvCollectionAccountNo.setText(mCollectionAccountCard.getCardNo());
            mBinding.tvCollectionAccountName.setText(mCollectionAccountCard.getName());
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rlvBindCardList.setLayoutManager(linearLayoutManager);
        MerchantCardAdapter merchantCardAdapter = new MerchantCardAdapter(mData, this);
        mBinding.rlvBindCardList.setAdapter(merchantCardAdapter);
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
    //获取卡绑定数据
    private void initBindCard(){
        List<MerchantCard> list = MerchantCardManager.getInstance().query();
        mData = new ArrayList<>();
        for(MerchantCard item : list){
            CardInfo info = new CardInfo();
            info.setCardNo(item.getCardNo());
            info.setName(item.getCardName());
            if (item.getIsCollectionAccount())
                mCollectionAccountCard = info;
            mData.add(info);
        }
    }
    //endregion
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
        if (mCollectionAccountCard == null)
            return;
        mBinding.tvCollectionAccountNo.setText("");
        mBinding.tvCollectionAccountName.setText("");
        MerchantCard merchantCard = new MerchantCard();
        merchantCard.setIsCollectionAccount(false);
        merchantCard.setCardNo(mCollectionAccountCard.getCardNo());
        MerchantCardManager.getInstance().modify(merchantCard);
        mCollectionAccountCard = null;
    }

    //设置收款账户
    @Override
    public void setCollectionAccount(CardInfo cardInfo) {
        mBinding.tvCollectionAccountNo.setText(cardInfo.getCardNo());
        mBinding.tvCollectionAccountName.setText(cardInfo.getName());
        MerchantCard merchantCardOld = new MerchantCard();
        merchantCardOld.setCardNo(mCollectionAccountCard.getCardNo());
        merchantCardOld.setIsCollectionAccount(false);
        MerchantCardManager.getInstance().modify(merchantCardOld);
        mCollectionAccountCard = cardInfo;
        MerchantCard merchantCardNew = new MerchantCard();
        merchantCardNew.setCardNo(mCollectionAccountCard.getCardNo());
        merchantCardNew.setIsCollectionAccount(true);
        MerchantCardManager.getInstance().modify(merchantCardNew);
    }
}
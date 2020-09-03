package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.wufeng.commonmvc.adapter.TradeRecordAdapter;
import com.wufeng.commonmvc.databinding.ActivityTradeRecordBinding;
import com.wufeng.commonmvc.entity.CardInfo;
import com.wufeng.commonmvc.entity.TradeRecordInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.control.SpaceItemDecoration;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;

import java.util.ArrayList;
import java.util.List;

public class TradeRecordActivity extends BaseActivity<ActivityTradeRecordBinding> {
    private List<TradeRecordInfo> mData;
    private TradeRecordAdapter tradeRecordAdapter;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mData = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            TradeRecordInfo info = new TradeRecordInfo();
            info.setTradeOrderCode("600120000000001234" + i);
            info.setReceivableAmount("10" + i);
            info.setActualAmount("11" + i);
            info.setTradeTime("2020-09-03 21:46:59");
            mData.add(info);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpaceItemDecoration decoration = new SpaceItemDecoration(0, 0, 0, 20);
        tradeRecordAdapter = new TradeRecordAdapter(mData);
        mBinding.rlvTradeRecordList.setLayoutManager(linearLayoutManager);
        mBinding.rlvTradeRecordList.addItemDecoration(decoration);
        mBinding.rlvTradeRecordList.setAdapter(tradeRecordAdapter);
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

    }

    //endregion
}
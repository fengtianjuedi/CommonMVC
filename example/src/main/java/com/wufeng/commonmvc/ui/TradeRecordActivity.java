package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.wufeng.commonmvc.adapter.TradeRecordAdapter;
import com.wufeng.commonmvc.databinding.ActivityTradeRecordBinding;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.entity.TradeRecordInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.control.DrawableEditText;
import com.wufeng.latte_core.control.SpaceItemDecoration;
import com.wufeng.latte_core.util.RequestUtil;
import com.wufeng.latte_core.util.SoftKeyBoardUtil;
import com.wufeng.latte_core.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TradeRecordActivity extends BaseActivity<ActivityTradeRecordBinding> {
    private List<TradeRecordInfo> mData;
    private TradeRecordAdapter tradeRecordAdapter;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpaceItemDecoration decoration = new SpaceItemDecoration(0, 0, 0, 20);
        tradeRecordAdapter = new TradeRecordAdapter(TradeRecordActivity.this, mData);
        mBinding.rlvTradeRecordList.setLayoutManager(linearLayoutManager);
        mBinding.rlvTradeRecordList.addItemDecoration(decoration);
        mBinding.rlvTradeRecordList.setAdapter(tradeRecordAdapter);
        mBinding.detStartDate.setText(TimeUtil.currentDateYMD());
        mBinding.detEndDate.setText(TimeUtil.currentDateYMD());
    }

    //region 初始化
    //绑定界面点击事件
    private void initClickEvent() {
        mBinding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.detStartDate.setOnDrawableRightListener(new DrawableEditText.OnDrawableRightListener() {
            @Override
            public void onDrawableRightClick() {
                getStartDate();
            }
        });
        mBinding.detEndDate.setOnDrawableRightListener(new DrawableEditText.OnDrawableRightListener() {
            @Override
            public void onDrawableRightClick() {
                getEndDate();
            }
        });
        mBinding.btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestUtil.queryTradeRecord(TradeRecordActivity.this, mBinding.detStartDate.getText().toString() + " 00:00:00", mBinding.detEndDate.getText().toString() + " 23:59:59", new ICallback<List<TradeRecordInfo>>() {
                    @Override
                    public void callback(List<TradeRecordInfo> tradeRecordInfos) {
                        mData.clear();
                        mData.addAll(tradeRecordInfos);
                        tradeRecordAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
    //endregion

    //获取开始日期
    private void getStartDate(){
        SoftKeyBoardUtil.hideSoftKeyBoard(TradeRecordActivity.this, mBinding.detStartDate);
        new TimePickerBuilder(TradeRecordActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Date endDate = TimeUtil.parseStringToDateYMDHMS(mBinding.detStartDate.getText().toString()+" 23:59:59");
                if (endDate !=null && date.compareTo(endDate) > 0){
                    Toast.makeText(TradeRecordActivity.this, "开始结束日期不能比结束日期大", Toast.LENGTH_SHORT).show();
                    return;
                }
                String startDate = TimeUtil.dateToStringYMD(date);
                mBinding.detStartDate.setText(startDate);
                mBinding.detStartDate.setSelection(startDate.length());
                mBinding.detStartDate.requestFocus();
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .build()
        .show();
    }

    //获取结束日期
    private void getEndDate(){
        SoftKeyBoardUtil.hideSoftKeyBoard(TradeRecordActivity.this, mBinding.detEndDate);
        new TimePickerBuilder(TradeRecordActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Date startDate = TimeUtil.parseStringToDateYMDHMS(mBinding.detStartDate.getText().toString() + " 00:00:00");
                if (startDate != null && date.compareTo(startDate) < 0){
                    Toast.makeText(TradeRecordActivity.this, "结束日期不能比开始日期小", Toast.LENGTH_SHORT).show();
                    return;
                }
                String endDate = TimeUtil.dateToStringYMD(date);
                mBinding.detEndDate.setText(endDate);
                mBinding.detEndDate.setSelection(endDate.length());
                mBinding.detEndDate.requestFocus();
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .build()
        .show();
    }

}
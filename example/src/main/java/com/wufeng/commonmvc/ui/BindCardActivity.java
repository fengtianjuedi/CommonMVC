package com.wufeng.commonmvc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.adapter.MerchantCardAdapter;
import com.wufeng.commonmvc.databinding.ActivityBindCardBinding;
import com.wufeng.commonmvc.dialog.PasswordDialog;
import com.wufeng.commonmvc.entity.CardInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

import java.util.ArrayList;
import java.util.List;

public class BindCardActivity extends BaseActivity<ActivityBindCardBinding>
        implements MerchantCardAdapter.CollectionAccountChangedListener, MerchantCardAdapter.QueryCardBalanceListener {
    private List<CardInfo> mData; //绑定卡列表
    private CardInfo currentCollectionAccount; //当前收款账户
    private MerchantCardAdapter merchantCardAdapter;
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        initClickEvent();
        mData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rlvBindCardList.setLayoutManager(linearLayoutManager);
        merchantCardAdapter = new MerchantCardAdapter(mData, this, this);
        mBinding.rlvBindCardList.setAdapter(merchantCardAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBindCard();
        if (currentCollectionAccount != null){
            mBinding.tvCollectionAccountNo.setText(currentCollectionAccount.getCardNo());
            mBinding.tvCollectionAccountName.setText(currentCollectionAccount.getName());
            mBinding.tvDelete.setVisibility(View.VISIBLE);
            mBinding.tvQueryCardBalance.setVisibility(View.VISIBLE);
        }
        merchantCardAdapter.notifyDataSetChanged();
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
        mBinding.itvAdd.setOnClickListener(new View.OnClickListener() { //添加绑卡
            @Override
            public void onClick(View v) {
                openBindMerchantCard();
            }
        });
        mBinding.tvDelete.setOnClickListener(new View.OnClickListener() { //删除收款账户
            @Override
            public void onClick(View v) {
                deleteCollectionAccount();
            }
        });
        mBinding.tvQueryCardBalance.setOnClickListener(new View.OnClickListener() { //查询卡余额
            @Override
            public void onClick(View v) {
                queryCardBalance(currentCollectionAccount.getCardNo());
            }
        });
    }
    //获取卡绑定数据
    private void initBindCard(){
        mData.clear();
        currentCollectionAccount = null;
        List<MerchantCard> list = MerchantCardManager.getInstance().query();
        for(MerchantCard item : list){
            CardInfo info = new CardInfo();
            info.setCardNo(item.getCardNo());
            info.setName(item.getCardName());
            if (item.getIsCollectionAccount()){
                currentCollectionAccount = info;
                continue;
            }
            mData.add(info);
        }
    }
    //endregion
    //打开商户卡绑定页
    private void openBindMerchantCard() {
        Intent intent = new Intent(BindCardActivity.this, AddMerchantCardActivity.class);
        startActivity(intent);
    }

    //删除收款账户
    private void deleteCollectionAccount(){
        if (!deleteOldCollectionAccount()){
            Toast.makeText(this, "删除旧收款账户失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mData.add(currentCollectionAccount);
        currentCollectionAccount = null;
        mBinding.tvCollectionAccountNo.setText("");
        mBinding.tvCollectionAccountName.setText("");
        mBinding.tvDelete.setVisibility(View.INVISIBLE);
        mBinding.tvQueryCardBalance.setVisibility(View.INVISIBLE);
        merchantCardAdapter.notifyDataSetChanged();
    }

    //查询卡余额
    private void queryCardBalance(final String cardNo){
        PasswordDialog passwordDialog = new PasswordDialog(BindCardActivity.this);
        passwordDialog.setOnClickListener(new PasswordDialog.OnClickListener() {
            @Override
            public void onOkClick(String password) {
                queryCardBalanceRequest(cardNo, password);
            }

            @Override
            public void onCancelClick() {
            }
        });
        passwordDialog.show(getSupportFragmentManager(), null);
    }

    //设置收款账户
    @Override
    public void setCollectionAccount(CardInfo cardInfo) {
        if (currentCollectionAccount != null && cardInfo.getCardNo().equals(currentCollectionAccount.getCardNo())){
            Toast.makeText(this, "当前卡已是收款账户", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!deleteOldCollectionAccount()){
            Toast.makeText(this, "删除旧收款账户失败", Toast.LENGTH_SHORT).show();
            return;
        }
        currentCollectionAccount = cardInfo;
        mBinding.tvCollectionAccountNo.setText(cardInfo.getCardNo());
        mBinding.tvCollectionAccountName.setText(cardInfo.getName());
        if(!setNewCollectionAccount(cardInfo)){
            Toast.makeText(this, "设置新收款账户失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mBinding.tvDelete.setVisibility(View.VISIBLE);
        mBinding.tvQueryCardBalance.setVisibility(View.VISIBLE);
        mData.remove(cardInfo);
        merchantCardAdapter.notifyDataSetChanged();
    }

    //卡余额查询
    @Override
    public void queryBalance(String cardNo) {
        queryCardBalance(cardNo);
    }

    //region 功能函数
    //设置新收款账户
    private boolean setNewCollectionAccount(CardInfo cardInfo){
        MerchantCard newCollectionAccount = new MerchantCard();
        newCollectionAccount.setIsCollectionAccount(true);
        newCollectionAccount.setCardNo(cardInfo.getCardNo());
        newCollectionAccount.setCardName(cardInfo.getName());
        return MerchantCardManager.getInstance().modify(newCollectionAccount);
    }

    //删除旧收款账户
    private boolean deleteOldCollectionAccount(){
        List<MerchantCard> cardList = MerchantCardManager.getInstance().query();
        MerchantCard currentCollectionAccount = null;
        for (MerchantCard item : cardList){
            if (item.getIsCollectionAccount())
                currentCollectionAccount = item;
        }
        if (currentCollectionAccount != null){
            currentCollectionAccount.setIsCollectionAccount(false);
            return MerchantCardManager.getInstance().modify(currentCollectionAccount);
        }
        return true;
    }

    //查询卡余额
    private void queryCardBalanceRequest(String cardNo, String password){
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        params.put("password", password);
        RestClient.builder()
                .url("/pgcore-pos/PosTrade/withdrawQuery")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){

                        }else{
                            Toast.makeText(BindCardActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(BindCardActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(BindCardActivity.this)
                .build()
                .post();
    }
    //endregion
}
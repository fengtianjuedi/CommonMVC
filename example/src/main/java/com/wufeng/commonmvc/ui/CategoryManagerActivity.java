package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.adapter.CategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityCategoryManagerBinding;
import com.wufeng.commonmvc.dialog.TipOneDialog;
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.control.SpaceItemDecoration;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CategoryManagerActivity extends BaseActivity<ActivityCategoryManagerBinding> implements CategoryAdapter.OnDeleteItemListener {
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
        categoryAdapter.setOnDeleteItemListener(this);
        mBinding.rlvCategoryList.setAdapter(categoryAdapter);
        initCategoryList();
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

    //初始化收款卡号品种列表
    private void initCategoryList(){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        if (merchantCard == null){
            TipOneDialog dialog = new TipOneDialog("提示", "收款账户未设置，请先设置！");
            dialog.show(getSupportFragmentManager(), "categoryManager");
            return;
        }
        queryMerchantCategoryList(merchantCard.getCardNo(), new ICallback<List<CategoryInfo>>() {
            @Override
            public void callback(List<CategoryInfo> categoryInfos) {
                if (categoryInfos != null){
                    mData.addAll(categoryInfos);
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    //endregion


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddCategoryActivity.REQUESTCODE && resultCode == RESULT_OK) {
            CategoryInfo info = new CategoryInfo();
            info.setId(data.getStringExtra("id"));
            info.setName(data.getStringExtra("name"));
            bindCategory(info);
        }
    }

    //删除列表子项事件
    @Override
    public void onDeleteItem(final int position, CategoryInfo categoryInfo) {
        String cardNo = MerchantCardManager.getInstance().queryCollectionAccount().getCardNo();
        deleteBindCategoryRequest(cardNo, categoryInfo.getId(), new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    mData.remove(position);
                    categoryAdapter.notifyItemRemoved(position);
                }
            }
        });
    }

    //region 功能函数
    //绑定品种
    private void bindCategory(final CategoryInfo categoryInfo){
        MerchantCard merchantCard = MerchantCardManager.getInstance().queryCollectionAccount();
        if (merchantCard == null){
            TipOneDialog dialog = new TipOneDialog("提示", "添加品种失败！请先设置收款账户");
            dialog.show(getSupportFragmentManager(), "categoryManager");
            return;
        }
        for (CategoryInfo item : mData) {
            if (item.getId().equals(categoryInfo.getId())){
                Toast.makeText(CategoryManagerActivity.this, "品种已绑定", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        bindCategoryRequest(merchantCard.getCardNo(), categoryInfo.getId(), new ICallback<Boolean>() {
            @Override
            public void callback(Boolean aBoolean) {
                if (aBoolean){
                    mData.add(categoryInfo);
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //endregion

    //region 网络请求
    //查询收款账户绑定品种
    private void queryMerchantCategoryList(String cardNo, final ICallback<List<CategoryInfo>> callback){
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/queryBinDing")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            List<CategoryInfo> list = new ArrayList<>();
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.size(); i++){
                                CategoryInfo categoryInfo = new CategoryInfo();
                                categoryInfo.setId(data.getJSONObject(i).getString("id"));
                                categoryInfo.setName(data.getJSONObject(i).getString("goodsname"));
                                list.add(categoryInfo);
                            }
                            if (callback != null){
                                callback.callback(list);
                            }
                        }else{
                            Toast.makeText(CategoryManagerActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(CategoryManagerActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(CategoryManagerActivity.this)
                .build()
                .post();
    }
    //绑定品种
    private void bindCategoryRequest(String cardNo, String goodsId, final ICallback<Boolean> callback){
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        params.put("goodsid", goodsId);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/binDing")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            if (callback != null){
                                callback.callback(true);
                            }
                        }else{
                            Toast.makeText(CategoryManagerActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(CategoryManagerActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(CategoryManagerActivity.this)
                .build()
                .post();
    }

    //解除品种绑定
    private void deleteBindCategoryRequest(String cardNo, String goodsId, final ICallback<Boolean> callback){
        JSONObject params = new JSONObject();
        params.put("cardcode", cardNo);
        params.put("goodsid", goodsId);
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/noBinDing")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            if (callback != null){
                                callback.callback(true);
                            }
                        }else{
                            Toast.makeText(CategoryManagerActivity.this, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(CategoryManagerActivity.this, "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(CategoryManagerActivity.this)
                .build()
                .post();
    }
    //endregion
}

package com.wufeng.commonmvc.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wufeng.commonmvc.adapter.CategoryAdapter;
import com.wufeng.commonmvc.databinding.ActivityCategoryManagerBinding;
import com.wufeng.commonmvc.dialog.TipOneDialog;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.control.SpaceItemDecoration;
import com.wufeng.latte_core.database.MerchantCard;
import com.wufeng.latte_core.database.MerchantCardManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CategoryManagerActivity extends BaseActivity<ActivityCategoryManagerBinding> {
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
    //endregion
}

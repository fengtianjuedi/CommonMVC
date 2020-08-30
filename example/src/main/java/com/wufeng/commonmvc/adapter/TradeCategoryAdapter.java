package com.wufeng.commonmvc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.entity.CardInfo;
import com.wufeng.commonmvc.entity.CategoryInfo;

import java.util.List;

public class TradeCategoryAdapter extends RecyclerView.Adapter<TradeCategoryAdapter.ViewHolder> {
    private List<CategoryInfo> mTradeCategoryList;
    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvCategoryName;
        LinearLayoutCompat llCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_categoryName);
            llCategory = itemView.findViewById(R.id.ll_category);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CategoryInfo categoryInfo);
    }

    public TradeCategoryAdapter(List<CategoryInfo> data, OnItemClickListener listener){
        mTradeCategoryList = data;
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_trade_category_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //子项点击事件
        holder.llCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(mTradeCategoryList.get(holder.getAdapterPosition()));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryInfo info = mTradeCategoryList.get(position);
        holder.tvCategoryName.setText(info.getName());
    }

    @Override
    public int getItemCount() {
        return mTradeCategoryList.size();
    }
}

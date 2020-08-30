package com.wufeng.commonmvc.adapter;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.widget.IconTextView;
import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.entity.CategoryRecordInfo;

import java.util.List;

public class CategoryRecordAdapter extends RecyclerView.Adapter<CategoryRecordAdapter.ViewHolder> {
    private List<CategoryRecordInfo> mCategoryRecordList;
    private OnItemDeleteListener onItemDeleteListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvCategoryName;
        AppCompatTextView tvCategoryPrice;
        AppCompatTextView tvCategoryNumber;
        AppCompatTextView tvCategoryAmount;
        IconTextView itvDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_categoryName);
            tvCategoryPrice = itemView.findViewById(R.id.tv_categoryPrice);
            tvCategoryNumber = itemView.findViewById(R.id.tv_categoryNumber);
            tvCategoryAmount = itemView.findViewById(R.id.tv_categoryAmount);
            itvDelete = itemView.findViewById(R.id.itv_delete);
        }
    }

    public interface OnItemDeleteListener {
        void onItemDelete(CategoryRecordInfo categoryRecordInfo);
    }

    public CategoryRecordAdapter(List<CategoryRecordInfo> data, OnItemDeleteListener listener){
        mCategoryRecordList = data;
        onItemDeleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_trade_category_record_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryRecordInfo info = mCategoryRecordList.get(holder.getAdapterPosition());
                if (onItemDeleteListener != null)
                    onItemDeleteListener.onItemDelete(info);
            }
        });
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryRecordInfo info = mCategoryRecordList.get(position);
        holder.tvCategoryName.setText(info.getGoodsName());
        holder.tvCategoryPrice.setText(info.getGoodsPrice() + "元/kg");
        holder.tvCategoryNumber.setText(info.getGoodsNumber() + "kg");
        holder.tvCategoryAmount.setText("合计：" + info.getGoodsAmount() + "元");
    }

    @Override
    public int getItemCount() {
        return mCategoryRecordList.size();
    }

}

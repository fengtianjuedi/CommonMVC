package com.wufeng.commonmvc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.widget.IconTextView;
import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.entity.CategoryInfo;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryInfo> mCategoryList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView tvCategoryName;
        IconTextView itvDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_categoryName);
            itvDelete = itemView.findViewById(R.id.itv_delete);
        }
    }

    public CategoryAdapter(List<CategoryInfo> data){
        mCategoryList = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_category_info_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //删除子项
        holder.itvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CategoryInfo categoryInfo = mCategoryList.get(position);
                mCategoryList.remove(position);
                notifyItemRemoved(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryInfo categoryInfo = mCategoryList.get(position);
        holder.tvCategoryName.setText(categoryInfo.getName());
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }


}

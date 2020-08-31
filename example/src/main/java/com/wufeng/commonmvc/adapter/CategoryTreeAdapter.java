package com.wufeng.commonmvc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.widget.IconTextView;
import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.commonmvc.entity.CategoryNode;

import java.util.List;

public class CategoryTreeAdapter extends RecyclerView.Adapter<CategoryTreeAdapter.ViewHolder> {
    private List<CategoryNode> mCategoryNodeList;
    private OnEndNodeClickListener onEndNodeClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlNode;
        IconTextView itvNodeIcon;
        AppCompatTextView tvNodeName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlNode = itemView.findViewById(R.id.rl_node);
            itvNodeIcon = itemView.findViewById(R.id.itv_nodeIcon);
            tvNodeName = itemView.findViewById(R.id.tv_nodeName);
        }
    }

    public interface OnEndNodeClickListener {
        void onEndNodeClick(CategoryInfo categoryInfo);
    }

    public CategoryTreeAdapter(List<CategoryNode> data, OnEndNodeClickListener listener){
        mCategoryNodeList = data;
        onEndNodeClickListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_category_tree_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.rlNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryNode node = mCategoryNodeList.get(holder.getAdapterPosition());
                if (node.isEndNode()){
                    if (onEndNodeClickListener != null)
                        onEndNodeClickListener.onEndNodeClick(new CategoryInfo(node.getId(), node.getName()));
                }else{
                    if (node.isLoadChild()){
                        for (int i = holder.getAdapterPosition() + 1; i < mCategoryNodeList.size(); i++){
                            CategoryNode childNode = mCategoryNodeList.get(i);
                            if (childNode.getLevel() - 1 != node.getLevel())
                                break;
                            //childNode.setEndNode();
                        }
                    }else{
                        //请求子节点，并加载
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryNode node = mCategoryNodeList.get(position);
        int indent = node.getLevel() * holder.itvNodeIcon.getWidth();
        holder.tvNodeName.setText(node.getName());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.rlNode.getLayoutParams();
        layoutParams.leftMargin = indent;
        holder.rlNode.setLayoutParams(layoutParams);
        if (node.isEndNode()){
            holder.itvNodeIcon.setVisibility(View.GONE);
        }else{
            holder.itvNodeIcon.setVisibility(View.VISIBLE);
            if (node.isExpand())
                holder.itvNodeIcon.setText("{fa-caret-down}");
            else
                holder.itvNodeIcon.setText("{fa-caret-right}");
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryNodeList.size();
    }
}

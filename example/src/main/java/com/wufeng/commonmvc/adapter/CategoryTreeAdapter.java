package com.wufeng.commonmvc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joanzapata.iconify.widget.IconTextView;
import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.commonmvc.entity.CategoryNode;
import com.wufeng.commonmvc.ui.AddCategoryActivity;
import com.wufeng.latte_core.callback.ICallback;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.net.IError;
import com.wufeng.latte_core.net.ISuccess;
import com.wufeng.latte_core.net.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryTreeAdapter extends RecyclerView.Adapter<CategoryTreeAdapter.ViewHolder> {
    private Context mContext;
    private List<CategoryNode> mCategoryNodeList;
    private OnEndNodeClickListener onEndNodeClickListener;
    private HashMap<String, List<CategoryNode>> mChildNodeMap; //节点的子节点hash表

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

    public CategoryTreeAdapter(Context context, List<CategoryNode> data, OnEndNodeClickListener listener){
        mContext = context;
        mCategoryNodeList = data;
        onEndNodeClickListener = listener;
        mChildNodeMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_category_tree_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.rlNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CategoryNode node = mCategoryNodeList.get(holder.getAdapterPosition());
                if (node.isEndNode()){
                    if (onEndNodeClickListener != null)
                        onEndNodeClickListener.onEndNodeClick(new CategoryInfo(node.getId(), node.getName()));
                }else{
                    if (mChildNodeMap.containsKey(node.getNodeId())){
                        if (node.isExpand()){
                            List<CategoryNode> deleteItems = new ArrayList<>();
                            for (int i = holder.getAdapterPosition() + 1; i < mCategoryNodeList.size(); i++){
                                CategoryNode childNode = mCategoryNodeList.get(i);
                                if (childNode.getLevel() > node.getLevel()){
                                    childNode.setExpand(false);
                                    deleteItems.add(mCategoryNodeList.get(i));
                                }else
                                    break;
                            }
                            mCategoryNodeList.removeAll(deleteItems);
                            node.setExpand(false);
                            notifyItemRangeRemoved(holder.getAdapterPosition() + 1, deleteItems.size());
                            notifyItemChanged(holder.getAdapterPosition());
                        }else{
                            List<CategoryNode> items = mChildNodeMap.get(node.getNodeId());
                            mCategoryNodeList.addAll(holder.getAdapterPosition() + 1, items);
                            node.setExpand(true);
                            notifyItemRangeChanged(holder.getAdapterPosition(), mCategoryNodeList.size() - holder.getAdapterPosition() + 1);
                        }
                    }else{
                        queryCategoryById(node, new ICallback<List<CategoryNode>>() {
                            @Override
                            public void callback(List<CategoryNode> categoryNodes) {
                                mChildNodeMap.put(node.getNodeId(), categoryNodes);
                                mCategoryNodeList.addAll(holder.getAdapterPosition() + 1, categoryNodes);
                                node.setExpand(true);
                                notifyItemRangeChanged(holder.getAdapterPosition(), mCategoryNodeList.size() - holder.getAdapterPosition() + 1);
                            }
                        });

                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryNode node = mCategoryNodeList.get(position);
        int indent = node.getLevel() * 32;
        holder.tvNodeName.setText(node.getName());
        holder.itemView.setPadding(indent, 5, 0, 5);
        if (node.isEndNode()){
            holder.itvNodeIcon.setVisibility(View.INVISIBLE);
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

    //region
    //根据品种Id查询子品种
    private void queryCategoryById(final CategoryNode parentNode, final ICallback<List<CategoryNode>> callback){
        JSONObject params = new JSONObject();
        params.put("goodsId", parentNode.getId());
        RestClient.builder()
                .url("/pgcore-pos/PosQuery/operationManagement")
                .xwwwformurlencoded("data=" + params.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        if ("0".equals(jsonObject.getString("resultCode"))){
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            List<CategoryNode> list= new ArrayList<>();
                            for (int i = 0; i < jsonArray.size(); i++){
                                CategoryNode node = new CategoryNode();
                                node.setNodeId(parentNode.getNodeId() + i);
                                node.setLevel(parentNode.getLevel() + 1);
                                node.setId(jsonArray.getJSONObject(i).getString("id"));
                                node.setName(jsonArray.getJSONObject(i).getString("goodsname"));
                                node.setEndNode("0".equals(jsonArray.getJSONObject(i).getString("lower")));
                                list.add(node);
                            }
                            if (callback != null)
                                callback.callback(list);
                        }else{
                            Toast.makeText(mContext, jsonObject.getString("resultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText((Context) ConfigManager.getInstance().getConfig(ConfigKeys.CONTEXT), "请求远程服务器失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .loading(mContext)
                .build()
                .post();
    }
    //endregion
}

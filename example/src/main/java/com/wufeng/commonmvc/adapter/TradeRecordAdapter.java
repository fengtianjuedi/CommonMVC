package com.wufeng.commonmvc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.widget.IconTextView;
import com.wufeng.commonmvc.R;
import com.wufeng.latte_core.entity.TradeRecordInfo;

import java.util.List;

public class TradeRecordAdapter extends RecyclerView.Adapter<TradeRecordAdapter.ViewHolder> {
    private List<TradeRecordInfo> mTradeRecordList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView tvOrderCode;
        AppCompatTextView tvReceivableAmount;
        AppCompatTextView tvActualAmount;
        AppCompatTextView tvTradeTime;
        IconTextView itvPrint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tv_orderCode);
            tvReceivableAmount = itemView.findViewById(R.id.tv_receivableAmount);
            tvActualAmount = itemView.findViewById(R.id.tv_actualAmount);
            tvTradeTime = itemView.findViewById(R.id.tv_tradeTime);
            itvPrint = itemView.findViewById(R.id.itv_print);
        }
    }

    public TradeRecordAdapter(List<TradeRecordInfo> list){
        mTradeRecordList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_trade_record_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itvPrint.setOnClickListener(new View.OnClickListener() { //打印
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TradeRecordInfo info = mTradeRecordList.get(holder.getAdapterPosition());
        holder.tvOrderCode.setText(info.getTradeOrderCode());
        holder.tvReceivableAmount.setText(info.getReceivableAmount());
        holder.tvActualAmount.setText(info.getActualAmount());
        holder.tvTradeTime.setText(info.getTradeTime());
    }

    @Override
    public int getItemCount() {
        return mTradeRecordList.size();
    }
}

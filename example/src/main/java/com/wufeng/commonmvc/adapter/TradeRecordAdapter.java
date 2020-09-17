package com.wufeng.commonmvc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.widget.IconTextView;
import com.wufeng.commonmvc.R;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.device.print.PrintTemplate;
import com.wufeng.latte_core.device.print.Printer;
import com.wufeng.latte_core.device.print.PrinterFactory;
import com.wufeng.latte_core.entity.TradeRecordInfo;

import java.util.List;

public class TradeRecordAdapter extends RecyclerView.Adapter<TradeRecordAdapter.ViewHolder> {
    private Context mContext;
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

    public TradeRecordAdapter(Context context, List<TradeRecordInfo> list){
        mContext = context;
        mTradeRecordList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_trade_record_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itvPrint.setOnClickListener(new View.OnClickListener() { //打印
            @Override
            public void onClick(View v) {
                Printer printer = PrinterFactory.getPrinter(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL).toString(), mContext);
                PrintTemplate printTemplate = new PrintTemplate(printer);
                printTemplate.tradeTemplate(mTradeRecordList.get(holder.getAdapterPosition()), new PrintTemplate.PrintResultCallback() {
                    @Override
                    public void result(int code, String message) {

                    }
                });
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

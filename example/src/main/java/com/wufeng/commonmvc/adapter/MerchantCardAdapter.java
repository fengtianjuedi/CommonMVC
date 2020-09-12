package com.wufeng.commonmvc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.dialog.PasswordDialog;
import com.wufeng.commonmvc.entity.CardInfo;
import com.wufeng.commonmvc.ui.BindCardActivity;
import com.wufeng.latte_core.database.MerchantCardManager;

import java.util.List;

public class MerchantCardAdapter extends RecyclerView.Adapter<MerchantCardAdapter.ViewHolder> {
    private List<CardInfo> mCardList;
    private CollectionAccountChangedListener collectionAccountChangedLister;
    private QueryCardBalanceListener queryCardBalanceListener;
    public interface CollectionAccountChangedListener {
        void setCollectionAccount(CardInfo cardInfo);
    }
    public interface QueryCardBalanceListener{
        void queryBalance(String cardNo);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvCollectionAccountNo;
        AppCompatTextView tvCollectionAccountName;
        AppCompatTextView tvSetCollectionAccount;
        AppCompatTextView tvDelete;
        AppCompatTextView tvQueryCardBalance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCollectionAccountNo = itemView.findViewById(R.id.tv_collectionAccountNo);
            tvCollectionAccountName = itemView.findViewById(R.id.tv_collectionAccountName);
            tvSetCollectionAccount = itemView.findViewById(R.id.tv_setCollectionAccount);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            tvQueryCardBalance = itemView.findViewById(R.id.tv_query_card_balance);
        }
    }

    public MerchantCardAdapter(List<CardInfo> cardList, CollectionAccountChangedListener lister, QueryCardBalanceListener listener){
        mCardList = cardList;
        collectionAccountChangedLister = lister;
        queryCardBalanceListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_info_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //删除子项
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CardInfo cardInfo = mCardList.get(position);
                if (!deleteBoundMerchantCard(cardInfo)){
                    Toast.makeText(parent.getContext(), "删除绑定卡失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                MerchantCardManager.getInstance().deleteByCardNo(cardInfo.getCardNo());
                mCardList.remove(cardInfo);
                notifyItemRemoved(position);
            }
        });
        holder.tvSetCollectionAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CardInfo cardInfo = mCardList.get(position);
                if (collectionAccountChangedLister != null)
                    collectionAccountChangedLister.setCollectionAccount(cardInfo);
            }
        });
        holder.tvQueryCardBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CardInfo cardInfo = mCardList.get(position);
                if (queryCardBalanceListener != null)
                    queryCardBalanceListener.queryBalance(cardInfo.getCardNo());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardInfo cardInfo = mCardList.get(position);
        holder.tvCollectionAccountNo.setText(cardInfo.getCardNo());
        holder.tvCollectionAccountName.setText(cardInfo.getName());
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    //删除已绑定商户卡
    private boolean deleteBoundMerchantCard(CardInfo cardInfo){
        return MerchantCardManager.getInstance().deleteByCardNo(cardInfo.getCardNo());
    }
}

package com.wufeng.commonmvc.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.commonmvc.entity.CategoryRecordInfo;
import com.wufeng.latte_core.util.BigDecimalUtil;

import java.math.BigDecimal;

public class AddCategoryRecordDialog extends AppCompatDialogFragment {
    private CategoryInfo categoryInfo;
    private OnAddCategoryRecordListener onAddCategoryRecordListener;
    private AppCompatEditText etPrice;
    private AppCompatEditText etNumber;
    private AppCompatEditText etAmount;

    public interface OnAddCategoryRecordListener{
        void onAddCategoryRecord(CategoryRecordInfo categoryRecordInfo);
    }

    /**
     * create dialog
     * @param categoryInfo 品种信息
     */
    public AddCategoryRecordDialog(CategoryInfo categoryInfo, OnAddCategoryRecordListener listener){
        this.categoryInfo = categoryInfo;
        onAddCategoryRecordListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TipDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCancelable(false);
        View view = inflater.inflate(R.layout.dialog_add_category_record, container);
        AppCompatTextView tvTitle = view.findViewById(R.id.tv_title);
        AppCompatTextView tvOk = view.findViewById(R.id.tv_ok);
        AppCompatTextView tvCancel = view.findViewById(R.id.tv_cancel);
        etPrice = view.findViewById(R.id.et_price);
        etNumber = view.findViewById(R.id.et_number);
        etAmount = view.findViewById(R.id.et_amount);
        tvTitle.setText(categoryInfo.getName());
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryRecordInfo recordInfo = new CategoryRecordInfo();
                recordInfo.setGoodsId(categoryInfo.getId());
                recordInfo.setGoodsName(categoryInfo.getName());
                String price = etPrice.getText().toString();
                String number = etNumber.getText().toString();
                String amount = etAmount.getText().toString();
                recordInfo.setGoodsPrice(new BigDecimal(TextUtils.isEmpty(price)?"0":price).toPlainString());
                recordInfo.setGoodsNumber(Integer.parseInt(TextUtils.isEmpty(number)?"0":number));
                recordInfo.setGoodsAmount(new BigDecimal(TextUtils.isEmpty(amount)?"0":amount).toPlainString());
                if (onAddCategoryRecordListener != null)
                    onAddCategoryRecordListener.onAddCategoryRecord(recordInfo);
                dismiss();
            }
        });
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String price = s.toString();
                String number = etNumber.getText().toString();
                etAmount.setText(String.valueOf(BigDecimalUtil.mul(TextUtils.isEmpty(price)?"0":price, TextUtils.isEmpty(number)?"0":number)));
            }
        });
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String price = etPrice.getText().toString();
                String number = s.toString();
                etAmount.setText(String.valueOf(BigDecimalUtil.mul(TextUtils.isEmpty(price)?"0":price, TextUtils.isEmpty(number)?"0":number)));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = (int) (dm.widthPixels * 0.8);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }
    }
}

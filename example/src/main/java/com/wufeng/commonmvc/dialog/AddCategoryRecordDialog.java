package com.wufeng.commonmvc.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.entity.CategoryRecordInfo;
import com.wufeng.latte_core.filter.AmountRangeInputFilter;
import com.wufeng.latte_core.filter.WeightRangeInputFilter;
import com.wufeng.latte_core.util.BigDecimalUtil;

import java.math.BigDecimal;

import static com.wufeng.commonmvc.ui.WholesaleTradeActivity.MAXAMOUNT;

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
        etPrice.setFilters(new InputFilter[]{new AmountRangeInputFilter()});
        etNumber.setFilters(new InputFilter[]{new WeightRangeInputFilter()});
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
                recordInfo.setGoodsNumber(new BigDecimal(TextUtils.isEmpty(number)?"0":number).toPlainString());
                recordInfo.setGoodsAmount(new BigDecimal(TextUtils.isEmpty(amount)?"0":amount).toPlainString());
                if (onAddCategoryRecordListener != null)
                    onAddCategoryRecordListener.onAddCategoryRecord(recordInfo);
                dismiss();
            }
        });
        etPrice.addTextChangedListener(priceTextWatcher);
        etNumber.addTextChangedListener(numberTextWatcher);
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
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //region 文本框输入限制函数
    //单价输入框
    private TextWatcher priceTextWatcher = new TextWatcher() {
        String mBeforeText;
        int mCursor;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mBeforeText = s.toString();
            mCursor = start;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            etPrice.removeTextChangedListener(priceTextWatcher);
            String price = s.toString();
            String number = etNumber.getText().toString();
            BigDecimal amount = BigDecimalUtil.mul(TextUtils.isEmpty(price)?"0":price, TextUtils.isEmpty(number)?"0":number);
            if (amount.compareTo(new BigDecimal(MAXAMOUNT)) > 0){
                etPrice.setText(mBeforeText);
                etPrice.setSelection(mCursor > mBeforeText.length()?mBeforeText.length():mCursor);
            }else
                etAmount.setText(String.valueOf(amount));
            etPrice.addTextChangedListener(priceTextWatcher);
        }
    };

    //数量输入框
    private TextWatcher numberTextWatcher = new TextWatcher() {
        String mBeforeText;
        int mCursor;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mBeforeText = s.toString();
            mCursor = start;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            etNumber.removeTextChangedListener(numberTextWatcher);
            String price = etPrice.getText().toString();
            String number = s.toString();
            BigDecimal amount = BigDecimalUtil.mul(TextUtils.isEmpty(price)?"0":price, TextUtils.isEmpty(number)?"0":number);
            if (amount.compareTo(new BigDecimal(MAXAMOUNT)) > 0){
                etNumber.setText(mBeforeText);
                etNumber.setSelection(mCursor > mBeforeText.length()?mBeforeText.length():mCursor);
            }else
                etAmount.setText(String.valueOf(amount));
            etNumber.addTextChangedListener(numberTextWatcher);
        }
    };
    //endregion
}

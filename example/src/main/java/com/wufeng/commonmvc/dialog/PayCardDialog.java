package com.wufeng.commonmvc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.wufeng.commonmvc.R;
import com.wufeng.latte_core.control.FixedEditText;

public class PayCardDialog extends AppCompatDialogFragment {
    private Context mContext;
    private OnClickListener onClickListener;
    public interface OnClickListener{
        void onOkClick(String cardNo, String password);
        void onCancelClick();
    }

    public void setOnClickListener(OnClickListener listener){
        onClickListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TipDialogStyle);
    }

    public PayCardDialog(Context context){
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCancelable(false);
        View view = inflater.inflate(R.layout.dialog_pay_card, container, false);
        final FixedEditText fetCardNo = view.findViewById(R.id.fet_cardNo);
        final FixedEditText fetPassword = view.findViewById(R.id.fet_password);
        AppCompatTextView tvOk = view.findViewById(R.id.tv_ok);
        AppCompatTextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fetCardNo.getText())){
                    Toast.makeText(mContext, "卡号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(fetPassword.getText())){
                    Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if (onClickListener != null)
                    onClickListener.onOkClick(fetCardNo.getText().toString(), fetPassword.getText().toString());
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickListener != null)
                    onClickListener.onCancelClick();
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

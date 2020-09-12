package com.wufeng.commonmvc.dialog;

import android.app.Dialog;
import android.content.ComponentName;
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
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;

import com.wufeng.commonmvc.R;
import com.wufeng.latte_core.control.FixedEditText;

public class PasswordDialog extends AppCompatDialogFragment {
    private Context mContext;
    private OnClickListener onClickListener;

    public interface OnClickListener{
        void onOkClick(String password);
        void onCancelClick();
    }

    public void setOnClickListener(OnClickListener listener){
        onClickListener = listener;
    }

    public PasswordDialog(Context context){
        mContext = context;
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
        View view = inflater.inflate(R.layout.dialog_password, container);
        AppCompatTextView tvOk = view.findViewById(R.id.tv_ok);
        AppCompatTextView tvCancel = view.findViewById(R.id.tv_cancel);
        final FixedEditText fetPassword = view.findViewById(R.id.fet_password);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = fetPassword.getText().toString();
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(mContext, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if (onClickListener != null)
                    onClickListener.onOkClick("");
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

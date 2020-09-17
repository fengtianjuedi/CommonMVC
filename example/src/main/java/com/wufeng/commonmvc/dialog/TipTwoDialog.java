package com.wufeng.commonmvc.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.wufeng.commonmvc.R;
import com.wufeng.commonmvc.adapter.TradeCategoryAdapter;

public class TipTwoDialog extends DialogFragment {
    private String mTitle;
    private String mMessage;
    private OnClickListener onClickListener;

    public interface OnClickListener{
        void onOkClick();
        void onCancelClick();
    }

    /**
     * create dialog
     * @param title 标题
     * @param message 内容
     */
    public TipTwoDialog(String title, String message){
        mTitle = title;
        mMessage = message;
    }

    public void setOnClickListener(OnClickListener listener){
        onClickListener = listener;
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
        View view = inflater.inflate(R.layout.dialog_tip_two, container);
        AppCompatTextView tv_title = view.findViewById(R.id.tv_title);
        AppCompatTextView tv_message = view.findViewById(R.id.tv_message);
        AppCompatTextView tv_ok = view.findViewById(R.id.tv_ok);
        AppCompatTextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_title.setText(mTitle);
        tv_message.setText(mMessage);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickListener != null)
                    onClickListener.onOkClick();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
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

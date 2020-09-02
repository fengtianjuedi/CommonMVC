package com.wufeng.commonmvc.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.wufeng.commonmvc.R;

public class TipOneDialog extends AppCompatDialogFragment {
    private String mTitle;
    private String mMessage;
    private OnOkClickListener onOkClickListener;

    public interface OnOkClickListener {
        void onOkClick();
    }

    /**
     * create dialog
     * @param title 标题
     * @param message 内容
     */
    public TipOneDialog(String title, String message){
        mTitle = title;
        mMessage = message;
    }

    //设置ok点击监听事件
    public void setOnOkClickListener(OnOkClickListener listener){
        onOkClickListener = listener;
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
        View view = inflater.inflate(R.layout.dialog_tip_one, container);
        AppCompatTextView tv_title = view.findViewById(R.id.tv_title);
        AppCompatTextView tv_message = view.findViewById(R.id.tv_message);
        AppCompatTextView tv_ok = view.findViewById(R.id.tv_ok);
        tv_title.setText(mTitle);
        tv_message.setText(mMessage);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onOkClickListener != null)
                    onOkClickListener.onOkClick();
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
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.35));
        }
    }
}

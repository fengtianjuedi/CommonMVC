package com.wufeng.latte_core.activity;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.wufeng.latte_core.R;
import com.wufeng.latte_core.util.StatusBarUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class BaseActivity <T extends ViewBinding> extends AppCompatActivity {
    public T mBinding;

    protected abstract void init(@Nullable Bundle savedInstanceState);

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType){
            try{
                Class<T> tClass = (Class<T>)((ParameterizedType)type).getActualTypeArguments()[0];
                Method method = tClass.getMethod("inflate", LayoutInflater.class);
                mBinding = (T)method.invoke(null, getLayoutInflater());
                if (mBinding != null)
                    setContentView(mBinding.getRoot());
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
        }
        StatusBarUtil.setWindowStatusBarColor(this, R.color.background_green);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        init(savedInstanceState);
    }
}

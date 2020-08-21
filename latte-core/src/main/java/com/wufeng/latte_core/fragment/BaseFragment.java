package com.wufeng.latte_core.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment <T extends ViewBinding> extends Fragment {
    public T mBinding;

    protected abstract void init(@Nullable Bundle savedInstanceState);

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType){
            try {
                Class<T> tClass = (Class<T>)((ParameterizedType)type).getActualTypeArguments()[0];
                Method method = tClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, Boolean.class);
                mBinding = (T)method.invoke(null, inflater, container, false);
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
        }
        View view = null;
        if (mBinding != null)
            view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(savedInstanceState);
    }
}

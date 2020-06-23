package com.wufeng.commonmvc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wufeng.commonmvc.databinding.ActivityMainBinding;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.loader.Loader;
import com.wufeng.latte_core.log.LogUtil;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @Override
    protected void init() {
        mBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loader.showLoading(MainActivity.this);
            }
        });
        mBinding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loader.stopLoading();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                call();
            else
                LogUtil.d("MainActivity", "call denied");

        }
    }

    private void call(){
        try{
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:10086"));
            startActivity(intent);
        }catch (SecurityException se){
            se.printStackTrace();
        }
    }
}

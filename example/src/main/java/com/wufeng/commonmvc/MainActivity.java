package com.wufeng.commonmvc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.wufeng.commonmvc.databinding.ActivityMainBinding;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.device.card.LiandiA8ReadCard;
import com.wufeng.latte_core.device.card.ReadCard;
import com.wufeng.latte_core.device.print.PrintTemplate;
import com.wufeng.latte_core.device.print.PrinterLiandiA8;
import com.wufeng.latte_core.util.LogUtil;

import java.util.Date;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private TimePickerView timePickerView;
    @Override
    protected void init() {
        mBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Loader.showLoading(MainActivity.this);
                //timePickerView.show();
                //RequestUtil.setMerchantAndTerminal("601100000000021", "00000021", MainActivity.this);
                //RequestUtil.checkIn("601100000000021", "00000021", MainActivity.this);
                ReadCard readCard = new LiandiA8ReadCard(getApplicationContext());
                //ReadCard readCard = new LiandiA8ReadCard(getApplicationContext());
                readCard.read(new ReadCard.ReadCardCallback() {
                    @Override
                    public void result(boolean success, String cardNo) {
                        if (success)
                            Log.d("MainActivity", "result: " + cardNo);
                    }
                });
                PrintTemplate template = new PrintTemplate(new PrinterLiandiA8(getApplicationContext()));
                template.testTemplate1(null);
            }
        });
        /*
        mBinding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loader.stopLoading();
            }
        });
        List<String> data = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"));
        mBinding.wheelView.setDataList(data);
         */
        //List<String> data = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"));
        //mBinding.wheelView.setDataList(data);
        timePickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

            }
        })
                .setType(new boolean[]{false, false, false, true, true, true})
                .setLabel("", "", "", "", "", "")
                .build();
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

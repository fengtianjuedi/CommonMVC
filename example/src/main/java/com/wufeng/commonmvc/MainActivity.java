package com.wufeng.commonmvc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.wufeng.commonmvc.databinding.ActivityMainBinding;
import com.wufeng.commonmvc.dialog.AddCategoryRecordDialog;
import com.wufeng.commonmvc.entity.CategoryInfo;
import com.wufeng.commonmvc.entity.CategoryRecordInfo;
import com.wufeng.commonmvc.ui.AddCategoryActivity;
import com.wufeng.commonmvc.ui.PaymentActivity;
import com.wufeng.latte_core.activity.BaseActivity;
import com.wufeng.latte_core.util.LogUtil;
import com.wufeng.latte_core.util.RequestUtil;
import com.wufeng.latte_core.util.UpdateUtil;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private TimePickerView timePickerView;
    UpdateUtil updateUtil;
    Map<String, Object> map;
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        updateUtil = new UpdateUtil(this, this);
        map = new HashMap<>();
        map.put("downloadUrl", "https://runtong-test.oss-cn-shanghai.aliyuncs.com/RTAgri_Update/test/example-baidu-release.apk");
        map.put("isForceUpgrade", true);
        map.put("title", "测试更新功能");
        map.put("content", "安装测试Apk");
        mBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Loader.showLoading(MainActivity.this);
                //timePickerView.show();
                //RequestUtil.setMerchantAndTerminal("601100000000021", "00000022", MainActivity.this);
                //RequestUtil.checkIn("601100000000021", "00000022", MainActivity.this);
                //ReadCard readCard = new LiandiA8ReadCard(getApplicationContext());
                //ReadCard readCard = new LiandiA8ReadCard(getApplicationContext());
                /*
                readCard.read(new ReadCard.ReadCardCallback() {
                    @Override
                    public void result(boolean success, String cardNo) {
                        if (success)
                            Log.d("MainActivity", "result: " + cardNo);
                    }
                });
                PrintTemplate template = new PrintTemplate(new PrinterLiandiA8(getApplicationContext()));
                template.testTemplate1(null);
                 */
                //Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                //startActivity(intent);
                //TipTwoDialog tipDialog = new TipTwoDialog("提示", "签到成功!");
                //tipDialog.show(getSupportFragmentManager(), "tipdialog");
                /*
                CategoryInfo info = new CategoryInfo();
                info.setId("123");
                info.setName("山竹");
                AddCategoryRecordDialog addCategoryRecordDialog = new AddCategoryRecordDialog(info, new AddCategoryRecordDialog.OnAddCategoryRecordListener() {
                    @Override
                    public void onAddCategoryRecord(CategoryRecordInfo categoryRecordInfo) {
                        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    }
                });
                addCategoryRecordDialog.show(getSupportFragmentManager(), "addCategoryRecordDialog");
                */
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示")
                        .setMessage("提示")
                        .setPositiveButton("好的", null)
                        .show();
                 */
                updateUtil.checkUpdate(map);
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
                updateUtil.checkUpdate(map);
            else
                Toast.makeText(this, "你拒绝了这个权限", Toast.LENGTH_SHORT).show();

        }
    }

    private void call(){
        try{
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:10086"));
            //startActivity(intent);
        }catch (SecurityException se){
            se.printStackTrace();
        }
    }
}

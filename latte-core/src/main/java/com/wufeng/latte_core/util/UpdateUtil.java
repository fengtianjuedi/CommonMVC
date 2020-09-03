package com.wufeng.latte_core.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.wufeng.latte_core.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class UpdateUtil {
    private FragmentActivity fragmentActivity;
    private Context mContext;

    private Dialog noticeDialog;

    private Dialog downloadDialog;

    private String mSavePath ="";
    private String saveFileName = "wKhuZF37SamAQo2LAIC42cKRa2M085.apk";
    private static String apkUrl = "";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private AppCompatTextView tvProgressBar;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private static final int DOWNLOAD_FAILED = 3;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private static boolean isForceUpgrade = false;

    private Handler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler{
        WeakReference<UpdateUtil> mWeakReference;

        MyHandler(UpdateUtil updateManager){
            mWeakReference = new WeakReference<>(updateManager);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateUtil updateManager = mWeakReference.get();
            if (updateManager == null)
                return;
            switch (msg.what) {
                case DOWN_UPDATE:
                    updateManager.mProgress.setProgress(updateManager.progress);
                    updateManager.tvProgressBar.setText(updateManager.progress+"%");
                    break;
                case DOWN_OVER:
                    updateManager.installApk();
                    break;
                case DOWNLOAD_FAILED:
                    Toast.makeText(updateManager.mContext, "网络断开，请稍候再试", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    public UpdateUtil(FragmentActivity fragmentActivity, Context context) {
        this.fragmentActivity = fragmentActivity;
        this.mContext = context;
    }

    //外部接口让主Activity调用
    public void checkUpdateInfo(Map map){
        int permission = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission == PackageManager.PERMISSION_GRANTED){
            showNoticeDialog(map);
        }else{
            Toast.makeText(mContext, "已检查到最新版本,在线升级，请点击允许按钮，否则禁止升级", Toast.LENGTH_LONG).show();
        }
    }

    //显示更新提示
    private void showNoticeDialog(Map map){
        apkUrl = String.valueOf(map.get("downloadUrl"));
        LogUtil.d("", "apkUrl："+apkUrl);
        isForceUpgrade = (boolean) map.get("isForceUpgrade");
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
        builder.setTitle("检查到新版本");
        builder.setMessage(map.get("title")+"\n\n"+map.get("content")+"");
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        if(!isForceUpgrade) {
            builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
    }

    //显示下载进度条
    private void showDownloadDialog(){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("正在更新版本,请稍后...");

            final LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.progress, null);
            mProgress = v.findViewById(R.id.progress);
            tvProgressBar = v.findViewById(R.id.tv_progressBar);
            builder.setView(v);
            if(!isForceUpgrade) {
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        interceptFlag = true;
                    }
                });
            }
            downloadDialog = builder.create();
            downloadDialog.setCanceledOnTouchOutside(false);
            downloadDialog.show();
        }catch (Exception ex){
            //Toast.makeText(fragmentActivity.getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                LogUtil.d("","apkUrl："+apkUrl);
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                conn.setConnectTimeout(4000);
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                // 获得存储卡的路径
                mSavePath = mContext.getExternalFilesDir(null) + "/" + "download";
                File file = new File(mSavePath);
                if(!file.exists()){
                    file.mkdir();
                }
                File ApkFile = new File(mSavePath,saveFileName);
//                ApkFile.setWritable(Boolean.TRUE);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numRead = is.read(buf);
                    count += numRead;
                    progress =(int)(((float)count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numRead <= 0){
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf,0,numRead);
                }while(!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
                // 取消下载对话框显示
                downloadDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
            }
        }
    };

    /**
     * 下载apk
     */
    private void downloadApk(){
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    /**
     * 安装apk
     */
    private void installApk(){
        File apkfile = new File(mSavePath,saveFileName);
        if (!apkfile.exists()) {
            return;
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;
    public void checkUpdate(Map map) {
        try {
            if(isNetworkAvailable(mContext)) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    checkUpdateInfo(map);
                } else {
                    ActivityCompat.requestPermissions(fragmentActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            EXTERNAL_STORAGE_REQ_CODE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check the Network is available
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null)return false;
            if(Build.VERSION.SDK_INT<23){
                NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
                return (netWorkInfo != null && netWorkInfo.isAvailable());//检测网络是否可用
            }else{
                Network netWork = connectivityManager.getActiveNetwork();
                if (netWork != null){
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(netWork);
                    return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                }
                return false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}

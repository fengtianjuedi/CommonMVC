package com.wufeng.latte_core.card;

import android.app.Activity;
import android.content.Context;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;
import com.vanstone.trans.api.PiccApi;
import com.vanstone.trans.api.SystemApi;
import com.vanstone.utils.CommonConvert;
import com.wufeng.latte_core.common.CommonUtils;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.config.PosModel;

public class M1Service {
    /**
     * M1卡操作结果监听
     */
    public interface OnResultListener{
        void success(final String data);
        void fail(final String message);
    }

    private Context context;
    private OnResultListener onResultListener;
    private RFCardReaderSample rfCardReaderSample; //联迪A8读卡对象
    private CommonUtils commonUtils;
    private static android.os.Handler mainHandler = new android.os.Handler();;
    private boolean isFindCard = false; //是否正在寻卡

    public M1Service(Context context, OnResultListener listener){
        this.context = context;
        this.onResultListener = listener;
        commonUtils = new CommonUtils((Activity) context);
    }

    /**
     * 绑定服务
     */
    public void bindService(){
        try{
            if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.LIANDIA8){
                DeviceService.login(this.context);
                rfCardReaderSample = new RFCardReaderSample(this.context) {
                    @Override
                    protected void displayRFCardInfo(String cardInfo) {
                        commonUtils.startMediaPlayerDi();
                        onResultListener.success(cardInfo);
                        isFindCard = false;
                    }

                    @Override
                    protected void onDeviceServiceCrash() {
                        onResultListener.fail("读卡失败");
                        isFindCard = false;
                    }
                };
            }else if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.AISINOA90){
                int result = PiccApi.PiccOpen_Api();
                if (result != 0)
                    onResultListener.fail("打开设备失败" + String.valueOf(result));
            }
        }catch (ServiceOccupiedException | ReloginException | UnsupportMultiProcess | RequestException ex){
            this.onResultListener.fail(ex.getMessage());
        }
    }

    /**
     * 解绑服务
     */
    public void unbindService(){
        isFindCard = false;
        if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.LIANDIA8){
            if (rfCardReaderSample != null)
                rfCardReaderSample.stopSearch();
            DeviceService.logout();
        }else if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.AISINOA90){
            PiccApi.PiccClose_Api();
        }
    }

    /**
     * 读卡
     */
    public void readCard(){
        if (isFindCard)
            return;
        isFindCard = true;
        if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.LIANDIA8){
            if (rfCardReaderSample != null)
                rfCardReaderSample.searchCard();
        }else if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.AISINOA90){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte []serialNo = new byte[16];
                    byte []cardType = new byte[2];
                    byte[] keyA = CommonConvert.hexStringToByte("B192C384D576");
                    byte[] data = new byte[16];
                    int result = 0;
                    final String error;
                    try{
                        while (isFindCard){
                            result = PiccApi.PiccCheck_Api('m', cardType, serialNo);
                            if (result == 0)
                                break;
                            Thread.sleep(500);
                        }
                    }catch (InterruptedException ex){
                        error = "寻卡异常，错误码：" + result;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.fail(error);
                            }
                        });
                        isFindCard = false;
                        return;
                    }
                    if (!isFindCard)
                        return;
                    else
                        isFindCard = false;
                    result = PiccApi.M1Authority_Api('A', 5, keyA);
                    if (result != 0){
                        error = "扇区认证失败, 错误码：" + result;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.fail(error);
                            }
                        });
                        return;
                    }
                    result = PiccApi.M1ReadBlock_Api(4, data);
                    if (result != 0){
                        error = "扇区读取失败, 错误码：" + result;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.fail(error);
                            }
                        });
                        return;
                    }
                    SystemApi.Beef_Api(4, 300);
                    final String info = CommonConvert.bytes2HexString(data);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.success(info);
                        }
                    });
                }
            }).start();
        }
    }
}

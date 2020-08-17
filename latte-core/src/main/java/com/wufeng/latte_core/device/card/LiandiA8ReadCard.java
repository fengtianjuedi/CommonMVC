package com.wufeng.latte_core.device.card;

import android.content.Context;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.card.MifareDriver;
import com.landicorp.android.eptapi.card.RFDriver;
import com.landicorp.android.eptapi.device.Beeper;
import com.landicorp.android.eptapi.device.RFCardReader;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;
import com.landicorp.android.eptapi.utils.BytesBuffer;
import com.wufeng.latte_core.util.ByteStringUtil;

/**
 * 联迪A8非接触IC卡读写
 */
public class LiandiA8ReadCard extends ReadCard {
    //设备型号
    public static final String LIANDIA8 = "LIANDIA8";
    // 卡驱动名
    private static final String DRIVER_NAME_PRO = "PRO";
    private static final String DRIVER_NAME_S50 = "S50";
    private static final String DRIVER_NAME_S70 = "S70";
    private static final String DRIVER_NAME_CPU = "CPU";

    private Context mContext;
    private RFCardReader reader = RFCardReader.getInstance();
    private MifareDriver driver;
    private String driverName;

    public LiandiA8ReadCard(Context context){
        mContext = context;
    }

    @Override
    public void read(final ReadCardCallback callback){
        try{
            DeviceService.login(mContext);
            reader.searchCard(new RFCardReader.OnSearchListener() {
                @Override
                public void onCardPass(int i) {
                    setDriverName(i);
                    if (driverName == null || (!DRIVER_NAME_S50.equals(driverName) && !DRIVER_NAME_S70.equals(driverName))){
                        if (callback != null)
                            callback.result(false, "不支持的卡类型");
                        DeviceService.logout();
                        return;
                    }
                    try{
                        reader.activate(driverName, new RFCardReader.OnActiveListener() {
                            @Override
                            public void onCardActivate(RFDriver rfDriver) {
                                try {
                                    driver = (MifareDriver) rfDriver;
                                    byte[] key = ByteStringUtil.hex2byte(ReadCard.KeyA);
                                    int result = 0xff;
                                    result = driver.authSector(1, 1, key);
                                    if (result != 0){
                                        if (callback != null)
                                            callback.result(false, "扇区认证失败");
                                        DeviceService.logout();
                                        return;
                                    }
                                    BytesBuffer bytesBuffer = new BytesBuffer();
                                    result = driver.readBlock(4, bytesBuffer);
                                    if (result != 0){
                                        if (callback != null)
                                            callback.result(false, "读取数据失败");
                                        DeviceService.logout();
                                        return;
                                    }
                                    Beeper.startBeep(300);
                                    if (callback != null)
                                        callback.result(true, bytesBuffer.toHexString().substring(0, 20));
                                }catch (RequestException re){
                                    if (callback != null)
                                        callback.result(false, re.getMessage());
                                }
                                DeviceService.logout();
                            }

                            @Override
                            public void onActivateError(int i) {
                                if (callback != null)
                                    callback.result(false, "卡激活失败");
                                DeviceService.logout();
                            }

                            @Override
                            public void onUnsupport(String s) {
                                if (callback != null)
                                    callback.result(false, "卡激活失败");
                                DeviceService.logout();
                            }

                            @Override
                            public void onCrash() {
                                if (callback != null)
                                    callback.result(false, "卡激活失败");
                                DeviceService.logout();
                            }
                        });
                    }catch (RequestException re) {
                        if (callback != null)
                            callback.result(false, re.getMessage());
                        DeviceService.logout();
                    }
                }

                @Override
                public void onFail(int i) {
                    if (callback != null)
                        callback.result(false, "寻卡失败");
                    DeviceService.logout();
                }

                @Override
                public void onCrash() {
                    if (callback != null)
                        callback.result(false, "寻卡失败");
                    DeviceService.logout();
                }
            });
        }catch (ServiceOccupiedException | ReloginException | RequestException | UnsupportMultiProcess ex) {
            if (callback != null)
                callback.result(false, ex.getMessage());
            DeviceService.logout();
        }
    }

    @Override
    public void write(final String data, final WriteCardCallback callback) {
        try{
            DeviceService.login(mContext);
            reader.searchCard(new RFCardReader.OnSearchListener() {
                @Override
                public void onCardPass(int i) {
                    setDriverName(i);
                    if (driverName == null || (!DRIVER_NAME_S50.equals(driverName) && !DRIVER_NAME_S70.equals(driverName))){
                        if (callback != null)
                            callback.result(false, "不支持的卡类型");
                        DeviceService.logout();
                        return;
                    }
                    try{
                        reader.activate(driverName, new RFCardReader.OnActiveListener() {
                            @Override
                            public void onCardActivate(RFDriver rfDriver) {
                                try {
                                    driver = (MifareDriver) rfDriver;
                                    byte[] key = ByteStringUtil.hex2byte(ReadCard.KeyB);
                                    int result = 0xff;
                                    result = driver.authSector(1, 2, key);
                                    if (result != 0){
                                        if (callback != null)
                                            callback.result(false, "扇区认证失败");
                                        DeviceService.logout();
                                        return;
                                    }
                                    byte[] content = ByteStringUtil.hex2byte(data);
                                    result = driver.writeBlock(4, content);
                                    if (result != 0){
                                        if (callback != null)
                                            callback.result(false, "数据写入失败");
                                        DeviceService.logout();
                                        return;
                                    }
                                    if (callback != null)
                                        callback.result(true, "");
                                }catch (RequestException re){
                                    if (callback != null)
                                        callback.result(false, re.getMessage());
                                }
                                DeviceService.logout();
                            }

                            @Override
                            public void onActivateError(int i) {
                                if (callback != null)
                                    callback.result(false, "卡激活失败");
                                DeviceService.logout();
                            }

                            @Override
                            public void onUnsupport(String s) {
                                if (callback != null)
                                    callback.result(false, "卡激活失败");
                                DeviceService.logout();
                            }

                            @Override
                            public void onCrash() {
                                if (callback != null)
                                    callback.result(false, "卡激活失败");
                                DeviceService.logout();
                            }
                        });
                    }catch (RequestException re) {
                        if (callback != null)
                            callback.result(false, re.getMessage());
                        DeviceService.logout();
                    }
                }

                @Override
                public void onFail(int i) {
                    if (callback != null)
                        callback.result(false, "寻卡失败");
                    DeviceService.logout();
                }

                @Override
                public void onCrash() {
                    if (callback != null)
                        callback.result(false, "寻卡失败");
                    DeviceService.logout();
                }
            });
        }catch (ServiceOccupiedException | ReloginException | RequestException | UnsupportMultiProcess ex) {
            if (callback != null)
                callback.result(false, ex.getMessage());
            DeviceService.logout();
        }
    }

    //设置驱动名称
    private void setDriverName(int cardType){
        switch (cardType){
            case RFCardReader.OnSearchListener.S50_CARD:
            case RFCardReader.OnSearchListener.S50_PRO_CARD:
                driverName = DRIVER_NAME_S50;
                break;
            case RFCardReader.OnSearchListener.S70_CARD:
            case RFCardReader.OnSearchListener.S70_PRO_CARD:
                driverName = DRIVER_NAME_S70;
                break;
            case RFCardReader.OnSearchListener.CPU_CARD:
                driverName = DRIVER_NAME_CPU;
                break;
            case RFCardReader.OnSearchListener.PRO_CARD:
                driverName = DRIVER_NAME_PRO;
                break;
            default:
                driverName = DRIVER_NAME_PRO;
                break;
        }
    }
}

package com.wufeng.latte_core.device.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;
import com.landicorp.android.eptapi.utils.ImageTransformer;
import com.landicorp.android.eptapi.utils.QrCode;
import com.wufeng.latte_core.util.ImageUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PrinterLiandiA8 extends com.wufeng.latte_core.device.print.Printer {
    private Context mContext;
    private List<Printer.Step> stepList;
    private Printer.Progress progress;

    public PrinterLiandiA8(Context context){
        mContext = context;
        stepList = new ArrayList<>();
    }

    @Override
    public void init() {
       bindService();
       stepList.add(new Printer.Step() {
           @Override
           public void doPrint(Printer printer) throws Exception {
               printer.setAutoTrunc(true);
           }
       });
    }

    @Override
    public void startPrint(final PrintEndCallback printEndCallback) {
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                Printer.Format format = new Printer.Format();
                format.setAscSize(Printer.Format.ASC_DOT5x7);
                format.setAscScale(Printer.Format.ASC_SC1x1);
                printer.setFormat(format);
            }

            @Override
            public void onFinish(int i) {
                unbindService();
                if (printEndCallback != null)
                    printEndCallback.result(i, getErrorDescription(i));
            }

            @Override
            public void onCrash() {
                unbindService();
                if (printEndCallback != null)
                    printEndCallback.result(-1, "打印崩溃");
            }
        };
        for(Printer.Step step : stepList){
            progress.addStep(step);
        }
        try {
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
            if (printEndCallback != null)
                printEndCallback.result(-1, e.getMessage());
        }
    }

    @Override
    public void printStr(final int align, final String text){
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                switch (align){
                    case com.wufeng.latte_core.device.print.Printer.AlignLeft:
                        printer.printText(Printer.Alignment.LEFT, text + "\n");
                        break;
                    case com.wufeng.latte_core.device.print.Printer.AlignCenter:
                        printer.printText(Printer.Alignment.CENTER, text + "\n");
                        break;
                    case com.wufeng.latte_core.device.print.Printer.AlignRight:
                        printer.printText(Printer.Alignment.RIGHT, text + "\n");
                        break;
                }
            }
        });
    }

    @Override
    public void printBarCode(final int align, final int width, final int height, final String text){
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                switch (align){
                    case com.wufeng.latte_core.device.print.Printer.AlignLeft:
                        printer.printBarCode(Printer.Alignment.LEFT, text);
                        break;
                    case com.wufeng.latte_core.device.print.Printer.AlignCenter:
                        printer.printBarCode(Printer.Alignment.CENTER, text);
                        break;
                    case com.wufeng.latte_core.device.print.Printer.AlignRight:
                        printer.printBarCode(Printer.Alignment.RIGHT, text);
                        break;
                }
            }
        });
    }

    @Override
    public void printQrCode(final int align, final int height, final String text){
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                switch (align){
                    case com.wufeng.latte_core.device.print.Printer.AlignLeft:
                        printer.printQrCode(Printer.Alignment.LEFT, new QrCode(text, QrCode.ECLEVEL_Q), height);
                        break;
                    case com.wufeng.latte_core.device.print.Printer.AlignCenter:
                        printer.printQrCode(Printer.Alignment.CENTER, new QrCode(text, QrCode.ECLEVEL_Q), height);
                        break;
                    case com.wufeng.latte_core.device.print.Printer.AlignRight:
                        printer.printQrCode(Printer.Alignment.RIGHT, new QrCode(text, QrCode.ECLEVEL_Q), height);
                        break;
                }
            }
        });
    }

    @Override
    public void printImage(final int resourceId){
        stepList.add(new Printer.Step() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                InputStream inputStream = mContext.getResources().openRawResource(resourceId);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                final int MAX_WIDTH = getPrinterWidth();
                if (bitmap.getWidth() > MAX_WIDTH) {
                    bitmap = ImageUtil.zoomImg(bitmap, 0, MAX_WIDTH);
                    if (bitmap == null) {
                        return;
                    }
                }
                ByteArrayOutputStream outputStream = ImageTransformer.convert1BitBmp(bitmap);
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                printer.printImage(com.landicorp.android.eptapi.device.Printer.Alignment.LEFT, inputStream);
                // 若是打印大位图，需使用printer.printMonochromeBmp接口
                inputStream.close();
                outputStream.close();
            }
        });
    }

    /**
     * 打印错误描述
     * @param code 错误码
     * @return 错误信息
     */
    @Override
    public String getErrorDescription(int code) {
        switch (code) {
            case Printer.ERROR_PAPERENDED:
                return "缺纸，装纸后使用 [重打印] 功能打印小票";//"Paper-out, the operation is invalid this time";
            case Printer.ERROR_HARDERR:
                return "打印机故障";//"Hardware fault, can not find HP signal";
            case Printer.ERROR_OVERHEAT:
                return "Overheat";
            case Printer.ERROR_BUFOVERFLOW:
                return "The operation buffer mode position is out of range";
            case Printer.ERROR_LOWVOL:
                return "Low voltage protect";
            case Printer.ERROR_PAPERENDING:
                return "Paper-out, permit the latter operation";
            case Printer.ERROR_MOTORERR:
                return "The printer core fault (too fast or too slow)";
            case Printer.ERROR_PENOFOUND:
                return "Automatic positioning did not find the alignment position, the paper back to its original position";
            case Printer.ERROR_PAPERJAM:
                return "paper got jammed";
            case Printer.ERROR_NOBM:
                return "Black mark not found";
            case Printer.ERROR_BUSY:
                return "The printer is busy";
            case Printer.ERROR_BMBLACK:
                return "Black label detection to black signal";
            case Printer.ERROR_WORKON:
                return "The printer power is open";
            case Printer.ERROR_LIFTHEAD:
                return "Printer head lift";
            case Printer.ERROR_LOWTEMP:
                return "Low temperature protect";
        }
        return "unknown error (" + code + ")";
    }

    /**
     * 绑定打印服务
     */
    private void bindService(){
        try{
            DeviceService.login(mContext);
        }catch (ServiceOccupiedException | ReloginException | UnsupportMultiProcess | RequestException e){
            e.printStackTrace();
        }
    }

    /**
     * 解绑打印服务
     */
    private void unbindService(){
        DeviceService.logout();
    }

    /**
     * 获取打印纸宽度
     */
    private int getPrinterWidth() {
        int width = Printer.getInstance().getValidWidth();
        if (width <= 0) {
            return 384;
        }
        return width;
    }
}

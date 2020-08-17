package com.wufeng.latte_core.device.print;

import android.content.Context;
import android.widget.Toast;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;

public class PrinterLiandiA8 {
    public static final String LIANDIA8 = "LIANDIA8";
    public interface PrinterLiandiA8Listener{
        void data(Printer printer) throws Exception;
        void Success();
        void Error(String message);
    }
    private Context mContext;
    private PrinterLiandiA8Listener printerLiandiA8Listener;
    private Printer.Progress progress = new Printer.Progress() {
        @Override
        public void doPrint(Printer printer) throws Exception {
            Printer.Format format = new Printer.Format();
            format.setAscSize(Printer.Format.ASC_DOT5x7);
            format.setAscScale(Printer.Format.ASC_SC1x1);
            printer.setFormat(format);
            if (printerLiandiA8Listener != null)
                printerLiandiA8Listener.data(printer);
            printer.feedLine(2);
        }

        @Override
        public void onCrash() {
            if (printerLiandiA8Listener != null)
                printerLiandiA8Listener.Error("打印崩溃，请重试");
        }

        @Override
        public void onFinish(int code) {
            if (code == Printer.ERROR_NONE) {
                if (printerLiandiA8Listener != null)
                    printerLiandiA8Listener.Success();
            }
            else {
                if (printerLiandiA8Listener != null)
                    printerLiandiA8Listener.Error(getErrorDescription(code));
            }
            unbindService();
        }
    };

    public PrinterLiandiA8(Context context){
        mContext = context;
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
     * 打印
     * @param listener 打印回调接口
     */
    public void print(PrinterLiandiA8Listener listener){
        try {
            bindService();
            printerLiandiA8Listener = listener;
            progress.addStep(new Printer.Step() {
                @Override
                public void doPrint(Printer printer) throws Exception {
                    // Make the print method can print more than one line.
                    printer.setAutoTrunc(true);
                }
            });
            progress.start();
        } catch (RequestException e) {
            if (printerLiandiA8Listener != null)
                printerLiandiA8Listener.Error(e.getMessage());
        }
    }

    /**
     * 打印错误描述
     * @param code 错误码
     * @return 错误信息
     */
    private String getErrorDescription(int code) {
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
}

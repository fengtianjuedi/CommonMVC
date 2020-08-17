package com.wufeng.latte_core.device.card;

import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;

public abstract class ReadCard {
    static final String KeyA = "B192C384D576";
    static final String KeyB = "9AB35CD67EF3";
    static final String Control = "08778F69";

    public abstract void read(ReadCardCallback callback);
    public abstract void write(String data, WriteCardCallback callback);

    public interface ReadCardCallback {
        void result(boolean success, String cardNo);
    }

    public interface WriteCardCallback {
        void result(boolean success, String error);
    }
}

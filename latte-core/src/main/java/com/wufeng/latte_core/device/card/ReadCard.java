package com.wufeng.latte_core.device.card;

public abstract class ReadCard {
    static final String KeyA = "B192C384D576";
    static final String KeyB = "9AB35CD67EF3";
    static final String Control = "08778F69";

    public abstract void read(ReadCardCallback callback); //读卡
    public abstract void write(String data, WriteCardCallback callback); //写卡
    public abstract void stop(); //停止寻卡

    public interface ReadCardCallback {
        void result(boolean success, String cardNo);
    }

    public interface WriteCardCallback {
        void result(boolean success, String error);
    }
}

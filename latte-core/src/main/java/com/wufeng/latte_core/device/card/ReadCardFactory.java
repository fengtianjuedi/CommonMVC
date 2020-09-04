package com.wufeng.latte_core.device.card;

import android.content.Context;

import com.wufeng.latte_core.device.PosDevice;

public class ReadCardFactory {
    public static ReadCard getReadCard(String model, Context context){
        if (PosDevice.AISINOA90.equals(model))
            return new AISINOA90ReadCard();
        else if (PosDevice.LIANDIA8.equals(model))
            return new LiandiA8ReadCard(context);
        else
            return null;
    }
}

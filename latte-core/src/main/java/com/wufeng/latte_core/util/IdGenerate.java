package com.wufeng.latte_core.util;

import java.util.concurrent.atomic.AtomicLong;

//唯一ID自增生成器
public class IdGenerate {
    private AtomicLong id = new AtomicLong();

    private static class Holder {
        private static IdGenerate INSTANCE = new IdGenerate();
    }

    public static IdGenerate getInstance(){return Holder.INSTANCE;}

    public String getId(){
        id.incrementAndGet();
        if (id.get() >= 10)
            id.set(0);
        return TimeUtil.currentDateYMDHMSS() + id.get();
    }
}

package com.wufeng.latte_core.icon;

import com.joanzapata.iconify.Icon;

public enum IconFontIcons implements Icon {
    icon_tradeRecord('\ue699'); //交易记录

    private char charater;

    IconFontIcons(char charater) {
        this.charater = charater;
    }

    @Override
    public String key() {
        return name().replace('_', '-');
    }

    @Override
    public char character() {
        return this.charater;
    }
}

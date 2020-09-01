package com.wufeng.latte_core.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Terminal_info")
public class TerminalInfo {
    @Id(autoincrement = true)
    private Long id;
    private String merchantCode; //商户号
    private String terminalCode; //终端号
    private String masterKey; //主密钥
    @Generated(hash = 722910595)
    public TerminalInfo(Long id, String merchantCode, String terminalCode,
            String masterKey) {
        this.id = id;
        this.merchantCode = merchantCode;
        this.terminalCode = terminalCode;
        this.masterKey = masterKey;
    }
    @Generated(hash = 1002466036)
    public TerminalInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMerchantCode() {
        return this.merchantCode;
    }
    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }
    public String getTerminalCode() {
        return this.terminalCode;
    }
    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }
    public String getMasterKey() {
        return this.masterKey;
    }
    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }
}

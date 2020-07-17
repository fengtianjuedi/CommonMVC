package com.wufeng.latte_core.card;

import com.landicorp.android.eptapi.card.RFCpuCardDriver;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.BytesUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UP card is a kind of mobile phone chip card.
 * The sample code is that we use in real project.
 *
 * @author chenwei
 *
 */
public abstract class UPCardReader extends RFCpuCardDriver.OnExchangeListener {
    private RFCpuCardDriver driver;
    private ResponseHandler respHandler;
    private int CARD_SW1;
    private int CARD_SW2;
    private byte[] serialNo;
    @SuppressWarnings("unused")
    private String cardName;
    private String pan;
    private String track2;
    private String track3;
    private String expiredDate;

    public UPCardReader(RFCpuCardDriver driver) {
        this.driver = driver;
    }

    @Override
    public void onFail(int errorCode) {
        showErrorMessage("UP CARD READ FAIL - "+getErrorDescription(errorCode));
    }

    @Override
    protected boolean checkResult(int result) {
        return super.checkResult(result);
    }

    public String getErrorDescription(int code){
        switch(code){
            case ERROR_ERRPARAM : return "Parameter error";
            case ERROR_FAILED : return "Other error(OS error,etc)";
            case ERROR_NOTAGERR : return "Operating range without card or card is not responding";
            case ERROR_CRCERR : return "The data CRC parity error";
            case ERROR_AUTHERR : return "Authentication failed";
            case ERROR_PARITYERR : return "Data parity error";
            case ERROR_CODEERR : return "The wrong card response data content";
            case ERROR_SERNRERR : return "Data in the process of conflict protection error";
            case ERROR_NOTAUTHERR : return "Card not authentication";
            case ERROR_BITCOUNTERR : return "The length of data bits card return is wrong";
            case ERROR_BYTECOUNTERR : return "The length of data bytes card return is wrong";
            case ERROR_OVFLERR : return "The card return data overflow";
            case ERROR_FRAMINGERR : return "Data frame error";
            case ERROR_UNKNOWN_COMMAND : return "The terminal sends illegal command";
            case ERROR_COLLERR : return "Multiple cards conflict";
            case ERROR_RESETERR : return "RF card module reset failed";
            case ERROR_INTERFACEERR : return "RF card module interface error";
            case ERROR_RECBUF_OVERFLOW : return "Receive buffer overflow";
            case ERROR_VALERR : return "Numerical block operation on the Mifare card, block error";
            case ERROR_ERRTYPE : return "Card type of error";
            case ERROR_SWDIFF : return "Data exchange with MifarePro card or TypeB card, card loopback status byte SW1! = 0x90, =0x00 SW2.";
            case ERROR_TRANSERR : return "Communication error";
            case ERROR_PROTERR : return "The card return data does not meet the requirements of the protocal";
            case ERROR_MULTIERR : return "There are multiple cards in the induction zone";
            case ERROR_NOCARD : return "There is no card in the induction zone";
            case ERROR_CARDEXIST : return "The card is still in the induction zone";
            case ERROR_CARDTIMEOUT : return "Response timeout";
            case ERROR_CARDNOACT : return "Pro card or TypeB card is not activated";
        }
        return "unknown error ["+code+"]";
    }

    @Override
    public void onSuccess(byte[] responseData) {
        //Save the sw1 and sw2 after current exchange.
        if(respHandler != null) {
            CARD_SW1 = responseData[responseData.length-2]&0xff;
            CARD_SW2 = responseData[responseData.length-1]&0xff;
            respHandler.onResponse(responseData);
        }
    }

    @Override
    public void onCrash() {
        onServiceCrash();
    }

    public void startRead() {
        detect(new NextStep() {
            @Override
            public void invoke() {
                readSerialNo(new NextStep(){
                    @Override
                    public void invoke() {
                        readCardId(new NextStep(){
                            @Override
                            public void invoke() {
                                readCardInfo(new NextStep(){
                                    @Override
                                    public void invoke() {
                                        try {
                                            driver.halt();
                                        } catch (RequestException e) {
                                            onServiceCrash();
                                        }
                                    }});
                            }

                        });
                    }
                });
            }
        });
    }

    /**
     * Detect if the card is UP card or not.
     * @param next
     */
    protected void detect(final NextStep next) {
        exechangeApdu("00A404000FA000000333"+extendString("CUP-MOBILE"), new ResponseHandler() {
            @Override
            public void onResponse(byte[] responseData) {
                if (CARD_SW1 == 0x61 || CARD_SW1 == 0x9f || CARD_SW1 == 0x90 && CARD_SW2 == 0x00) {
                    next.invoke();
                }
                else {
                    showErrorMessage("The card you insert is not a UP card!");
                }
            }
        });
    }

    private String extendString(String str) {
        try {
            byte[] gbk = str.getBytes("GBK");
            return BytesUtil.bytes2HexString(gbk);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * Read serial no from card.
     * @param next
     */
    protected void readSerialNo(final NextStep next) {
        exechangeApdu("00B0820000", new ResponseHandler() {
            @Override
            public void onResponse(byte[] responseData) {
                if(responseData.length < 10) {
                    showErrorMessage("card sn read fail!");
                    return;
                }
                serialNo = BytesUtil.subBytes(responseData, 0, 10);
                next.invoke();
            }
        });
    }

    /**
     * Read card id from card.
     * @param next
     */
    protected void readCardId(final NextStep next) {
        exechangeApdu("00B0830000", new ResponseHandler() {
            @Override
            public void onResponse(byte[] responseData) {
                next.invoke();
            }
        });
    }

    /**
     * Read card info and notify.
     * @param next
     */
    protected void readCardInfo(final NextStep next) {
        final String readTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        exechangeApdu("80F8020008"+readTime+"80", new ResponseHandler() {
            @Override
            public void onResponse(byte[] responseData) {
                if(responseData.length < 116) {
                    showErrorMessage("card info read fail!");
                    return;
                }
                cardName = gbk(responseData, 0, 20);
                pan = toHexString(responseData, 20, 10, true);

                int track2Len;
                int track3Len;
                try {
                    track2Len = Integer.parseInt(BytesUtil.bytes2HexString(new byte[]{responseData[30]}));
                    track3Len = Integer.parseInt(BytesUtil.bytes2HexString(new byte[]{responseData[50], responseData[51]}));
                } catch (NumberFormatException e) {
                    showErrorMessage("card info read fail!");
                    return;
                }
                track2 = toHexString(responseData, 31, 20, false).substring(0, track2Len);
                track3 = toHexString(responseData, 52, 54, false).substring(0, track3Len);
                expiredDate = toHexString(responseData, 104, 2, false);

                onDataRead(pan, track2, track3, expiredDate, serialNo, readTime);
                next.invoke();
            }
        });
    }

    /**
     * Convert byte[] to hex string.
     *
     * @param data
     * @param offset
     * @param len
     * @param detectEnd
     * @return
     */
    private String toHexString(byte[] data, int offset, int len, boolean detectEnd) {
        byte[] d = BytesUtil.subBytes(data, offset, len);
        String str = BytesUtil.bytes2HexString(d);
        if(detectEnd && str.endsWith("F")) {
            int i = str.length();
            while(--i > 0) {
                if(str.charAt(i) != 'F') {
                    return str.substring(0, i+1);
                }
            }
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * Convert byte[] to GBK String
     * @param b
     * @param offset
     * @param len
     * @return
     */
    private String gbk(byte[] b, int offset, int len) {
        try {
            return new String(BytesUtil.subBytes(b, offset, len), "GBK");
        } catch (UnsupportedEncodingException e) {
        };
        return null;
    }

    /**
     * To operate the card through the exchange of apdu.
     * @param apdu
     */
    private void exechangeApdu(String apdu, ResponseHandler h) {
        this.respHandler = h;
        exechangeApdu(BytesUtil.hexString2Bytes(apdu));
    }

    /**
     * To operate the card through the exchange of apdu.
     * @param apdu
     */
    private void exechangeApdu(byte[] apdu) {
        try {
            driver.exchangeApdu(apdu, this);
        } catch (RequestException e) {
            onServiceCrash();
        }
    }

    /**
     * Show error message in reading process.
     * @param msg
     */
    protected abstract void showErrorMessage(String msg);

    protected abstract void onServiceCrash();

    /**
     * Handle final result include all data readed.
     * @param pan
     * @param track2
     * @param track3
     * @param expiredDate
     * @param serialNo
     * @param readTime
     */
    protected abstract void onDataRead(String pan, String track2, String track3, String expiredDate, byte[] serialNo, String readTime);

    /**
     * Handle the response of once exchanging.
     * @author chenwei
     *
     */
    interface ResponseHandler {
        void onResponse(byte[] responseData);
    }

    /**
     * Each card operation means a step.
     * @author chenwei
     *
     */
    interface NextStep {
        void invoke();
    }
}
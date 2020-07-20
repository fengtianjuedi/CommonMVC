package com.wufeng.latte_core.card;

import android.content.Context;

import com.landicorp.android.eptapi.card.MifareDriver;
import com.landicorp.android.eptapi.card.RFCpuCardDriver;
import com.landicorp.android.eptapi.card.RFDriver;
import com.landicorp.android.eptapi.device.RFCardReader;
import com.landicorp.android.eptapi.exception.RequestException;

/**
 * This code sample is about RF card operations.
 * @author chenwei
 *
 */
public abstract class RFCardReaderSample extends AbstractSample {
    private String driverName;

    /**
     * Create a listener to listen the result of search card.
     */
    private RFCardReader.OnSearchListener onSearchListener = new RFCardReader.OnSearchListener() {
        @Override
        public void onCrash() {
            onDeviceServiceCrash();
        }

        @Override
        public void onFail(int error) {
            displayRFCardInfo("SEARCH ERROR - "+getErrorDescription(error));
        }

        @Override
        public void onCardPass(int cardType) {
            // Choose the right card driver .
            switch (cardType) {
                case S50_CARD:
                    driverName = "S50";
                    break;
                case S70_CARD:
                    driverName = "S70";
                    break;
                case CPU_CARD:
                case PRO_CARD:
                case S50_PRO_CARD: // The card of this type can use 'S50' driver too.
                case S70_PRO_CARD: // The card of this type can use 'S70' driver too.
                    driverName = "PRO";
                    break;
                default:
                    showErrorMessage("Search card fail, unknown card type!");
                    return;
            }

            //displayRFCardInfo("rf card detected, and use "+driverName+" driver to read it!");
            try {
                RFCardReader.getInstance().activate(driverName, onActiveListener);
            } catch (RequestException e) {
                onDeviceServiceCrash();
            }
        }

        public String getErrorDescription(int code) {
            switch(code){
                case ERROR_CARDNOACT:
                    return "Pro card or TypeB card is not activated";
                case ERROR_CARDTIMEOUT:
                    return "No response";
                case ERROR_PROTERR :
                    return "The card return data does not meet the requirements of the protocal";
                case ERROR_TRANSERR:
                    return "Communication error";
            }
            return "unknown error ["+code+"]";
        }
    };

    /**
     * Create a listener to listen the result of activate card.
     */
    private RFCardReader.OnActiveListener onActiveListener = new RFCardReader.OnActiveListener() {

        @Override
        public void onCrash() {
            onDeviceServiceCrash();
        }

        @Override
        public void onUnsupport(String driverName) {
            //	All driver names using in this example have already support.
        }

        @Override
        public void onCardActivate(RFDriver cardDriver) {
            byte[] serial = getLastCardSerialNo();

            //displayRFCardInfo("card activated!"+BytesUtil.bytesToInt(serial));
            if(cardDriver instanceof RFCpuCardDriver) {
                // It is assumed to be UP card
                UPCardReader reader = new UPCardReader((RFCpuCardDriver) cardDriver) {
                    @Override
                    protected void showErrorMessage(String msg) {
                        RFCardReaderSample.this.displayRFCardInfo(msg);
                    }

                    @Override
                    protected void onServiceCrash() {
                        RFCardReaderSample.this.onDeviceServiceCrash();
                    }

                    @Override
                    protected void onDataRead(String pan, String track2, String track3,
                                              String expiredDate, byte[] serialNo, String readTime) {
                        StringBuilder infoBuilder = new StringBuilder();
                        infoBuilder.append("PAN [");
                        infoBuilder.append(pan);
                        infoBuilder.append("]\n");

                        infoBuilder.append("TRACK2 [");
                        infoBuilder.append(track2);
                        infoBuilder.append("]\n");

                        infoBuilder.append("TRACK3 [");
                        infoBuilder.append(track3);
                        infoBuilder.append("]\n");

                        infoBuilder.append("EXPIRED DATE [");
                        infoBuilder.append(expiredDate);
                        infoBuilder.append("]\n");

                        RFCardReaderSample.this.displayRFCardInfo(infoBuilder.toString());
                    }
                };

                reader.startRead();
            }
            else if(cardDriver instanceof MifareDriver) {
                // Use MifareOneCardReader to do some operations.
                MifareOneCardReader card = new MifareOneCardReader((MifareDriver)cardDriver) {

                    @Override
                    protected void showErrorMessage(String msg) {
                        RFCardReaderSample.this.displayRFCardInfo(msg);
                    }

                    @Override
                    protected void onDeviceServiceException() {
                        RFCardReaderSample.this.onDeviceServiceCrash();
                    }

                    @Override
                    protected void onDataRead(String info) {
                        RFCardReaderSample.this.displayRFCardInfo(info);
                    }
                };

                card.startRead();
            }
        }

        @Override
        public void onActivateError(int code) {
            displayRFCardInfo("ACTIVATE ERROR - "+getErrorDescription(code));
        }

        public String getErrorDescription(int code){
            switch(code){
                case ERROR_TRANSERR:
                    return "Communication error";
                case ERROR_PROTERR :
                    return "The card return data does not meet the requirements of the protocal";
                case ERROR_CARDTIMEOUT:
                    return "No response";
            }
            return "unknown error ["+code+"]";
        }
    };

    public RFCardReaderSample(Context context) {
        super(context);
    }

    /**
     * Start Search. The card you want to search can be a Mifare One card or CPU card (TypeB card).
     */
    public void searchCard() {
        try {
            RFCardReader.getInstance().searchCard(onSearchListener);
        } catch (RequestException e) {
            onDeviceServiceCrash();
        }
    }

    /**
     * Stop search if card searching is started
     */
    public void stopSearch() {
        try {
            RFCardReader.getInstance().stopSearch();
        } catch (RequestException e) {
            onDeviceServiceCrash();
        }
    }

    /**
     * Stop search if card searching is started
     */
    public boolean exists() {
        try {
            return RFCardReader.getInstance().exists();
        } catch (RequestException e) {
            onDeviceServiceCrash();
            return false;
        }
    }

    /**
     * Display rf card info
     * @param cardInfo
     */
    protected abstract void displayRFCardInfo(String cardInfo);
}

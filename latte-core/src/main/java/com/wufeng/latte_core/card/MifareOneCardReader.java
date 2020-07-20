package com.wufeng.latte_core.card;

import com.landicorp.android.eptapi.card.MifareDriver;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.BytesUtil;


/**
 *
 * This card reader can do some data operations.
 *
 * @author chenwei
    经营卡主卡
    6001100000000011451	6001210000000011	600121009999001
    6001100000000012452	6001210000000012	600121009999002
    经营卡副卡
    6001110000000111161	6001210000000111	600121009999001
    6001110000000112162	6001210000000112	600121009999002
    消费主卡
    6001200000000021121	6001210000000021	600121009999001
    6001200000000022122	6001210000000022	600121009999002
    消费副卡
    6001210000000121781	6001210000000121	600121009999001
    6001210000000122782	6001210000000122	600121009999002
    信贷主卡
    6001300000000031381	6001210000000031	600121009999001
    6001300000000032382	6001210000000032	600121009999002
    信贷副卡
    6001310000000131691	6001210000000131	600121009999001
    6001310000000132692	6001210000000132	600121009999002
    信用主卡
    6001400000000041731	6001210000000041	600121009999001
    6001400000000042732	6001210000000042	600121009999002
    信用副卡
    6001410000000141181	6001210000000141	600121009999001
    6001410000000142182	6001210000000142	600121009999002
 */
public abstract class MifareOneCardReader {
    private MifareDriver driver;

    public MifareOneCardReader(MifareDriver cardDriver) {
        this.driver = cardDriver;
    }

    protected abstract void onDeviceServiceException();

    protected abstract void showErrorMessage(String msg);

    protected abstract void onDataRead(String info);

    /**
     * Start read card
     */
    public void startRead() {
        execute(
                new Op() {
                    @Override
                    public void onStart(byte[] lastDataRead) throws RequestException {
                        //Certificate sector 1 can operate the No. 4 ~7 block
                        byte[] keyA = BytesUtil.hexString2Bytes("B192C384D576");
                        driver.authSector(1, MifareDriver.KEY_A, keyA, this);
                    }
                },
                new ReadOp() {
                    @Override
                    public void onStart(byte[] lastDataRead) throws RequestException {
                        // Read data
                        driver.readBlock(4, this);
                    }
                }
        );
        /*
        // The driver method can be used only once in the 'onStart' method.
        if(RFCardActivity.rfWriteCard) {
            execute(
                    new Op() {
                        @Override
                        public void onStart(byte[] lastDataRead) throws RequestException {
                            //Certificate sector 1 can operate the No. 4 ~7 block
                            byte[] keyB = BytesUtil.hexString2Bytes(Config.KeyB);
                            driver.authSector(1, MifareDriver.KEY_B, keyB, this);
                        }
                    },
                    new Op() {
                        @Override
                        public void onStart(byte[] lastDataRead) throws RequestException {
                            //RFCardActivity.rfCardNo = 060021000000000022353130
                            byte[] newData = BytesUtil.hexString2Bytes(RFCardActivity.rfCardNo+"20160513");
                            driver.writeBlock(4, newData, this);
                            driver.writeBlock(7, BytesUtil.hexString2Bytes("B192C384D57608778F699AB35CD67EF3"), this);
                        }
                    },
                    new ReadOp() {
                        @Override
                        public void onStart(byte[] lastDataRead) throws RequestException {
                            // Read data
                            driver.readBlock(4, this);
                        }
                    }
            );
        }else{
            execute(
                    new Op() {
                        @Override
                        public void onStart(byte[] lastDataRead) throws RequestException {
                            //Certificate sector 1 can operate the No. 4 ~7 block
                            byte[] keyA = BytesUtil.hexString2Bytes(Config.KeyA);
                            driver.authSector(1, MifareDriver.KEY_A, keyA, this);
                        }
                    },
                    new ReadOp() {
                        @Override
                        public void onStart(byte[] lastDataRead) throws RequestException {
                            // Read data
                            driver.readBlock(4, this);
                        }
                    }
            );
        }

        /*
        execute(
                new Op() {
                    @Override
                    public void onStart(byte[] lastDataRead) throws RequestException {
                        //Certificate sector 1 can operate the No. 4 ~7 block
                        byte[] keyA = BytesUtil.hexString2Bytes(Config.KeyA);
                        driver.authSector(1, MifareDriver.KEY_A, keyA, this);
                    }
                },
                new Op() {
                    @Override
                    public void onStart(byte[] lastDataRead) throws RequestException {
                        if (RFCardActivity.rfWriteCard) {
                            // Write the data have read to No. 5 block
                            // 6001200000000022122	6001210000000022	600121009999002
                            //06001200000000022122323020160426
                            byte[] newData = BytesUtil.hexString2Bytes(RFCardActivity.rfCardNo);
                            driver.writeBlock(4, newData, this);
                            driver.writeBlock(7, BytesUtil.hexString2Bytes("B192C384D57608778F699AB35CD67EF3"), this);
                        }
                    }
                },
                new ReadOp() {
                    @Override
                    public void onStart(byte[] lastDataRead) throws RequestException {
                        // Read data
                        driver.readBlock(4, this);
                    }
                },
                new Op() {
                    @Override
                    public void onStart(byte[] lastDataRead)
                            throws RequestException {
                        // Increase the No. 6 block value.
                        driver.increase(6, 2, this);
                    }
                },
                new Op() {

                    @Override
                    public void onStart(byte[] lastDataRead)
                            throws RequestException {
                        // Decrease the No. 6 block value.
                        driver.decrease(6, 10, this);
                    }
                },
                new Op() {

                    @Override
                    public void onStart(byte[] lastDataRead)
                            throws RequestException {
                        // Submit the changed value.
                        driver.transferRAM(6, this);
                    }
                },
                new Op() {

                    @Override
                    public void onStart(byte[] lastDataRead)
                            throws RequestException {
                        // All operations done. After invoke 'halt' method, you have to
                        // re search card to read card info again.
                        driver.halt();
                        onDataRead("Mifare One Card Read End");
                    }
                }
        );
        */
    }

    /**
     * Connect all operations and execute them.
     * @param operations
     */
    private void execute(MifareOneOperation... operations) {
        // Like chain of responsibility
        int len = operations.length - 1;
        for(int i=0; i<len; i++) {
            operations[i].setNextOperation(operations[i+1]);
        }
        // Start
        operations[0].start(null);
    }

    /**
     * A simplified non blocking Mifare One driver operation
     *
     * @author chenwei
     *
     */
    interface MifareOneOperation {
        /**
         * Start this operation object.
         * @param lastDataRead
         */
        void start(byte[] lastDataRead);
        /**
         * You can do one operation in this place.
         * @throws RequestException
         */
        void onStart(byte[] lastDataRead) throws RequestException;
        /**
         * Set the next operation and it will be start on this operation success.
         * @param operation
         */
        void setNextOperation(MifareOneOperation operation);
    }

    /**
     * It's a template of commen driver operation based on 'OnResultListener' such as 'increase', 'decrease'.
     * @author chenwei
     *
     */
    abstract class Op extends MifareDriver.OnResultListener implements MifareOneOperation {
        private MifareOneOperation nextOperation;

        @Override
        public void onFail(int code) {
            showErrorMessage("Mifare One Operation Error - "+getErrorDescription(code));
        }

        public String getErrorDescription(int code) {
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
        public void onCrash() {
            // This class does not know how to handle this exception.
            onDeviceServiceException();
        }

        @Override
        public void onSuccess() {
            if(nextOperation != null) {
                nextOperation.start(null);
            }
        }

        @Override
        public void start(byte[] lastDataRead) {
            try {
                onStart(lastDataRead);
            } catch (RequestException e) {
                onDeviceServiceException();
            }
        }

        @Override
        public void setNextOperation(MifareOneOperation operation) {
            this.nextOperation = operation;
        }
    }

    /**
     * It's a template of read operation based on OnReadListener.
     * @author chenwei
     *
     */
    abstract class ReadOp extends MifareDriver.OnReadListener implements MifareOneOperation {
        private MifareOneOperation nextOperation;
        @Override
        public void onFail(int error) {
            showErrorMessage("Mifare One Operation Error - "+getErrorDescription(error));
        }

        String getErrorDescription(int code){
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
        public void onSuccess(byte[] data) {
            String hex = BytesUtil.bytes2HexString(data);
            onDataRead(hex);
            if(nextOperation != null) {
                nextOperation.start(data);
            }
        }

        @Override
        public void onCrash() {
            onDeviceServiceException();
        }

        @Override
        public void start(byte[] lastDataRead) {
            try {
                onStart(lastDataRead);
            } catch (RequestException e) {
                onDeviceServiceException();
            }
        }

        @Override
        public void setNextOperation(MifareOneOperation operation) {
            this.nextOperation = operation;
        }
    }

}

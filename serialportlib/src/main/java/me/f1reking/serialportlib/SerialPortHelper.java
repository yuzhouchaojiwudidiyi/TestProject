/*
 * Copyright 2019 F1ReKing.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.f1reking.serialportlib;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import me.f1reking.serialportlib.entity.BAUDRATE;
import me.f1reking.serialportlib.entity.DATAB;
import me.f1reking.serialportlib.entity.Device;
import me.f1reking.serialportlib.entity.FLOWCON;
import me.f1reking.serialportlib.entity.PARITY;
import me.f1reking.serialportlib.entity.STOPB;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import me.f1reking.serialportlib.listener.IOpenSerialPortListener;
import me.f1reking.serialportlib.listener.ISerialPortDataListener;
import me.f1reking.serialportlib.listener.Status;
import me.f1reking.serialportlib.util.ByteUtils;

/**
 * @author F1ReKing
 * @date 2019/11/1 09:38
 * @Description
 */
public class SerialPortHelper {

    private static final String TAG = SerialPortHelper.class.getSimpleName();

    static {
        System.loadLibrary("serialport");
    }

    private IOpenSerialPortListener mIOpenSerialPortListener;
    private ISerialPortDataListener mISerialPortDataListener;
    private HandlerThread mSendingHandlerThread;
    private Handler mSendingHandler;
    private SerialPortReceivedThread mSerialPortReceivedThread;
    private SerialPortFinder mSerialPortFinder;

    private FileDescriptor mFD;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    private static String mPort = "/dev/ttyUSB0"; //?????????????????????
    private static int mBaudRate = 115200; //??????????????????
    private static int mStopBits = 2; //??????????????????
    private static int mDataBits = 8; //??????????????????
    private static int mParity = 0; //??????????????????
    private static int mFlowCon = 0; //???????????????
    private static int mFlags = 0;
    private boolean isOpen = false; //????????????????????????

    /**
     * ?????????????????????????????????
     *
     * @return ???????????????????????????
     */
    public String[] getAllDeicesPath() {
        if (mSerialPortFinder == null) {
            mSerialPortFinder = new SerialPortFinder();
        }
        return mSerialPortFinder.getAllDeicesPath();
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    public List<Device> getAllDevices() {
        if (mSerialPortFinder == null) {
            mSerialPortFinder = new SerialPortFinder();
        }
        return mSerialPortFinder.getAllDevices();
    }

    /**
     * ????????????
     *
     * @return ?????????????????? true:?????? false???????????????
     */
    public boolean open() {
        return openSerialPort(new File(mPort), mBaudRate, mStopBits, mDataBits, mParity, mFlowCon, mFlags);
    }

    /**
     * ????????????
     */
    public void close() {
        closeSerialPort();
    }

    /**
     * ????????????
     * @return true:?????? false:??????
     */
    public boolean isOpen() {
        return isOpen;
    }

    public boolean setPort(String port) {
        if (isOpen) {
            return false;
        }
        mPort = port;
        return true;
    }

    public String getPort() {
        return mPort;
    }

    public boolean setBaudRate(int baudRate) {
        if (isOpen) {
            return false;
        }
        mBaudRate = baudRate;
        return true;
    }

    public int getBaudRate() {
        return mBaudRate;
    }

    public boolean setDataBits(int dataBits) {
        if (isOpen) {
            return false;
        }
        mDataBits = dataBits;
        return true;
    }

    public int getDataBits() {
        return mDataBits;
    }

    public boolean setStopBits(int stopBits) {
        if (isOpen) {
            return false;
        }
        mStopBits = stopBits;
        return true;
    }

    public int getStopBits() {
        return mStopBits;
    }

    public boolean setParity(int parity) {
        if (isOpen) {
            return false;
        }
        mParity = parity;
        return true;
    }

    public int getParity() {
        return mParity;
    }

    public boolean setFlowCon(int flowCon) {
        if (isOpen) {
            return false;
        }
        mFlowCon = flowCon;
        return true;
    }

    public int getFlowCon() {
        return mFlowCon;
    }

    public boolean setFlags(int flags) {
        if (isOpen) {
            return false;
        }
        mFlags = flags;
        return true;
    }

    public int getFlags() {
        return mFlags;
    }

    public static class Builder {

        public Builder(String port, int baudRate) {
            mPort = port;
            mBaudRate = baudRate;
        }

        public Builder setStopBits(int stopBits) {
            mStopBits = stopBits;
            return this;
        }

        public Builder setDataBits(int dataBits) {
            mDataBits = dataBits;
            return this;
        }

        public Builder setParity(int parity) {
            mParity = parity;
            return this;
        }

        public Builder setFlowCon(int flowCon) {
            mFlowCon = flowCon;
            return this;
        }

        public Builder setFlags(int flags) {
            mFlags = flags;
            return this;
        }

        public SerialPortHelper build() {
            return new SerialPortHelper();
        }

    }

    /**
     * ????????????
     *
     * @param bytes ???????????????
     * @return ???????????? true:???????????? false???????????????
     */
    public boolean sendBytes(byte[] bytes) {
        if (null != mSendingHandler) {
            Message message = Message.obtain();
            message.obj = bytes;
            return mSendingHandler.sendMessage(message);
        }
        return false;
    }

    /**
     * ??????Hex
     *
     * @param hex 16????????????
     */
    public void sendHex(String hex) {
        byte[] hexArray = ByteUtils.hexToByteArr(hex);
        boolean b = sendBytes(hexArray);
    }

    /**
     * ????????????
     *
     * @param txt ??????
     */
    public void sendTxt(String txt) {
        byte[] txtArray = txt.getBytes();
        sendBytes(txtArray);
    }



    /**
     * ???????????????????????????
     *
     * @param iOpenSerialPortListener ??????
     */
    public void setIOpenSerialPortListener(IOpenSerialPortListener iOpenSerialPortListener) {
        mIOpenSerialPortListener = iOpenSerialPortListener;
    }

    /**
     * ?????????????????????????????????
     *
     * @param iSerialPortDataListener ??????
     */
    public void setISerialPortDataListener(ISerialPortDataListener iSerialPortDataListener) {
        mISerialPortDataListener = iSerialPortDataListener;
    }

    /**
     * ????????????
     *
     * @param device ???????????????????????????
     * @param baudRate {@link BAUDRATE} ?????????
     * @param stopBits {@link STOPB} ?????????
     * @param dataBits {@link DATAB} ?????????
     * @param parity {@link PARITY} ?????????
     * @param flowCon {@link FLOWCON} ??????
     * @param flags O_RDWR  ?????????????????? | O_NOCTTY  ??????????????????????????? | O_NDELAY   ?????????
     * @return ????????????
     */
    private boolean openSerialPort(File device, int baudRate, int stopBits, int dataBits, int parity, int flowCon, int flags) {
        isOpen = openSafe(device, baudRate, stopBits, dataBits, parity, flowCon, flags);
        return isOpen;
    }

    /**
     * ????????????
     */
    private void closeSerialPort() {
        stopSendThread();
        stopReceivedThread();
        closeSafe();
        isOpen = false;
    }

    /**
     * ????????????????????????
     */
    private void startSendThread() {
        mSendingHandlerThread = new HandlerThread("mSendingHandlerThread");
        mSendingHandlerThread.start();

        mSendingHandler = new Handler(mSendingHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                byte[] sendBytes = (byte[]) msg.obj;
                if (null != mFileOutputStream && null != sendBytes && sendBytes.length > 0) {
                    try {
                        mFileOutputStream.write(sendBytes);
                        if (null != mISerialPortDataListener) {
                            mISerialPortDataListener.onDataSend(sendBytes);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * ????????????????????????
     */
    private void stopSendThread() {
        mSendingHandler = null;
        if (null != mSendingHandlerThread) {
            mSendingHandlerThread.interrupt();
            mSendingHandlerThread.quit();
            mSendingHandlerThread = null;
        }
    }

    /**
     * ???????????????????????????
     */
    private void startReceivedThread() {
        mSerialPortReceivedThread = new SerialPortReceivedThread(mFileInputStream) {
            @Override
            public void onDataReceived(byte[] bytes) {
                if (null != mISerialPortDataListener) {
                    mISerialPortDataListener.onDataReceived(bytes);
                }
            }
        };
        mSerialPortReceivedThread.start();
    }

    /**
     * ???????????????????????????
     */
    private void stopReceivedThread() {
        if (null != mSerialPortReceivedThread) {
            mSerialPortReceivedThread.release();
        }
    }

    private boolean openSafe(File device, int baudRate, int stopBits, int dataBits, int parity, int flowCon, int flags) {
        Log.i(TAG, String.format("SerialPort: %s: %d,%d,%d,%d,%d,%d", device.getPath(), baudRate, stopBits, dataBits, parity, flowCon, flags));
        if (!device.canRead() || !device.canWrite()) {
            boolean chmod777 = chmod777(device);
            if (!chmod777) {
                Log.e(TAG, device.getPath() + " : ??????????????????");
                if (null != mIOpenSerialPortListener) {
                    mIOpenSerialPortListener.onFail(device, Status.NO_READ_WRITE_PERMISSION);
                }
                return false;
            }
        }
        try {
            mFD = nativeOpen(device.getAbsolutePath(), baudRate, stopBits, dataBits, parity, flowCon, flags);
            mFileInputStream = new FileInputStream(mFD);
            mFileOutputStream = new FileOutputStream(mFD);
            startSendThread();
            startReceivedThread();
            if (null != mIOpenSerialPortListener) {
                mIOpenSerialPortListener.onSuccess(device);
            }
            Log.i(TAG, device.getPath() + " : ??????????????????");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (null != mIOpenSerialPortListener) {
                mIOpenSerialPortListener.onFail(device, Status.OPEN_FAIL);
            }
        }
        return false;
    }

    private void closeSafe() {
        if (null != mFD) {
            nativeClose();
            mFD = null;
        }
        if (null != mFileInputStream) {
            try {
                mFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileInputStream = null;
        }

        if (null != mFileOutputStream) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileOutputStream = null;
        }
    }

    /**
     * ??????????????????
     *
     * @param device ??????
     * @return ????????????????????????
     */
    private boolean chmod777(File device) {
        if (null == device || !device.exists()) {
            return false;
        }
        try {
            Process su = Runtime.getRuntime().exec("/system/bin/su");
            String cmd = "chmod 777" + device.getAbsolutePath() + "\n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
            if (0 == su.waitFor() && device.canRead() && device.canWrite() && device.canExecute()) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        }
        return false;
    }

    /**
     * ????????????
     *
     * @param path ???????????????????????????
     * @param baudRate {@link BAUDRATE} ?????????
     * @param stopBits {@link STOPB} ?????????
     * @param dataBits {@link DATAB} ?????????
     * @param parity {@link PARITY} ?????????
     * @param flowCon {@link FLOWCON} ??????
     * @param flags O_RDWR  ?????????????????? | O_NOCTTY  ??????????????????????????? | O_NDELAY   ?????????
     *
     */
    private static native FileDescriptor nativeOpen(String path, int baudRate, int stopBits, int dataBits, int parity, int flowCon, int flags);

    /**
     * ????????????
     */
    public native void nativeClose();

}

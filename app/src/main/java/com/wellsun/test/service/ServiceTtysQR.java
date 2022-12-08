package com.wellsun.test.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import java.io.File;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.entity.DATAB;
import me.f1reking.serialportlib.entity.FLOWCON;
import me.f1reking.serialportlib.entity.PARITY;
import me.f1reking.serialportlib.entity.STOPB;
import me.f1reking.serialportlib.listener.IOpenSerialPortListener;
import me.f1reking.serialportlib.listener.ISerialPortDataListener;
import me.f1reking.serialportlib.listener.Status;

/**
 * date     : 2022-03-12
 * author   : ZhaoZheng
 * describe :
 */
public class ServiceTtysQR extends Service implements IOpenSerialPortListener, ISerialPortDataListener {
    private SerialPortHelper mSerialPortHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //    这String.format是将字节数组转换为十六进制的最简单和明显的方法，%02x对于小写十六进制，%02X大写十六进制。
    public static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
//            result.append(String.format("%02X", aByte));
            // upper case
            result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTtys();
    }


    //TTL7
    private void initTtys() {
        if (mSerialPortHelper == null) {
            mSerialPortHelper = new SerialPortHelper();
            mSerialPortHelper.setPort("/dev/ttyS7");                    //串口名
            mSerialPortHelper.setBaudRate(9600);   //9600               //波特率
            mSerialPortHelper.setStopBits(STOPB.getStopBit(STOPB.B1));  //停止位1
            mSerialPortHelper.setDataBits(DATAB.getDataBit(DATAB.CS8)); //数据位
            mSerialPortHelper.setParity(PARITY.getParity(PARITY.NONE));  //无奇偶校验
            mSerialPortHelper.setFlowCon(FLOWCON.getFlowCon(FLOWCON.NONE)); //无硬件控流
        }
        mSerialPortHelper.setIOpenSerialPortListener(this);
        mSerialPortHelper.setISerialPortDataListener(this);
        mSerialPortHelper.open();


    }

    @Override
    public void onSuccess(File device) {
        Log.v("接收数据", "串口7=" + device.getPath() + "成功");
    }

    @Override
    public void onFail(File device, Status status) {
        Log.v("接收数据", "串口7=" + device.getPath() + "失败");
    }


    @Override
    public void onDataReceived(byte[] bytes) {
        try {
            String readHex = bytesToHex(bytes);


        } catch (Exception e) {

        }

    }

    @Override
    public void onDataSend(byte[] bytes) {

    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


}

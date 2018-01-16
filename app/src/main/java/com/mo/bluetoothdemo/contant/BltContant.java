package com.mo.bluetoothdemo.contant;

import java.util.UUID;

/**
 * Created by mjx on 2016/9/26.
 */
public class BltContant {

    /**
     * 蓝牙UUID
     */
   /* //public static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static UUID SPP_UUID_SERVICE = UUID.fromString("EBB1201A-7680-4E99-B7DB-056B7172A34C");
    public static UUID SPP_UUID_READ = UUID.fromString("00010203-0405-0607-0809-0A0B0C0D2B12");
    public static UUID SPP_UUID_WRITE = UUID.fromString("66880001-0000-1000-8000-008012563489");
    public static UUID SPP_UUID_NOTIFY = UUID.fromString("66880002-0000-1000-8000-008012563489");
    //public final static String write = "66880001-0000-1000-8000-008012563489";
    public final static String write = "00001101-0000-1000-8000-00805F9B34FB";*/

    public final static String SERVICE = "66880000-0000-1000-8000-008012563489";
    public final static String WRITE = "66880001-0000-1000-8000-008012563489";
    public final static String NOTIFY = "66880002-0000-1000-8000-008012563489";

    //发送命令
    public final static String SYNCHRODATA = "01";    //同步数据
    public final static String SETTIME = "02";
    public final static String SETALARM = "03";
    public final static String SETWARN = "04"+"01";      //(0x01:久坐，0x02:喝水，0x03:QQ，0x04:微信，0x05:短信，0x07:facebook,0x08:skype,0x09:twitter,0x0a:whatsapp)
    public final static String FIND_WATCH = "08";    //寻找手表
    public final static String SET_SYSTEM = "0a"+"02";      //设置当前系统  0x01: iOS ， 0x02:android
    public final static String UNBOUND = "0d";     //解除绑定
    public final static String HANG_UP = "12";   //手机挂电话








    //启用蓝牙
    public static final int BLUE_TOOTH_OPEN = 1000;
    //禁用蓝牙
    public static final int BLUE_TOOTH_CLOSE = BLUE_TOOTH_OPEN + 1;
    //搜索蓝牙
    public static final int BLUE_TOOTH_SEARTH = BLUE_TOOTH_CLOSE + 1;
    //被搜索蓝牙
    public static final int BLUE_TOOTH_MY_SEARTH = BLUE_TOOTH_SEARTH + 1;
    //关闭蓝牙连接
    public static final int BLUE_TOOTH_CLEAR = BLUE_TOOTH_MY_SEARTH + 1;
}

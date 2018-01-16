package com.mo.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.mo.bluetoothdemo.Utils.Utils;
import com.mo.bluetoothdemo.contant.BltContant;

import java.util.UUID;

import static com.mo.bluetoothdemo.Code.DEVICE_SHEARCH_FAILURE;

/**
 * Created by mjx on 2017/11/30.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BltManager {

    private final static String TAG = "mjx";

    private final static int STOP_LESCAN = 0x101;

    private static BltManager bltManager;

    private BluetoothAdapter mBluetoothAdapter;     //蓝牙适配器

    private BluetoothDevice mBluetoothDevice;          //蓝牙设备

    private BluetoothGatt mBluetoothGatt;           //监听设备器

    private boolean isScanning = false;         //是否正在搜索

    Context mContext;

    BluetoothAdapter.LeScanCallback mLeScanCallback;

    private boolean isConnected = false;    //当前蓝牙连接状态
    private IBleExcute mBleExcute;    //蓝牙连接状态回调接口

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case STOP_LESCAN:
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    isScanning = false;
                    Log.i(TAG, "Scan time is up");
                    break;
            }
        }
    };

    /**
     * 获取单例对象
     */
    public static synchronized BltManager getInstance() {
        if (bltManager == null) {
            bltManager = new BltManager();
        }
        return bltManager;
    }

    //初始化BluetoothManager
    public void init(Context context,IBleExcute mBleExcute){
        mContext = context;
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);  //BluetoothManager只在android4.3以上有
        if (bluetoothManager == null) {
            Log.e(TAG, "Unable to initialize BluetoothManager.");
            return;
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();  //打开蓝牙
            }
        }
        this.mBleExcute = mBleExcute;
        //startLeScan();
        mBleExcute.initDone(true);
    }

    //开始搜索蓝牙设备
    public void startLeScan(BluetoothAdapter.LeScanCallback mLeScanCallback) {
        this.mLeScanCallback = mLeScanCallback;
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();   //重新获取Adapter
        }
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();  //打开蓝牙
                return;
            }
        }

        if (isScanning) {
            return;
        }
        isScanning = true;
        mBluetoothAdapter.startLeScan(this.mLeScanCallback);   //此mLeScanCallback为回调函数
        mHandler.sendEmptyMessageDelayed(STOP_LESCAN, 15000);  //这个搜索10秒，如果搜索不到则停止搜索
    }


   /* private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int arg1, byte[] arg2) {
            if(device.getName() == null){
                return;
            }

            if (device.getAddress().equals("A4:C1:38:00:03:89")) {    //判断是否搜索到你需要的ble设备      A4:C1:38:00:01:AE
                mBluetoothDevice = device;   //获取到周边设备
                mBluetoothAdapter.stopLeScan(mLeScanCallback);   //此mLeScanCallback为回调函数  //1、当找到对应的设备后，立即停止扫描；2、不要循环搜索设备，为每次搜索设置适合的时间限制。避免设备不在可用范围的时候持续不停扫描，消耗电量。
                mBleExcute.onConnectState(Code.CONNECTING);
                connect();  //连接
            }
        }
    };*/




    public boolean connect(BluetoothDevice device) {
        if (device == null) {
            Log.i(TAG, "BluetoothDevice is null.");
            return false;
        }
        mBluetoothDevice = device;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        //两个设备通过BLE通信,首先需要建立GATT连接。这里我们讲的是Android设备作为client端，连接GATT Server
        mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, mGattCallback);  //mGattCallback为回调接口

        if (mBluetoothGatt != null) {

            if (mBluetoothGatt.connect()) {
                Log.d(TAG, "Connect succeed.");
                isConnected = true;
                return true;
            } else {
                Log.d(TAG, "Connect fail.");
                return false;
            }
        } else {
            Log.d(TAG, "BluetoothGatt null.");
            return false;
        }
    }


     /**监听蓝牙连接的数据操作*/
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {//连接设备 成功
                gatt.discoverServices(); //执行到这里其实蓝牙已经连接成功了
                Log.i(TAG, "Connected to GATT server.");
                isConnected = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {      //断开连接
                isConnected = false;
                mBleExcute.onConnectState(Code.CONNECTION_FAILTH);
                if(mBluetoothDevice != null){
                    Log.i(TAG, "重新连接");
                    connect(mBluetoothDevice);
                }else{
                    Log.i(TAG, "Disconnected from GATT server.");
                }
            }
        }

         @Override
         public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
             super.onDescriptorWrite(gatt, descriptor, status);
             mBleExcute.onConnectState(Code.CONNECTION_SUCCESS);
             Log.i(TAG, "Connected and shareHard");
             //建立好监听，可以开始握手或发送数据

         }

         @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onServicesDiscovered");
                mHandler.post(notifyRunnable);
            } else {
                Log.i(TAG, "onServicesDiscovered status------>" + status);
                isConnected = false;
            }
        }



        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //判断UUID是否相等
            if (BltContant.SERVICE.equals(characteristic.getUuid().toString())) {
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] vValue = characteristic.getValue();
            String code = Utils.bytesToHexString(vValue);
            Log.e(TAG, "返回码-------: " + code);
            mBleExcute.onRead(code);
            //判断UUID是否相等
            if (BltContant.SERVICE.equals(characteristic.getUuid().toString())) {
            }
        }

        //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发onCharacteristicWrite
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //Log.d(TAG, "onCharacteristicWrite------>" + Utils.bytesToHexString(characteristic.getValue()));
        }
    };


    /**
     * 功能：监听返回数据
     */
    private Runnable notifyRunnable = new Runnable() {
        @Override
        public void run() {
            BluetoothGattCharacteristic batteryLevelGattC = getCharcteristic(BltContant.SERVICE, BltContant.NOTIFY);
            if (batteryLevelGattC != null) {
                readCharacteristic(batteryLevelGattC);
                setCharacteristicNotification(batteryLevelGattC,true); //设置当指定characteristic值变化时，发出通知。
            }
            mBleExcute.onConnectState(Code.CONNECTION_SUCCESS);
        }
    };

    //a.获取服务
    public BluetoothGattService getService(UUID uuid) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            mBleExcute.onConnectState(DEVICE_SHEARCH_FAILURE);
            return null;
        }
        return mBluetoothGatt.getService(uuid);
    }

    //b.获取特征
    private BluetoothGattCharacteristic getCharcteristic(String serviceUUID, String characteristicUUID) {

        //得到服务对象
        BluetoothGattService service = getService(UUID.fromString(serviceUUID));  //调用上面获取服务的方法

        if (service == null) {
            Log.e(TAG, "Can not find 'BluetoothGattService'");
            mBleExcute.onConnectState(DEVICE_SHEARCH_FAILURE);
            return null;
        }

        //得到此服务结点下Characteristic对象
        final BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (gattCharacteristic != null) {
            return gattCharacteristic;
        } else {
            Log.e(TAG, "Can not find 'BluetoothGattCharacteristic'");
            mBleExcute.onConnectState(DEVICE_SHEARCH_FAILURE);
            return null;
        }
    }



    //获取数据
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        Log.e(TAG, "readCharacteristic 获取数据--------------");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            mBleExcute.onConnectState(DEVICE_SHEARCH_FAILURE);
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }



    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            mBleExcute.onConnectState(DEVICE_SHEARCH_FAILURE);
            return false;
        }
        return mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

    }



    public void write(byte[] data) {   //一般都是传byte
        if(!isConnected||!mBluetoothAdapter.isEnabled()){
            Log.e(TAG, "writeCharacteristic 开启飞行模式");
            //closeBluetoothGatt();
            isConnected = false;
            mBleExcute.onConnectState(Code.DISCONNECT);
            return;
        }
        BluetoothGattCharacteristic writeCharacteristic = getCharcteristic(BltContant.SERVICE, BltContant.WRITE);  //这个UUID都是根据协议号的UUID
        if (writeCharacteristic == null) {
            Log.e(TAG, "Write failed. GattCharacteristic is null.");
            return;
        }
        writeCharacteristic.setValue(data); //为characteristic赋值
        writeCharacteristicWrite(writeCharacteristic);

    }

    public void write(String  data) {   //一般都是传byte
        Log.e(TAG,"send:"+data);
        if(!mBluetoothAdapter.isEnabled()||!isConnected){
            //closeBluetoothGatt();
            isConnected = false;
            mBleExcute.onConnectState(Code.DISCONNECT);
            return;
        }
        BluetoothGattCharacteristic writeCharacteristic = getCharcteristic(BltContant.SERVICE, BltContant.WRITE);  //这个UUID都是根据协议号的UUID
        if (writeCharacteristic == null) {
            Log.e(TAG, "Write failed. GattCharacteristic is null.");
            return;
        }
        byte valueByte[] = new byte[0];
        if (data!=null&&!data.equals("")) {
            valueByte = Utils.HextoByteArray(data);//String转换为byte[]
        }
        if (valueByte!=null) {
            writeCharacteristic.setValue(valueByte); //为characteristic赋值
            writeCharacteristicWrite(writeCharacteristic);
        }
    }

    public void writeCharacteristicWrite(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            mBleExcute.onConnectState(DEVICE_SHEARCH_FAILURE);
            return;
        }
        boolean isBoolean;
        isBoolean = mBluetoothGatt.writeCharacteristic(characteristic);
        Log.e(TAG, "写入数据: " +isBoolean);  //如果isBoolean返回的是true则写入成功
        mBleExcute.onWriteStatus(isBoolean);
    }

}

package com.mo.bluetoothdemo.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mo.bluetoothdemo.BltManager;
import com.mo.bluetoothdemo.Code;
import com.mo.bluetoothdemo.IBleExcute;
import com.mo.bluetoothdemo.R;
import com.mo.bluetoothdemo.Utils.Utils;
import com.mo.bluetoothdemo.adapter.DevicesAdapter;
import com.mo.bluetoothdemo.bean.DevicesBean;
import com.mo.bluetoothdemo.service.MsgListenerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mjx on 2017/12/19.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends AppCompatActivity implements IBleExcute,AdapterView.OnItemClickListener{
    private  final  static  int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    Context mContext;

    EditText editText;
    ListView deviceList;
    DevicesAdapter deviceAdapter;
    List<DevicesBean> devices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext= this;

        startService(new Intent(MainActivity.this, MsgListenerService.class));//启动服务
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);  //获取通知栏使用权限来监听QQ微信等手机信息
        startActivity(intent);
        deviceList = (ListView)findViewById(R.id.device_list);
        devices = new ArrayList<>();
        deviceAdapter = new DevicesAdapter(this,devices);
        deviceList.setAdapter(deviceAdapter);
        deviceList.setOnItemClickListener(this);
        Button aa = (Button)findViewById(R.id.aaa);
        aa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText.getText().toString();//String变量
                if (s!=null&&!s.equals("")) {
                    BltManager.getInstance().write(s);   //直接发送string
                   /* byte b[] = Utils.HextoByteArray(s);//String转换为byte[]
                    BltManager.getInstance().write(b);*/
                }
            }
        });
         editText = (EditText)findViewById(R.id.edit);
         BltManager.getInstance().init(this,this);
        checkBluetoothPermission();
        Button button = (Button)findViewById(R.id.but);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBluetoothPermission();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void initDone(boolean isSucc) {
        if (isSucc) {
            Toast.makeText(this, "初始化成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectState(Code pConnectCode) {
        switch (pConnectCode) {
            case CONNECTION_SUCCESS:
                Toast.makeText(this,"连接设备成功",Toast.LENGTH_LONG).show();

                break;
            case CONNECTION_FAILTH:
                Toast.makeText(this,"连接设备失败",Toast.LENGTH_SHORT).show();
                break;
            case CONNECTION_TIMEOUT:
                Toast.makeText(this,"连接超时",Toast.LENGTH_SHORT).show();
                break;
            case DEVICE_SHEARCH_FAILURE:
                Toast.makeText(this,"没有搜索到该设备",Toast.LENGTH_SHORT).show();
                break;
            case DISCONNECT:
                Toast.makeText(this,"断开连接",Toast.LENGTH_SHORT).show();
                break;
            case CONNECTING:
                Toast.makeText(this,"找到设备开始连接",Toast.LENGTH_SHORT).show();
                break;
            case INVALID_MAC:
                Toast.makeText(this,"无效蓝牙",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onRead(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
            }
        });

        dealData(data);
    }

    @Override
    public void onWriteStatus(boolean writeStatus) {
        if (writeStatus){
            Toast.makeText(this,"写入成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"写入失败",Toast.LENGTH_SHORT).show();
        }
    }

    public void dealData(String data){
       //处理蓝牙返回的数据
    }

   /**
      *校验蓝牙权限
     * Android6.0 蓝牙扫描才需要
     */
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                //具有权限
                BltManager.getInstance().startLeScan(mLeScanCallback);
                Toast.makeText(this,"具有权限",Toast.LENGTH_SHORT).show();
            }
        } else {
            //系统不高于6.0直接执行
            BltManager.getInstance().startLeScan(mLeScanCallback);
        }
    }


    /**权限回调*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意权限
                Toast.makeText(this,"同意权限",Toast.LENGTH_SHORT).show();
                BltManager.getInstance().startLeScan(mLeScanCallback);
            } else {
                // 权限拒绝
                // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
                Toast.makeText(this,"拒绝权限",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int arg1, byte[] arg2) {
            if(device.getName() == null){
                return;
            }
            devices.add(new DevicesBean(device));
            deviceAdapter.setData(devices);
            // BltManager.getInstance().connect(device);
           /* if (device.getAddress().equals("A4:C1:38:00:03:89")) {    //判断是否搜索到你需要的ble设备      A4:C1:38:00:01:AE
                mBluetoothDevice = device;   //获取到周边设备
                mBluetoothAdapter.stopLeScan(mLeScanCallback);   //此mLeScanCallback为回调函数  //1、当找到对应的设备后，立即停止扫描；2、不要循环搜索设备，为每次搜索设置适合的时间限制。避免设备不在可用范围的时候持续不停扫描，消耗电量。
                mBleExcute.onConnectState(Code.CONNECTING);
                connect();  //连接
            }*/
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BltManager.getInstance().connect(devices.get(position).getDevice());
    }
}

package com.mo.bluetoothdemo.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.mo.bluetoothdemo.BltManager;
import com.mo.bluetoothdemo.Utils.Utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by mjx on 2017/12/7.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class MsgListenerService extends NotificationListenerService {

    private MyHandler handler = new MyHandler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("mjx", "Srevice is open" + "-----");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn!=null&&sbn.getNotification().tickerText != null) {
            if (sbn.getPackageName().equals("com.tencent.mqq")){    //QQ消息    00
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.tencent.mm")){  //微信       01
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.android.dialer")){  //电话    03
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.android.mms")){  //短信    02
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.facebook.katana")){  //facebook     05
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.skype.raider")){  //skype       06
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.twitter.android")){  //twitter      07
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.whatsapp")){  //whatsapp      08
                dealData(sbn.getNotification().tickerText.toString());
            }else if (sbn.getPackageName().equals("com.android.email")){  //邮件      04
                dealData(sbn.getNotification().tickerText.toString());
            }
        }
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);
    }

    public void dealData(String content){
       //处理返回的消息
    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MsgListenerService.this, "接收到一条消息！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
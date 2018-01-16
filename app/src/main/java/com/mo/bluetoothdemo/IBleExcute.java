package com.mo.bluetoothdemo;


/**
 * Created by yzy on 17-5-19.
 */

public interface IBleExcute {

    /**
     * 初始化完成回调
     * @param isSucc
     */
    void initDone(boolean isSucc);

    /**
     * 连接状态回调
     * @param pConnectCode
     */
    void onConnectState(Code pConnectCode);

    void onRead(String data);

    void onWriteStatus(boolean writeStatus);

    /**
     * 设备返回信息回调
     * @param pCode  设备返回的code
     * @param pDeviceInfo   根据code 解析成的实体包含所有设备信息
     */
    //void onRead(Code pCode, DeviceInfo pDeviceInfo);
}

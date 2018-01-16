package com.mo.bluetoothdemo.bean;

import java.io.Serializable;

/**
 * Created by mjx on 2017/11/30.
 */

public class DeviceMessage implements Serializable {
    private String runDate;           //跑步天数
    private String steps;           //步数
    private String deepSleepHour;           //深睡眠小时
    private String deepSleepMim;           //深睡眠分钟
    private String lightSleepHour;           //浅睡眠小时
    private String lightSleepMim;           //浅睡眠分钟
    private String wakeNum;           //清醒次数
    private String distance;           //距离
    private String calories;           //卡里路

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDeepSleepHour() {
        return deepSleepHour;
    }

    public void setDeepSleepHour(String deepSleepHour) {
        this.deepSleepHour = deepSleepHour;
    }

    public String getDeepSleepMim() {
        return deepSleepMim;
    }

    public void setDeepSleepMim(String deepSleepMim) {
        this.deepSleepMim = deepSleepMim;
    }

    public String getLightSleepHour() {
        return lightSleepHour;
    }

    public void setLightSleepHour(String lightSleepHour) {
        this.lightSleepHour = lightSleepHour;
    }

    public String getLightSleepMim() {
        return lightSleepMim;
    }

    public void setLightSleepMim(String lightSleepMim) {
        this.lightSleepMim = lightSleepMim;
    }

    public String getWakeNum() {
        return wakeNum;
    }

    public void setWakeNum(String wakeNum) {
        this.wakeNum = wakeNum;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}

package com.ruoyi.log.common.queue;

import java.util.List;

public interface CommonDataQueue {
    void sendLogData(String logData);
    List<String> pollLogData();
    void sendAlertData(String alertData);
    List<String> pollAlertData();
}
package com.ruoyi.log.warehouse.store;

import com.ruoyi.log.common.queue.CommonDataQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class LogDataStorageDispatch {

    @Autowired(required = false)
    private CommonDataQueue commonDataQueue;

    @PostConstruct
    public void init() {
        if (commonDataQueue != null) {
            new Thread(this::consumeLogData).start();
        }
    }

    private void consumeLogData() {
        while (true) {
            try {
                List<String> logs = commonDataQueue.pollLogData();
                if (logs != null && !logs.isEmpty()) {
                    // TODO: Implement ElasticSearch / ClickHouse bulk insert
                    System.out.println("Consumed " + logs.size() + " logs to warehouse.");
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
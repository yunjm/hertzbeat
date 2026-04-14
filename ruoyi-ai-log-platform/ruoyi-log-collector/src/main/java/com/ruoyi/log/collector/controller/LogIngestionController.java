package com.ruoyi.log.collector.controller;

import com.ruoyi.log.common.queue.CommonDataQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// NOTE: In a real RuoYi project, @PreAuthorize("@ss.hasPermi('...')") would be used here.
// import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/logs")
public class LogIngestionController {

    @Autowired(required = false)
    private CommonDataQueue commonDataQueue;

    // @PreAuthorize("@ss.hasPermi('log:ingest:add')")
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestLog(@RequestBody String logData) {
        if (commonDataQueue != null) {
            commonDataQueue.sendLogData(logData);
            return ResponseEntity.ok("Log accepted");
        }
        return ResponseEntity.internalServerError().body("Queue not configured");
    }
}
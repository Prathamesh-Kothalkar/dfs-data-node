package com.dfs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@EnableScheduling // Enables background task execution timers
public class HeartbeatScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${server.port}")
    private String myPort;

   
    @Scheduled(fixedRate = 5000)
    public void sendPing() {
        String nameNodeUrl = "http://localhost:8080/api/v1/registry/heartbeat?nodeUrl=http://localhost:" + myPort;
        try {
            restTemplate.postForEntity(nameNodeUrl, null, String.class);
            System.out.println("Heartbeat sent successfully to Master from port: " + myPort);
        } catch (Exception e) {
            System.err.println("NameNode Master offline. Retrying in 5 seconds...");
        }
    }
}
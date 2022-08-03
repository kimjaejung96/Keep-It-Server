package com.teamside.project.alpha.sms.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 Class       : com.teamside.project.alpha.sms..config.SMSAsyncConfig
 * Author      : 조 준 희
 * Description : SMS Event 비동기 쓰레드 풀 Configuration
 * History     : [2022-08-03] - 조 준희 - Class Create
 */
@Configuration
@EnableAsync
@Log4j2
public class SMSAsyncConfig  {


    /**
     * Description : SMS Event 비동기 쓰레드 풀 Configuration
     * Name        : getAsyncExecutor
     * Author      : 조 준 희
     * History     : [2022-08-03] - 조 준 희 - Create
     */
    @Bean("smsAsync")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);  // Idle Count
        executor.setMaxPoolSize(10);  // Use When queue max size is
        executor.setQueueCapacity(10);   // Ready Queue Size
        executor.setKeepAliveSeconds(60); // Alive Secondes
        executor.setThreadNamePrefix("SMSRequestWorker");  // Thread Worker Prefix-Name
        executor.initialize();

        log.debug("SMS Event Async 쓰레드 풀 생성 완료");

        return executor;
    }
}

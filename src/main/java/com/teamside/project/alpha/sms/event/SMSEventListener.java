package com.teamside.project.alpha.sms.event;


import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.sms.component.SMSSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Class       : SMSEventListener
 * Author      : 조 준 희
 * Description : SMS Event 리스너.
 * History     : [2022-08-03] - 조 준희 - Class Create
 */
@Component
@EnableAsync
@Log4j2
public class SMSEventListener {
    private final SMSSender smsSender;

    @Autowired
    public SMSEventListener(SMSSender smsSender) {
        this.smsSender = smsSender;
    }

    @Async("smsAsync")
    @EventListener
    public void onApplicationEvent(SMSEvent event){
        try {
            smsSender.sendAuthMessage(event.getPhoneNum(), event.getSmsAuthNum());

        } catch (IOException | CustomException e) {
            log.error("SMS Send Exception : {}", e.toString());
            return ;
        }

        log.debug("SMS Send 성공.");
    }
}

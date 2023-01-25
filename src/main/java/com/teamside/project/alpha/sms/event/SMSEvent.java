package com.teamside.project.alpha.sms.event;

import lombok.Getter;
import lombok.ToString;

/**
 * Class       : SMSEvent
 * Author      : 조 준 희
 * Description : SMS 발송 Event 티켓
 * History     : [2022-08-03] - 조 준희 - Class Create
 */
@Getter
@ToString
public class SMSEvent  {

    private final String phoneNum;
    private final String smsAuthNum;
    private final String smsType;

    /**
     * Description : 생성자
     * Name        : SMSEvent
     * @param phoneNum 폰 번호
     * @param smsAuthNum 인증 문자
     * Author      : 조 준 희
     * History     : [2022-08-03] - 조 준 희 - Create
     */
    public SMSEvent( String phoneNum, String smsAuthNum, String smsType){
        this.phoneNum = phoneNum;
        this.smsAuthNum = smsAuthNum;
        this.smsType = smsType;
    }

}

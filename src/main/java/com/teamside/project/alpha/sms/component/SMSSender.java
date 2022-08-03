package com.teamside.project.alpha.sms.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.sms.dto.SmsDto;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;


/**
 * Class       : com.teamside.project.alpha.sms.component.SMSSender
 * Author      : 조 준 희
 * Description : SMS 발송 유틸
 * History     : [2022-08-03] - 조 준희 - Class Create
 */
@Component
public class SMSSender {
    private final Logger logger = LogManager.getLogger(SMSSender.class);
    private final String SENS_HOST_URL;
    private final String SENS_REQUEST_URL ;
    private final String SENS_REQUEST_TYPE ;
    private final String SENS_SVC_ID ;
    private final String SENS_MESSAGE_TYPE_SMS;
    private final String SENS_MESSAGE_CONTENTTPYE_COMM ;
    private final String SENS_MESSAGE_COUNTRYCODE_DEFAULT ;
    private final String SENS_ACCESSKEY ;
    private final String SENS_SECRETKEY ;
    private final String CERTIFICATION_MSG_FORMAT;


    @Autowired
    public SMSSender(@Value("${alpha.sens.host}") String SENS_HOST_URL
            , @Value("${alpha.sens.url}") String SENS_REQUEST_URL
            , @Value("${alpha.sens.type}") String SENS_REQUEST_TYPE
            , @Value("${alpha.sens.svc_id}") String SENS_SVC_ID
            , @Value("${alpha.sens.message.type}") String SENS_MESSAGE_TYPE_SMS
            , @Value("${alpha.sens.message.content_type}") String SENS_MESSAGE_CONTENTTPYE_COMM
            , @Value("${alpha.sens.message.country_code}") String SENS_MESSAGE_COUNTRYCODE_DEFAULT
            , @Value("${alpha.sens.accessKey}") String SENS_ACCESSKEY
            , @Value("${alpha.sens.secretKey}") String SENS_SECRETKEY
    , @Value("${alpha.sens.message.message}") String CERTIFICATION_MSG_FORMAT) {
        this.SENS_HOST_URL = SENS_HOST_URL;
        this.SENS_REQUEST_URL = SENS_REQUEST_URL;
        this.SENS_REQUEST_TYPE = SENS_REQUEST_TYPE;
        this.SENS_SVC_ID = SENS_SVC_ID;
        this.SENS_MESSAGE_TYPE_SMS = SENS_MESSAGE_TYPE_SMS;
        this.SENS_MESSAGE_CONTENTTPYE_COMM = SENS_MESSAGE_CONTENTTPYE_COMM;
        this.SENS_MESSAGE_COUNTRYCODE_DEFAULT = SENS_MESSAGE_COUNTRYCODE_DEFAULT;
        this.SENS_ACCESSKEY = SENS_ACCESSKEY;
        this.SENS_SECRETKEY = SENS_SECRETKEY;
        this.CERTIFICATION_MSG_FORMAT = CERTIFICATION_MSG_FORMAT;
    }

    /**
     * Description : SMS 발송  (폰번호 + 인증 문자)
     * @param sendPhoneNum 타겟 폰 번호.
     * @param smsAuthNum 인증 문자.
     * Name        : sendAuthMessage
     * Author      : 조 준 희
     * History     : [2022-08-03] - 조 준 희 - Create
     */
    public void sendAuthMessage(String sendPhoneNum, String smsAuthNum) throws IOException, InterruptedException {
        int resultCode = 0;
        long executeTimer;
        StopWatch stopWatch = new StopWatch();
        String timeStamp = Long.toString(System.currentTimeMillis());
        String apiUrl = SENS_HOST_URL + SENS_REQUEST_URL + SENS_SVC_ID + SENS_REQUEST_TYPE;

        SmsDto smsDto  = SmsDto.builder()
                .type(SENS_MESSAGE_TYPE_SMS)
                .contentType(SENS_MESSAGE_CONTENTTPYE_COMM)
                .countryCode(SENS_MESSAGE_COUNTRYCODE_DEFAULT)
                .from("01027090787")
//                .subject("SMS")
                .content("[인증]")
//                .content("기본 콘텐츠" + Integer.toString(RandomUtils.nextInt(10000, 100000)))
                .messages(Collections
                        .singletonList(SmsDto.MessageInfoDto.builder()
                                .to(sendPhoneNum)
                                .content(String.format(CERTIFICATION_MSG_FORMAT, smsAuthNum))
                                .build()))
                .build();


        ObjectMapper mapper = new ObjectMapper();

        String jsonStr = mapper.writeValueAsString(smsDto);



        StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");

        try{
            stopWatch.start();
            HttpClient httpClient = HttpClients.custom().setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("x-ncp-apigw-timestamp", timeStamp);
            httpPost.addHeader("x-ncp-iam-access-key", SENS_ACCESSKEY);
            httpPost.addHeader("x-ncp-apigw-signature-v2", makeSignature(timeStamp));


            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            resultCode = httpResponse.getStatusLine().getStatusCode();

        } catch (Exception e) {
            stopWatch.stop();
            executeTimer = stopWatch.getTotalTimeMillis();
            logger.error("SMS 전송 Error - {}에 전송을 실패 : {}", sendPhoneNum, e.getMessage());
        }

        stopWatch.stop();
        executeTimer = stopWatch.getTotalTimeMillis();

        if (resultCode == 202) {
            logger.info("SMS 전송 Success - {}에 전송 성공", sendPhoneNum);
        } else {
            logger.error("SMS 전송 Error - {}에 전송을 실패. ResultCode : {}", sendPhoneNum, resultCode);
        }
    }

    private String makeSignature(String timestamp) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";

        String sensApiUrl = SENS_REQUEST_URL + SENS_SVC_ID + SENS_REQUEST_TYPE;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(sensApiUrl)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(SENS_ACCESSKEY)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(SENS_SECRETKEY.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        return encodeBase64String;
    }

}

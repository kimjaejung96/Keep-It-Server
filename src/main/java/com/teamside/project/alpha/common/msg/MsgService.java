package com.teamside.project.alpha.common.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.msg.enumurate.MQExchange;
import com.teamside.project.alpha.common.msg.enumurate.MQRoutingKey;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MsgService {
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public <T> void publishMsg(MQExchange exchange, MQRoutingKey routingKey, T message) {
        MsgContent msgContent = new MsgContent(message);
        try {
            amqpTemplate.convertAndSend(exchange.getValue(), routingKey.getValue(), objectMapper.writeValueAsString(msgContent));
        } catch (JsonProcessingException e) {
            System.out.println("Error sending message");
        }


    }
}

package com.teamside.project.alpha.common.msg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamside.project.alpha.common.msg.enumurate.MQExchange;
import com.teamside.project.alpha.common.msg.enumurate.MQRoutingKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MsgService {
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public <T> Boolean publishMsg(MQExchange exchange, MQRoutingKey routingKey, T message) {
        try {
            amqpTemplate.convertAndSend(exchange.getValue(), routingKey.getValue(), objectMapper.writeValueAsString(message));
            return true;
        } catch (JsonProcessingException | RuntimeException e) {
            log.error("\n[Error] failed to publish message");
            return false;
        }
    }
}

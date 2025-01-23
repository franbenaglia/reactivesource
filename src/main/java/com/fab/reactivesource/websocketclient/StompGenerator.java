package com.fab.reactivesource.websocketclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.fab.reactivesource.source.ClimateStreamSourceMock;
import com.fab.reactivesource.source.model.Climate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@Component
public class StompGenerator {

    private final static Logger logger = LoggerFactory.getLogger(StompGenerator.class);

    @Autowired
    private ClimateStreamSourceMock streamSource;

    @SuppressWarnings("removal")
    public void climateMock() {

        WebSocketClient webSocketClient = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        stompClient.setMessageConverter(new StringMessageConverter());

        String url = "ws://localhost:8080/climate-websocket";

        StompSessionHandler sessionHandler = new MyStompSessionHandler();

        stompClient.connect(url, sessionHandler);

    }

    private class MyStompSessionHandler extends StompSessionHandlerAdapter {

        Flux<Climate> cs = streamSource.getClimateData();

        @SuppressWarnings("null")
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

            cs.subscribe(climate -> {
                ObjectMapper mapper = new ObjectMapper();
                String json = "";
                try {
                    json = mapper.writeValueAsString(climate);
                    logger.info("input: " + json);
                    session.send("/app/climateData", json);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });

        }

        @SuppressWarnings("null")
        @Override
        public void handleException(StompSession session, @Nullable StompCommand command,
                StompHeaders headers, byte[] payload, Throwable exception) {
            logger.error(exception.getMessage());
        }

    }

}

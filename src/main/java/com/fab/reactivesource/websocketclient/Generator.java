package com.fab.reactivesource.websocketclient;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.fab.reactivesource.source.ClimateStreamSourceMock;
import com.fab.reactivesource.source.model.Climate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class Generator {

    private final static Logger logger = LoggerFactory.getLogger(Generator.class);

    @Autowired
    private ClimateStreamSourceMock streamSource;

    public void climateMock() {

        Flux<Climate> cs = streamSource.getClimateData();
        WebSocketClient client = new ReactorNettyWebSocketClient();

        EmitterProcessor<String> output = EmitterProcessor.create();

        // Many<String> z = Sinks.many().multicast().onBackpressureBuffer();

        Mono<Void> sessionMono = client.execute(URI.create("ws://localhost:8080/climateData"),
                session -> session.send(cs.map(climate -> {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = "";
                    try {
                        json = mapper.writeValueAsString(climate);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return session.textMessage(json);
                }))
                        .thenMany(
                                session.receive()
                                        .map(WebSocketMessage::getPayloadAsText)
                                        .subscribeWith(output).then())
                        .then());

        sessionMono.subscribe();

        output.doOnSubscribe(s -> {
            sessionMono.subscribe();
        }).subscribe(x -> logger.info("output: " + x));

    }

    public void climateMockWithOutSenderReceived() {

        Flux<Climate> cs = streamSource.getClimateData();
        WebSocketClient client = new ReactorNettyWebSocketClient();

        Mono<Void> sessionMono = client.execute(URI.create("ws://localhost:8080/climateData"),
                session -> session.send(cs.map(climate -> {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = "";
                    try {
                        json = mapper.writeValueAsString(climate);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return session.textMessage(json);
                }))
                        .then());

        sessionMono.subscribe();

    }

}

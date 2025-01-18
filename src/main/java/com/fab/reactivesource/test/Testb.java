package com.fab.reactivesource.test;

import java.net.URI;
import java.time.Duration;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Testb {

        private final static Logger logger = LoggerFactory.getLogger(Testb.class);

        public static void main(String[] args) {

                Flux<String> input = Flux
                                .<String>generate(
                                                sink -> sink.next(String.format(
                                                                "{ message: 'got message', date: '%s' }", new Date())))
                                .delayElements(Duration.ofSeconds(1));

                WebSocketClient client = new ReactorNettyWebSocketClient();

                EmitterProcessor<String> output = EmitterProcessor.create();

                Mono<Void> sessionMono = client.execute(URI.create("ws://localhost:8080/climateData"),
                                session -> session.send(input.map(session::textMessage))
                                                .thenMany(
                                                                session.receive()
                                                                                .map(WebSocketMessage::getPayloadAsText)
                                                                                .subscribeWith(output).then())
                                                .then());

                output.doOnSubscribe(s -> {
                        sessionMono.subscribe();
                }).subscribe(x -> logger.info("output: " + x));

                for (;;)
                        ;

        }

}

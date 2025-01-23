package com.fab.reactivesource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.fab.reactivesource.websocketclient.Generator;
import com.fab.reactivesource.websocketclient.StompGenerator;

@SpringBootApplication
public class ReactivesourceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ReactivesourceApplication.class, args);
		// Generator g = context.getBean(Generator.class);
		StompGenerator stompg = context.getBean(StompGenerator.class);
		// g.climateMockWithOutSenderReceived();
		stompg.climateMock();
	}

}

package com.fab.reactivesource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.fab.reactivesource.websocketclient.Generator;

@SpringBootApplication
public class ReactivesourceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ReactivesourceApplication.class, args);
		Generator g = context.getBean(Generator.class);
		g.climateMockWithOutSenderReceived();
	}

}

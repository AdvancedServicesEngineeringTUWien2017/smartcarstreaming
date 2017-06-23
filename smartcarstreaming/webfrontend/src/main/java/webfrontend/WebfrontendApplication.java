package webfrontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import webfrontend.Kafka.Receiver;

@SpringBootApplication
public class WebfrontendApplication {

	@Autowired
	Receiver receiver;

	public static void main(String[] args) {
		SpringApplication.run(WebfrontendApplication.class, args);
	}
}

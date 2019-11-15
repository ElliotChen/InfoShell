package tw.elliot.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class CliApplication {

	public static void main(String[] args) {
		log.info("START!");
		SpringApplication.run(CliApplication.class, args);
		log.info("END!");
	}

}

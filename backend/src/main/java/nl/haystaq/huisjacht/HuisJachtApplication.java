package nl.haystaq.huisjacht;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HuisJachtApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuisJachtApplication.class, args);
    }
}

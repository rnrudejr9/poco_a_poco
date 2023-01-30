package teamproject.pocoapoco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing
public class PocoapocoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocoapocoApplication.class, args);
    }

}

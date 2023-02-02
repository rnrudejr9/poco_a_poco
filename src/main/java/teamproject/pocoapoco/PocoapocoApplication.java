package teamproject.pocoapoco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class PocoapocoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocoapocoApplication.class, args);
    }

}

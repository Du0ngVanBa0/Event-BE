package DuongVanBao.event;

import DuongVanBao.event.config.EnvLoaderConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class EventApplication {

	public static void main(String[] args) {
		EnvLoaderConfig.load();
		SpringApplication.run(EventApplication.class, args);
	}

}

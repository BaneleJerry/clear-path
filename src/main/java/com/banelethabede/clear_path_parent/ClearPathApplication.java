package com.banelethabede.clear_path_parent;

import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ClearPathApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClearPathApplication.class, args);
	}

}

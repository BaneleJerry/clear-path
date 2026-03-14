package com.banelethabede.clear_path_parent;

import org.springframework.boot.SpringApplication;

public class TestClearPathApplication {

	public static void main(String[] args) {
		SpringApplication.from(ClearPathApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
